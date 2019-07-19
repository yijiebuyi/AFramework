package com.callme.platform.widget.crop;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.callme.platform.util.PermissionHelper;

import java.io.File;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：图片选择基类
 * 作者：huangyong
 * 创建时间：2018/1/24
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class BaseSelectionPictureActivity extends FragmentActivity implements CropConstants {
    //获取权限
    protected final int REQUEST_GALLERY_PERMISSION = 1000;
    protected static final int REQUEST_CAMERA_PERMISSION = 1001;

    //启动activity回调
    protected final int CHOOSE_IMAGE = 1; // 选择图片
    protected final int TAKE_PHOTO = 2; // 拍照
    protected final int CROP_IMAGE_REQUEST_CODE = 3; // 裁剪

    private PermissionHelper mPermissionHelper;

    @TargetApi(19)
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= 19; //Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumnPermissionGranted(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumnPermissionGranted(context, contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumnPermissionGranted(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri when permission is granted . This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    protected String getDataColumnPermissionGranted(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 拍照
     */
    protected void takePhoto() {
        try {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            String rationalePermission = PermissionHelper.shouldRationale(this, permissions);
            boolean hasGranted = PermissionHelper.hasGranted(this, permissions);
            if (!TextUtils.isEmpty(rationalePermission)) {
                PermissionHelper.showPermissionTipDialog(this, rationalePermission,
                        REQUEST_CAMERA_PERMISSION, true);
                return;
            }

            if (!hasGranted) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION);
            } else {
                enterCamera();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            finish();
        }
    }

    /**
     * 从图库选择照片
     */
    protected void pickPhoto() {
        try {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            String rationalePermission = PermissionHelper.shouldRationale(this, permissions);
            boolean hasGranted = PermissionHelper.hasGranted(this, permissions);
            if (!TextUtils.isEmpty(rationalePermission)) {
                PermissionHelper.showPermissionTipDialog(this, rationalePermission,
                        REQUEST_GALLERY_PERMISSION, true);
                return;
            }

            if (!hasGranted) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_GALLERY_PERMISSION);
            } else {
                entryGallery();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            finish();
        }
    }

    /**
     * 从图库选择照片
     */
    protected void entryGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CHOOSE_IMAGE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CHOOSE_IMAGE);
            } else {
                //如手机没有找到默认打开图片的应用(比如用户没有安装图库)，让用户自己选择打开图片的应用程序
                intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("*/*");
                startActivityForResult(intent, CHOOSE_IMAGE);
            }
        }
    }

    /**
     * 打開相机应用
     */
    protected void enterCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File outFile = new File(CropBusiness.generateCameraPicPath(this));
        if (Build.VERSION.SDK_INT >= 24) { //Build.VERSION_CODES.N: 24
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    getPackageName() + ".fileProvider", outFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
        }
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @TargetApi(23)
    private boolean shouldRationale(String[] permissions) {
        boolean rationale = false;
        for (String permission : permissions) {
            rationale = shouldShowRequestPermissionRationale(permission);
            if (rationale) {
                break;
            }
        }

        return rationale;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }

        switch (requestCode) {
            case REQUEST_GALLERY_PERMISSION:
                if (granted) {
                    entryGallery();
                } else {
                    onPermissionFail(requestCode, permissions, grantResults);
                }
                break;
            case REQUEST_CAMERA_PERMISSION:
                if (granted) {
                    enterCamera();
                } else {
                    onPermissionFail(requestCode, permissions, grantResults);
                }
                break;
        }
    }

    /**
     * 权限授权失败
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    protected void onPermissionFail(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults) {

    }
}
