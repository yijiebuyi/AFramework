package com.callme.platform.widget.swipelistview;

import android.view.View;

public class BaseSwipeListViewListener implements SwipeListViewListener {
    @Override
    public void onOpened(int position, boolean toRight) {
    }

    @Override
    public void onClosed(int position, boolean fromRight) {
    }

    @Override
    public void onListChanged() {
    }

    @Override
    public void onMove(int position, float x) {
    }

    @Override
    public void onStartOpen(int position, int action, boolean right) {
    }

    @Override
    public void onStartClose(int position, boolean right) {
    }

    @Override
    public void onClickFrontView(View view, int position) {
    }

    @Override
    public void onClickBackView(View view, int position) {
    }

    @Override
    public void onDismiss(int[] reverseSortedPositions) {
    }

    @Override
    public void onItemDismiss(int pos) {
    }


    @Override
    public int onChangeSwipeMode(int position) {
        return SwipeListView.SWIPE_MODE_DEFAULT;
    }

    @Override
    public void onChoiceChanged(int position, boolean selected) {
    }

    @Override
    public void onChoiceStarted() {
    }

    @Override
    public void onChoiceEnded() {
    }

    @Override
    public void onFirstListItem() {
    }

    @Override
    public void onLastListItem() {
    }

    @Override
    public void onNextPageShown(boolean shown) {

    }

    @Override
    public void onItemLongClickListener(View view, int position) {

    }
}
