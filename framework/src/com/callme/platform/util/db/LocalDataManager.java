package com.callme.platform.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：本地数据库增删改查管理类
 * 作者：huangyong
 * 创建时间：2018/6/12
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class LocalDataManager extends SQLiteOpenHelper {
    protected SQLiteOpenHelper mDbHelper;
    protected List<Class<? extends Entry>> mTableCls;
    private IDataChangedListener mDataChangedListener;
    private HashMap<String, EntrySchema> mEntrySchemaMap;

    protected LocalDataManager(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
        mDbHelper = this;
        mEntrySchemaMap = new HashMap<String, EntrySchema>();
        mTableCls = getTableClazz();
    }

    protected EntrySchema getEntrySchema(Entry entry) {
        if (entry == null) {
            return null;
        }

        Class<? extends Entry> clazz = entry.getClass();
        return getEntrySchema(clazz);
    }

    protected EntrySchema getEntrySchema(Class<? extends Entry> clazz) {
        if (clazz == null) {
            return null;
        }

        String key = clazz.getSimpleName();
        EntrySchema schema = mEntrySchemaMap.get(key);
        if (schema == null) {
            schema = new EntrySchema(clazz);
            mEntrySchemaMap.put(key, schema);
        }

        return schema;
    }

    public long insert(Entry entry) {
        if (entry == null) {
            return -1;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        EntrySchema schema = getEntrySchema(entry);

        return schema.insertOrReplace(db, entry);
    }

    public void insert(List<? extends Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Entry firstEntry = entries.get(0);
            Class<? extends Entry> clazz = firstEntry.getClass();
            EntrySchema schema = getEntrySchema(firstEntry);

            for (Entry entry : entries) {
                schema.insertOrReplace(db, entry);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }

    }

    public void deleteAll(Class<? extends Entry> clazz) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        EntrySchema schema = getEntrySchema(clazz);
        schema.deleteAll(db);
    }

    public void deleteById(Class<? extends Entry> clazz, long id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        EntrySchema schema = getEntrySchema(clazz);
        schema.deleteWithId(db, id);
    }

    public void execSQL(String sql) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql);
    }

    public int deleteBySelectionArgs(Class<? extends Entry> clazz, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        EntrySchema schema = getEntrySchema(clazz);
        return schema.deleteBySelectionArgs(db, selection, selectionArgs);
    }

    public List<? extends Entry> queryAll(Class<? extends Entry> clazz) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        EntrySchema schema = getEntrySchema(clazz);

        Cursor cursor = schema.queryAll(db);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        ArrayList<Entry> entries = new ArrayList<Entry>();
        try {
            while (cursor.moveToNext()) {
                Entry entry = clazz.newInstance();
                schema.cursorToObject(cursor, entry);
                entries.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //close cursor
        cursor.close();

        return entries;
    }

    public List<? extends Entry> queryBySelectionArgs(Class<? extends Entry> clazz, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        EntrySchema schema = getEntrySchema(clazz);

        Cursor cursor = schema.queryBySelectionArgs(db, selection, selectionArgs);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        ArrayList<Entry> entries = new ArrayList<Entry>();
        try {
            while (cursor.moveToNext()) {
                Entry entry = clazz.newInstance();
                schema.cursorToObject(cursor, entry);
                entries.add(entry);
            }
        } catch (Exception e) {

        }

        //close cursor
        cursor.close();

        return entries;
    }

    public Entry queryWithId(long id, Class<? extends Entry> clazz) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        EntrySchema schema = getEntrySchema(clazz);

        Entry entry = null;
        try {
            entry = clazz.newInstance();
            boolean succ = schema.queryWithId(db, id, entry);
            if (!succ) {
                return null;
            }
        } catch (Exception e) {

        }

        return entry;
    }

    public int update(Entry entry, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table = EntrySchema.parseTableName(entry.getClass());
        return db.update(table, buildContentValues(entry), whereClause, whereArgs);
    }

    public void update(List<Entry> entries, String whereClause, String[] whereArgs) {
        for (Entry entry : entries) {
            update(entry, whereClause, whereArgs);
        }
    }

    public int updateEntryToDatabase(Entry entry) {
        if (entry == null) {
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table = EntrySchema.parseTableName(entry.getClass());
        String whereClause = "_id = ? ";
        String[] whereArgs = new String[]{String.valueOf(entry.id)};

        ContentValues values = buildContentValues(entry);

        int row = db.update(table, values, whereClause, whereArgs);
        return row;
    }

    public ContentValues buildContentValues(Entry entry) {
        ContentValues values = new ContentValues();
        EntrySchema schema = getEntrySchema(entry.getClass());
        schema.objectToValues(entry, values);
        if (entry.id == 0) {
            values.remove("_id");
        }
        return values;
    }

    public void createTables(Class<? extends Entry> clazz, SQLiteDatabase db) {
        EntrySchema schema = getEntrySchema(clazz);
        schema.createTables(db);
    }

    protected void dropTables(Class<? extends Entry> clazz, SQLiteDatabase db) {
        EntrySchema schema = getEntrySchema(clazz);
        schema.dropTables(db);
    }

    public IDataChangedListener getDataChangedListener() {
        return mDataChangedListener;
    }

    public void setDataChangedListener(IDataChangedListener listener) {
        mDataChangedListener = listener;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mTableCls != null && !mTableCls.isEmpty()) {
            for (Class<? extends Entry> tableCls : mTableCls) {
                createTables(tableCls, db);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mTableCls != null && !mTableCls.isEmpty()) {
            for (Class<? extends Entry> tableCls : mTableCls) {
                dropTables(tableCls, db);
            }

            onCreate(db);
        }
    }

    protected abstract List<Class<? extends Entry>> getTableClazz();
}
