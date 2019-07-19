package com.callme.platform.widget.swipemenulistview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;


/**
 * author: zyl
 * time: 2016/8/30 10:25
 */
public class LoadMoreListView extends ListView {
    private boolean isAutoLoadMore;
    private boolean isLoadingMore;
    private OnLoadMoreListener listener;
    private SwipeRefreshLayout swipeView;

    public LoadMoreListView(Context context) {
        super(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int count = firstVisibleItem + visibleItemCount;
                if (count == totalItemCount && count != 0 && listener != null
                        && !isLoadingMore && isAutoLoadMore) {
                    setLoadingMore(true);
                    listener.onLoadMore();
                } else if (swipeView != null) {
                    View firstView = absListView.getChildAt(firstVisibleItem);

                    if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {// 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
                        swipeView.setEnabled(true);
                    } else {
                        swipeView.setEnabled(false);
                    }
                }

            }
        });
    }


    public void setSwipeRefreshLayout(SwipeRefreshLayout layout) {
        this.swipeView = layout;
    }

    /**
     * 设置是否支持自动加载更多
     *
     * @param autoLoadMore
     */
    public void setAutoLoadMoreEnable(boolean autoLoadMore) {
        isAutoLoadMore = autoLoadMore;
    }

    /**
     * 设置正在加载更多
     *
     * @param loadingMore
     */
    public void setLoadingMore(boolean loadingMore) {
        this.isLoadingMore = loadingMore;
    }

    /**
     * 加载更多监听
     *
     * @param listener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }
}
