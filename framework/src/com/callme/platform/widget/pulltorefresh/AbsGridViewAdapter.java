package com.callme.platform.widget.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.util.CmRequestListener;
import com.callme.platform.util.ToastUtil;
import com.callme.platform.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.callme.platform.widget.stickylistheaders.StickyListHeadersAdapter;
import com.callme.platform.widget.stickylistheaders.StickyListHeadersListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <B> 数据对象bean
 * @param <H> item对象 holder
 * @author mikeyou 修改描述： 添加swipe list相关支持
 */
public abstract class AbsGridViewAdapter<B, H> extends BaseAdapter implements
        StickyListHeadersAdapter {

    private final int PAGE_SIZE = 20;
    /**
     * 初始状态
     */
    private final int STATE_NORMAL = 110;
    /**
     * 下拉刷新
     */
    private final int STATE_UP_REFRESH = 100;
    /**
     * 上拉加载
     */
    private final int STATE_DOWN_REFRESH = 101;
    /**
     * 下拉加载数据异常
     */
    private final int STATE_UP_ERROR = 102;
    /**
     * 上拉加载数据异常
     */
    private final int STATE_DOWN_ERROR = 103;
    /**
     * 所有数据为空
     */
    private final int STATE_ALL_EMPTY = 104;
    /**
     * 单次加载数据为空
     */
    private final int STATE_SINGLE_EMPTY = 105;
    /**
     * 获取数据参数错误
     */
    private final int STATE_PARAM_ERROR = 106;
    /**
     * 数据为空时加载数据
     */
    private final int STATE_LOADING = 107;
    /**
     * 数据为空时加载数据异常
     */
    private final int STATE_LOADING_ERROR = 108;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<B> mBeanList = new ArrayList<B>();
    protected int mCurrentPage = 0;
    protected boolean isLastPage = false;
    // protected boolean isAutoRequestButtom = true;
    protected PullToRefreshGridView mGridView;
    private int mCurrentState = STATE_NORMAL;

    private String mRequestId;

    private View mFailedView; // 失败View
    // 无数据提示界面相关数据
    private View mEmptyView;
    private int mEmptyIconResId; // 无数据图片ResId
    private int mEmptyTitleResId; // 无数据黑色字提示ResId
    private int mEmptyTipResId; // 无数据灰色提示ResId

    private String mEmptyTitle; // 无数据黑色字提示
    private String mEmptyTip; // 无数据灰色提示

    private SwipeDeleteListener mListener;
    private boolean mHasSeperatorWhenChoice = false;
    private String mSwipeText;

    private int mPageSize = PAGE_SIZE;

    private boolean mJudgeLastPageByNum = true;// 是否通过返回数量来判定是否是最后一页

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (STATE_UP_REFRESH == mCurrentState) {
                mBeanList.clear();
            }
            if (msg.obj != null) {
                onSuccess(msg.obj.toString());
            } else {
                addListData(null);
                onResponse();
            }
        }
    };
    private boolean mIsChoiceMode = false;

    public AbsGridViewAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public AbsGridViewAdapter(Context context, PullToRefreshGridView listView) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initGridView(listView);
    }

    public AbsGridViewAdapter(Context context, PullToRefreshGridView listView,
                              List<B> list) {
        this(context);
        if (null == listView) {
            throw new NullPointerException("listview is null");
        }
        if (null == list) {
            throw new NullPointerException("list data is null");
        }

        mBeanList.addAll(list);
        initGridView(listView);
    }

    public final void setSwipeDeleteListener(SwipeDeleteListener listener) {
        mListener = listener;
    }

    public final void setEmptyIcon(int emptyIcon) {
        mEmptyIconResId = emptyIcon;
    }

    public final void setEmptyTitle(int emptyTitle) {
        mEmptyTitleResId = emptyTitle;
    }

    public final void setEmptyTitle(String emptyTitle) {
        mEmptyTitle = emptyTitle;
    }

    public final void setEmptyTip(int emptyTip) {
        mEmptyTipResId = emptyTip;
    }

    public final void setEmptyTip(String emptyTip) {
        mEmptyTip = emptyTip;
    }

    public final View getEmptyView() {
        return mEmptyView;
    }

    public final void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public final View getFailedView() {
        return mFailedView;
    }

    public final boolean isChoiceMode() {
        return mIsChoiceMode;
    }

    public void setSwipeText(String text) {
        mSwipeText = text;
    }

    public void setHasSeperatorWhenChoice(boolean has) {
        mHasSeperatorWhenChoice = has;
    }

    /**
     * 设置ListItem的内容
     */
    protected abstract void setViewContent(H holder, B bean, int position);

    /**
     * 创建ListItem
     */
    protected View createItem(final int position) {
        if (mIsChoiceMode || mGridView != null) {
            View templateView = LayoutInflater.from(mContext).inflate(
                    R.layout.base_list_swipe_row, null);
            LinearLayout frontView = (LinearLayout) templateView
                    .findViewById(R.id.swipe_front);
            LinearLayout backView = (LinearLayout) templateView
                    .findViewById(R.id.swipe_back);
            if (mIsChoiceMode) {
                backView.setVisibility(View.GONE);
            }
            FrameLayout contentFrame = (FrameLayout) frontView
                    .findViewById(R.id.content);
            // 滑动删除list item的自定义区域
            View contentView = createContentItem(position);
            contentFrame.addView(contentView);
            TextView delete = (TextView) templateView.findViewById(R.id.delete);
            ImageView checkBtn = (ImageView) templateView
                    .findViewById(R.id.checkbox);
            templateView.findViewById(R.id.checkarea).setVisibility(
                    mIsChoiceMode ? View.VISIBLE : View.GONE);
            if (mHasSeperatorWhenChoice) {
                templateView.findViewById(R.id.top_divider).setVisibility(
                        View.VISIBLE);
                templateView.findViewById(R.id.bottom_divider).setVisibility(
                        View.VISIBLE);
            }

            setCheckListener(checkBtn, position);
            setDeleteListener(delete, position);

            final int height = getItemHeight();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) frontView
                    .getLayoutParams();
            if (height != 0) {
                lp.height = height;
                frontView.setLayoutParams(lp);
            }
            return templateView;
        } else {
            return createContentItem(position);
        }
    }

    protected int computeItemHeight(View content) {
        return 0;
    }

    protected int getItemHeight() {
        return 0;
    }

    private void setCheckListener(final ImageView iv, final int position) {
        if (iv != null && position >= 0) {
            if (mIsChoiceMode) {
                final GridView list = mGridView.getRefreshableView();
                iv.setVisibility(View.VISIBLE);
                /*
                 * final boolean isSelected = list.getSelected(position);
				 * iv.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View view) {
				 * list.unSelected(position); refreshCheckboxIcon(iv,
				 * list.getSelected(position)); } }); refreshCheckboxIcon(iv,
				 * isSelected);
				 */
            } else {
                iv.setVisibility(View.GONE);
            }
        }
    }

    private void refreshCheckboxIcon(final ImageView iv, boolean isSelected) {
//		if (isSelected) {
//			iv.setImageResource(R.drawable.checkbox_checked_icon);
//		} else {
//			iv.setImageResource(R.drawable.checkbox_no_checked_icon);
//		}
    }

    private void setDeleteListener(TextView delete, final int position) {
        if (delete != null && position >= 0) {
            // 有滑动删除时的公有逻辑
            delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (canDelete(position)) {
                        deleteItem(position);
                    }
                }
            });
            if (!TextUtils.isEmpty(mSwipeText)) {
                delete.setText(mSwipeText);
            }
        }
    }

    protected boolean canDelete(int pos) {
        if (mListener != null) {
            return mListener.canDelete(pos);
        }
        return true;
    }

    ;

    public final void deleteItem(final int position) {
        /*
         * final GridView list = mGridView.getRefreshableView();
		 * list.closeOpenedItems(); list.dismiss(position, new
		 * AnimatorListenerAdapter() {
		 * 
		 * @Override public void onAnimationEnd(Animator animation) {
		 * removeItem(position); } });
		 */
    }

    /**
     * 创建滑动删除item的内容区域
     *
     * @return
     */
    protected abstract View createContentItem(int position);

    /**
     * 初始化ViewHolder
     */
    protected abstract H initHolder(View view);

    /**
     * 此方法必须设置是否到最后一页
     *
     * @param json
     */
    protected abstract void onSuccess(String json);

    protected void onResponse() {

    }

    protected void onDataItemClick(int position, B bean) {

    }

    /**
     * 数据为空时，被调用。
     */
    protected void onDataEmpty() {

    }

    /**
     * 初始化ListView,如果已经初始化将忽略
     */
    private void initGridView(PullToRefreshGridView gridView) {
        if (null != mGridView) {
            return;
        }
        mGridView = gridView;
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onDataItemClick(position, mBeanList.get(position));
            }
        });

        mGridView.setFirstLoading(false);
        mGridView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridViewWithHeaderAndFooter>() {
                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
                        mCurrentPage = 0;
                        isLastPage = false;
                        mCurrentState = STATE_UP_REFRESH;
                        request();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<GridViewWithHeaderAndFooter> refreshView) {
                        if (mCurrentState != STATE_DOWN_REFRESH) {
                            mCurrentState = STATE_DOWN_REFRESH;
                            request();
                        }
                    }
                });
        SoundPullEventListener<StickyListHeadersListView> soundListener = new SoundPullEventListener<StickyListHeadersListView>(
                mContext);
        if (mGridView instanceof PullToRefreshGridView) {
            mGridView.setMode(Mode.BOTH);
            request();
        } else {
            mGridView.setMode(Mode.DISABLED);
        }
    }

    private void request() {
        if (isLastPage) {
            changeRequestStatus(STATE_NORMAL);
            return;
        }
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 如果所给的URL为空，直接报错。
                if (!autoLoad()) {
                    changeRequestStatus(STATE_PARAM_ERROR);
                    return;
                }
                if (mBeanList == null || mBeanList.size() == 0) {
                    changeRequestStatus(STATE_LOADING);
                }
                doRequest();
            }
        }, 500);
    }

    protected boolean autoLoad() {
        return true;
    }

    public void doRefresh() {
        mCurrentPage = 0;
        isLastPage = false;
        mCurrentState = STATE_NORMAL;
        changeRequestStatus(STATE_UP_REFRESH);
    }

    /**
     * 向服务端请求数据
     */
    protected void doRequest() {
        if (isLastPage) {
            return;
        }

        //TODO remove before request
        mRequestId = initRequest();
    }

    protected abstract String initRequest();

    protected CmRequestListener getListener() {
        return new CmRequestListener<JSONObject>() {
            @Override
            public void onReSendReq() {
                // 登录态过期后重新登录后再次发出请求
                request();
            }

            @Override
            public void onLoginTimeout() {
                if (mCurrentState == STATE_DOWN_REFRESH
                        || mCurrentState == STATE_UP_REFRESH
                        || mCurrentState == STATE_LOADING) {
                    changeRequestStatus(STATE_NORMAL);
                    mRequestId = null;
                }
            }

            @Override
            public void onResponse(JSONObject response) {
                mCurrentPage++;
                mRequestId = null;
                Object json = response;
                Message msg = Message.obtain();
                if (json instanceof JSONArray) {
                    if (json != null
                            && ((JSONArray) json).length() != 0) {
                        msg.obj = (json);
                    } else {
                        isLastPage = true;
                    }
                } else if (json instanceof JSONObject) {
                    if (json != null
                            && ((JSONObject) json).length() != 0) {
                        msg.obj = (json);
                    } else {
                        isLastPage = true;
                    }
                } else {
                    isLastPage = true;
                }
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                mRequestId = null;
                if (mCurrentState == STATE_DOWN_REFRESH) {
                    changeRequestStatus(STATE_DOWN_ERROR);
                } else if (mCurrentState == STATE_UP_REFRESH) {
                    changeRequestStatus(STATE_UP_ERROR);
                } else {
                    changeRequestStatus(STATE_LOADING_ERROR);
                }
                AbsGridViewAdapter.this.onFailure(errorCode, msg);
            }
        };
    }

    public boolean isNeedJsonObject() {
        return false;
    }

    /**
     * 向适配器添加数据,当网络请求完成的时候，调用这个函数，可以更新器状态和数据。
     *
     * @param list 数据
     * @param last 是否为最后一页。
     */
    private final void addListData(List<B> list, boolean last) {
        if (null == list || list.size() == 0) {
            mCurrentPage--;
            if (mBeanList.isEmpty()) {
                changeRequestStatus(STATE_ALL_EMPTY);
            } else {
                changeRequestStatus(STATE_SINGLE_EMPTY);
            }
            return;
        }

        isLastPage = last;
        // 只有当前有数据时，才刷新页面
        if (!list.isEmpty()) {
            mBeanList.addAll(list);
            notifyDataSetChanged();
        }

        // 总数据为空，显示数据为空回调
        if (mBeanList.isEmpty()) {
            onDataEmpty();
        }

        if (mBeanList == null || mBeanList.size() == 0) {
            changeRequestStatus(STATE_ALL_EMPTY);
        } else {
            changeRequestStatus(STATE_NORMAL);
        }

        if (isLastPage && mCurrentState != STATE_SINGLE_EMPTY) {
            changeRequestStatus(STATE_SINGLE_EMPTY);
        }
    }

    /**
     * 向适配器添加数据,当网络请求完成的时候，调用这个函数，可以更新器状态和数据。
     *
     * @param list 数据
     */
    public final void addListData(List<B> list) {
        boolean lastPage = false;
        if (mJudgeLastPageByNum) {
            if (list == null || list.size() < mPageSize) {
                lastPage = true;
            }
        }
        addListData(list, lastPage);
    }

    public final void setJudgeLastPageByNum(boolean byNum) {
        mJudgeLastPageByNum = byNum;
    }

    /**
     * 向列表尾部插入一条数据
     *
     * @param bean
     */
    public final void addItemBean(B bean) {
        if (bean != null) {
            mBeanList.add(bean);
            notifyDataSetChanged();
        }
    }

    /**
     * 手动设置数据，将之前的数据清空
     *
     * @param list
     * @param last
     */
    public final void setListData(List<B> list, boolean last) {
        if (list == null) {
            return;
        }
        mBeanList.clear();
        isLastPage = last;
        mBeanList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 重设数据内容。
     */
    public final void reset() {
        mBeanList.clear();
        mCurrentPage = 0;
        isLastPage = false;
        mCurrentState = STATE_NORMAL;
    }

    /**
     * 改变状态
     *
     * @param statusLoading
     */
    protected void changeRequestStatus(int statusLoading) {
        mCurrentState = statusLoading;
        switch (mCurrentState) {
            case STATE_NORMAL:
                mGridView.onRefreshComplete();
                mGridView.removeEmptyView(mEmptyView);
                if (!isLastPage && mGridView.getMode() == Mode.PULL_FROM_START) {
                    mGridView.setMode(Mode.BOTH);
                }
                break;
            case STATE_LOADING:
                mGridView.setFirstLoading(true);
                mGridView.setRefreshing();
                break;
            case STATE_LOADING_ERROR:
                onResponseFailed();
                mGridView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                addFailedView();
                break;
            case STATE_PARAM_ERROR:
                mGridView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_UP_REFRESH:
                mGridView.setRefreshing();
                break;
            case STATE_DOWN_REFRESH:

                break;
            case STATE_UP_ERROR:
                mGridView.onRefreshComplete();
                ToastUtil.showCustomViewToast(mContext, R.string.get_data_error);
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_DOWN_ERROR:
                mGridView.onRefreshComplete();
                if (mCurrentPage < 0)
                    mCurrentPage = 0;
                ToastUtil.showCustomViewToast(mContext, R.string.get_data_error);
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_ALL_EMPTY:
                isLastPage = true;
                mGridView.onRefreshComplete();
                mGridView.setMode(Mode.PULL_FROM_START);
                addEmptyView();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_SINGLE_EMPTY:
                isLastPage = true;
                mGridView.onRefreshComplete();
                mGridView.setMode(Mode.PULL_FROM_START);
                if (mBeanList == null || mBeanList.size() == 0) {
                    addEmptyView();
                } else if (mBeanList.size() > mPageSize) {
                    ToastUtil.showCustomViewToast(mContext, R.string.no_data);
                }
                break;
            default:
                break;
        }
    }

    public void onResponseFailed() {

    }

    public void onFailure(int errorCode, String msg) {

    }

    public final List<B> getAllList() {
        return mBeanList;
    }

    protected void addFailedView() {
        if (mFailedView == null) {
            mFailedView = mInflater.inflate(R.layout.base_failed_note, null);
            mFailedView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    doRefresh();
                }
            });
        }
        mGridView.setEmptyView(mFailedView);
    }

    protected void addEmptyView() {
        if (mEmptyView == null) {
            //同一个mGridView多次设置不同（new 出来的）空数据的Adapter，会导致mGridView添加多个emptyView
            //为了保证mGridView在设置新的Adapter中不会产生多个emptyView，所以需要删除
            View oldEmptyView = mGridView.getEmptyView();
            if (oldEmptyView != null) {
                mGridView.removeEmptyView(oldEmptyView);
            }

            mEmptyView = mInflater
                    .inflate(R.layout.base_pull_to_refresh_empty, null);
            ImageView icon = (ImageView) mEmptyView
                    .findViewById(R.id.empty_icon);
            TextView title = (TextView) mEmptyView
                    .findViewById(R.id.empty_title);
            TextView tip = (TextView) mEmptyView.findViewById(R.id.empty_tip);
            if (mEmptyIconResId > 0) {
                icon.setImageResource(mEmptyIconResId);
            } else if (mEmptyIconResId == -1) {
                icon.setVisibility(View.GONE);
            }


            if (mEmptyTitleResId > 0) {
                title.setText(mEmptyTitleResId);
            } else if (!TextUtils.isEmpty(mEmptyTitle)) {
                title.setText(mEmptyTitle);
            } else if (mEmptyTitleResId == -1 || TextUtils.isEmpty(mEmptyTitle)) {
                title.setVisibility(View.GONE);
            }

            if (mEmptyTipResId > 0) {
                tip.setText(mEmptyTipResId);
            } else if (!TextUtils.isEmpty(mEmptyTip)) {
                tip.setText(mEmptyTip);
            } else if (mEmptyTipResId == -1 || TextUtils.isEmpty(mEmptyTip)) {
                tip.setVisibility(View.GONE);
            }

            if (mEmptyIconResId == -1 && mEmptyTitleResId == -1 && mEmptyTipResId == -1) {
                mEmptyView.setVisibility(View.INVISIBLE);
            }
        }
        mGridView.setEmptyView(mEmptyView);
        onDataEmpty();
    }

    public final void setUrl() {
        reset();
        notifyDataSetChanged();
        mCurrentState = STATE_NORMAL;
        changeRequestStatus(STATE_LOADING);
    }

    @Override
    public int getCount() {
        return mBeanList.size();
    }

    @Override
    public B getItem(int position) {
        return mBeanList.get(position);
    }

    public void removeItem(int position) {
        if (position >= mBeanList.size()) {
            return;
        }
        mBeanList.remove(position);
        notifyDataSetChanged();
        if (mBeanList == null || mBeanList.size() == 0) {
            addEmptyView();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        H holder = null;
        if (view == null) {
            view = createItem(position);
            holder = initHolder(view);
            view.setTag(holder);
        } else {
            holder = (H) view.getTag();
            TextView delete = (TextView) view.findViewById(R.id.delete);
            setDeleteListener(delete, position);
            ImageView checkBtn = (ImageView) view.findViewById(R.id.checkbox);
            setCheckListener(checkBtn, position);
        }

        setViewContent(holder, getItem(position), position);
        return view;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new View(mContext);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    public interface SwipeDeleteListener {
        /**
         * list item是否能通过滑动出来的删除按钮来删除
         */
        public boolean canDelete(int pos);
    }
}
