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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.callme.platform.R;
import com.callme.platform.api.callback.ResultBean;
import com.callme.platform.api.listenter.RequestListener;
import com.callme.platform.util.ToastUtil;
import com.callme.platform.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.callme.platform.widget.stickylistheaders.StickyListHeadersAdapter;
import com.callme.platform.widget.stickylistheaders.StickyListHeadersListView;
import com.callme.platform.widget.swipelistview.BaseSwipeListViewListener;
import com.callme.platform.widget.swipelistview.SwipeListView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <B> 数据对象bean
 * @param <H> item对象 holder
 * @author mikeyou 修改描述： 添加swipe list相关支持
 */
public abstract class AbsSwipeAdapter<B, H> extends BaseAdapter implements
        StickyListHeadersAdapter {
    protected final int PAGE_SIZE = 10;
    protected Context mContext;
    protected LayoutInflater mInflater;

    protected List<B> mBeanList = new ArrayList<B>();

    protected int mCurrentPage = 1;

    protected boolean isLastPage = false;
    // protected boolean isAutoRequestButtom = true;
//    protected String mUrl;
//    protected RequestParams mParams;
    protected PullToRefreshSwipeListView mListView;

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

    private int mCurrentState = STATE_NORMAL;

    private String mRequestId;

    private View mFailedView; // 失败View
    // 无数据提示界面相关数据
    private View mEmptyView;
    private int mEmptyIconResId; // 无数据图片资源id
    private int mEmptyTitleResId; // 无数据黑色字提示资源id
    private int mEmptyTipResId; // 无数据灰色提示资源id

    private String mEmptyTitle; // 无数据黑色字提示
    private String mEmptyTip; // 无数据灰色提示

    private SwipeDeleteListener mListener;
    private boolean mHasSeperatorWhenChoice = false;
    private boolean mSwipeOpenOnLongPress = true; //当支持左右滑动时，是否支持长按打开swipe
    private String mSwipeText;

    private int mPageSize = PAGE_SIZE;

    private boolean mJudgeLastPageByNum = true;// 是否通过返回数量来判定是否是最后一页

    public final void setSwipeDeleteListener(SwipeDeleteListener listener) {
        mListener = listener;
    }

    public final void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public final void setEmptyIcon(int emptyIconResId) {
        mEmptyIconResId = emptyIconResId;
    }

    public final void setEmptyTip(String emptyTip) {
        mEmptyTip = emptyTip;
    }

    public final void setEmptyTip(int emptyTip) {
        mEmptyTipResId = emptyTip;
    }

    public final void setEmptyTitle(String emptyTitle) {
        mEmptyTitle = emptyTitle;
    }

    public final void setEmptyTitle(int emptyTitleResId) {
        mEmptyTitleResId = emptyTitleResId;
    }

    public final View getEmptyView() {
        return mEmptyView;
    }

    public final View getFailedView() {
        return mFailedView;
    }

    public final boolean isChoiceMode() {
        return mIsChoiceMode;
    }

    public final void enterChoiceMode() {
        mIsChoiceMode = true;
        if (mListView != null) {
            mListView.getRefreshableView().setChoiceMode(mIsChoiceMode);
        }
        notifyDataSetChanged();
    }

    public final void exitChoiceMode() {
        mIsChoiceMode = false;
        if (mListView != null) {
            mListView.getRefreshableView().setChoiceMode(mIsChoiceMode);
        }
        notifyDataSetChanged();
    }

    public final void reverseChoiceMode() {
        mIsChoiceMode = !mIsChoiceMode;
        if (mListView != null) {
            mListView.getRefreshableView().setChoiceMode(mIsChoiceMode);
        }
        notifyDataSetChanged();
    }

    public void setSwipeText(String text) {
        mSwipeText = text;
    }

    // 获取选中数据列表
    public final List<B> getCheckList() {
        List<B> list = new ArrayList<B>();
        List<Integer> positions = mListView.getRefreshableView()
                .getWrappedList().getPositionsSelected();
        if (positions != null && positions.size() > 0) {
            for (int i = 0; i < positions.size(); i++) {
                list.add(mBeanList.get(positions.get(i)));
            }
        }
        return list;
    }

    public void setHasSeperatorWhenChoice(boolean has) {
        mHasSeperatorWhenChoice = has;
    }

    /**
     * 设置ListItem的内容
     */
    protected abstract void setViewContent(H holder, B bean, int position);

    private boolean mIsChoiceMode = false;

    /**
     * 创建ListItem
     */
    protected View createItem(final int position) {
        if (mIsChoiceMode || mListView != null
                && mListView.getRefreshableView().isSwipeEnabled()) {
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
                final SwipeListView list = mListView.getRefreshableView()
                        .getWrappedList();
                iv.setVisibility(View.VISIBLE);
                final boolean isSelected = list.getSelected(position);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        list.unSelected(position);
                        refreshCheckboxIcon(iv, list.getSelected(position));
                    }
                });
                refreshCheckboxIcon(iv, isSelected);
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

    public final void deleteItem(final int position) {
        final SwipeListView list = mListView.getRefreshableView()
                .getWrappedList();
        list.closeOpenedItems();
        list.dismiss(position, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeItem(position);
            }
        });
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
     */
    protected abstract void onSuccess(ResultBean data);

    protected void onResponse() {

    }


    protected void onDataItemClick(View view, int position, B bean) {

    }

    protected void onDataItemLongClick(View view, int position, B bean) {

    }

    /**
     * 数据为空时，被调用。
     */
    protected void onDataEmpty() {

    }

    public AbsSwipeAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public AbsSwipeAdapter(Context context, PullToRefreshSwipeListView listView) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initListView(listView);
    }

    public AbsSwipeAdapter(Context context,
                           PullToRefreshSwipeListView listView, List<B> list) {
        this(context);
        if (null == listView) {
            throw new NullPointerException("listview is null");
        }
        if (null == list) {
            throw new NullPointerException("list data is null");
        }
        mBeanList.addAll(list);
        initListView(listView);
    }

    /**
     * 初始化ListView,如果已经初始化将忽略
     * 如果支持Swipe时，若用戶需要屏蔽长按时间打开滑动操作，需要设置setSwipeOpenOnLongPress为false
     *
     * @see SwipeListView#setSwipeOpenOnLongPress
     */
    private void initListView(PullToRefreshSwipeListView listView) {
        if (null != mListView) {
            return;
        }
        mListView = listView;
        final SwipeListView list = mListView.getRefreshableView()
                .getWrappedList();
        if (list.isSwipeEnabled()) {
            //支持左滑右滑动操作
            list.setSwipeOpenOnLongPress(mSwipeOpenOnLongPress);
            list.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onClickFrontView(View view, int position) {
                    int idx = position;
                    if (list.getHeaderViewsCount() > 0 || list.getFooterViewsCount() > 0) {
                        // 由于设置headView，位置要减
                        idx = position - list.getHeaderViewsCount();
                    }
                    if (idx >= 0 && idx < mBeanList.size()) {
                        onDataItemClick(view, position, mBeanList.get(idx));
                    }
                }

                @Override
                public void onItemLongClickListener(View view, int position) {

                    int idx = position;
                    if (list.getHeaderViewsCount() > 0 || list.getFooterViewsCount() > 0) {
                        // 由于设置headView，位置要减
                        idx = position - list.getHeaderViewsCount();
                    }
                    if (idx >= 0 && idx < mBeanList.size()) {
                        onDataItemLongClick(view, position, mBeanList.get(idx));
                    }
                }
            });
        } else {
            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    int idx = position;
                    if (list.getHeaderViewsCount() > 0 || list.getFooterViewsCount() > 0) {
                        // 由于设置headView，位置要减
                        idx = position - list.getHeaderViewsCount();
                    }
                    if (idx >= 0 && idx < mBeanList.size()) {
                        onDataItemClick(view, position, mBeanList.get(idx));
                    }
                }
            });

            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    int idx = position;
                    if (list.getHeaderViewsCount() > 0 || list.getFooterViewsCount() > 0) {
                        // 由于设置headView，位置要减
                        idx = position - list.getHeaderViewsCount();
                    }
                    if (idx >= 0 && idx < mBeanList.size()) {
                        onDataItemLongClick(view, position, mBeanList.get(idx));
                    }
                    return true;
                }
            });
        }
        mListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StickyListHeadersListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<StickyListHeadersListView> refreshView) {
                        mCurrentPage = 1;
                        isLastPage = false;
                        mCurrentState = STATE_UP_REFRESH;
                        request();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<StickyListHeadersListView> refreshView) {
                        if (mCurrentState != STATE_DOWN_REFRESH) {
                            mCurrentState = STATE_DOWN_REFRESH;
                            request();
                        }
                    }
                });
        SoundPullEventListener<StickyListHeadersListView> soundListener = new SoundPullEventListener<StickyListHeadersListView>(
                mContext);
//		soundListener.addSoundEvent(State.REFRESHING, R.raw.refresh);
        mListView.setOnPullEventListener(soundListener);
        if (mListView instanceof PullToRefreshSwipeListView) {
            mListView.setMode(Mode.BOTH);
            request();
        } else {
            mListView.setMode(Mode.DISABLED);
        }
    }

    protected boolean autoLoad() {
        return true;
    }

    private void request() {
        if (isLastPage) {
            changeRequestStatus(STATE_NORMAL);
            return;
        }
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
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

    public void doRefresh() {
        mCurrentPage = 1;
        isLastPage = false;
        mCurrentState = STATE_NORMAL;
        changeRequestStatus(STATE_UP_REFRESH);
    }

    /**
     * 向服务端请求数据
     */
    protected void doRequest() {
        if (isLastPage/* || TextUtils.isEmpty(mUrl)*/) {
            return;
        }

        //TODO remove before request
//        mParams.put("page", mCurrentPage);
        mRequestId = initRequest();
    }

    protected abstract String initRequest();

    protected RequestListener getListener() {
        return new RequestListener<ResultBean>() {

            @Override
            public void onSuccess(ResultBean response) {
                mCurrentPage++;
                mRequestId = null;
                List json = response.rows;
                Message msg = Message.obtain();
                if (json != null && json.size() > 0) {
                    msg.obj = response;
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
                AbsSwipeAdapter.this.onFailure(errorCode, msg);
            }
        };
    }

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (STATE_UP_REFRESH == mCurrentState) {
                mBeanList.clear();
            }
            if (msg.obj != null && msg.obj instanceof ResultBean) {
                onSuccess((ResultBean) msg.obj);
            } else {
                addListData(null);
                onResponse();
            }
        }
    };

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
        // 不根据返回数据的数量来决定是否是最后一页
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
        mCurrentPage = 1;
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
                mListView.onRefreshComplete();
                mListView.removeEmptyView(mEmptyView);
                if (!isLastPage && mListView.getMode() == Mode.PULL_FROM_START) {
                    mListView.setMode(Mode.BOTH);
                }
                break;
            case STATE_LOADING:
                mListView.setFirstLoading(true);
                mListView.setRefreshing();
                break;
            case STATE_LOADING_ERROR:
                onReponseFailed();
                mListView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                addFailedView();
                break;
            case STATE_PARAM_ERROR:
                mListView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_UP_REFRESH:
                mListView.setRefreshing();
                break;
            case STATE_DOWN_REFRESH:

                break;
            case STATE_UP_ERROR:
                mListView.onRefreshComplete();
                ToastUtil.showCustomViewToast(mContext, R.string.get_data_error);
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_DOWN_ERROR:
                mListView.onRefreshComplete();
                if (mCurrentPage < 1)
                    mCurrentPage = 1;
                ToastUtil.showCustomViewToast(mContext, R.string.get_data_error);
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_ALL_EMPTY:
                isLastPage = true;
                mListView.onRefreshComplete();
                mListView.setMode(Mode.PULL_FROM_START);
                addEmptyView();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_SINGLE_EMPTY:
                isLastPage = true;
                mListView.onRefreshComplete();
                mListView.setMode(Mode.PULL_FROM_START);
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

    public void onReponseFailed() {

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
        mListView.setEmptyView(mFailedView);
    }

    protected void addEmptyView() {
        if (mEmptyView == null) {
            //同一个listView多次设置不同（new 出来的）空数据的Adapter，会导致listView添加多个emptyView
            //为了保证listView在设置新的Adapter中不会产生多个emptyView，所以需要删除
            View oldEmptyView = mListView.getEmptyView();
            if (oldEmptyView != null) {
                mListView.removeEmptyView(oldEmptyView);
            }

            mEmptyView = mInflater
                    .inflate(R.layout.base_pull_to_refresh_empty, null);
        }
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
        mListView.setEmptyView(mEmptyView);
        onDataEmpty();
    }

    public final void setUrl(String url) {
        reset();
        notifyDataSetChanged();
        mCurrentState = STATE_NORMAL;
//        mUrl = url;
        changeRequestStatus(STATE_LOADING);
    }

    @Override
    public int getCount() {
        return mBeanList.size();
    }

    @Override
    public B getItem(int position) {
        if (position >= mBeanList.size()) {
            return mBeanList.get(mBeanList.size() - 1);
        } else if (position < 0) {
            return mBeanList.get(0);
        }
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

    public interface SwipeDeleteListener {
        /**
         * list item是否能通过滑动出来的删除按钮来删除
         */
        public boolean canDelete(int pos);
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


    /**
     * 当支持左右滑动时，是否支持长按打开swipe，如果不支持，长按事件就回调onDataItemLongClick(T data, int pos)
     *
     * @param swipeOpenOnLongPress
     */
    protected void setSwipeOpenOnLongPressed(boolean swipeOpenOnLongPress) {
        mSwipeOpenOnLongPress = swipeOpenOnLongPress;
    }

}
