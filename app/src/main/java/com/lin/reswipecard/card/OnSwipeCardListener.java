package com.lin.reswipecard.card;

import android.support.v7.widget.RecyclerView;

/**
 * Created by linchen on 2018/2/6.
 * mail: linchen@sogou-inc.com
 */

public interface OnSwipeCardListener<T> {

    /**
     * 卡片还在滑动时回调
     *
     * @param viewHolder 该滑动卡片的viewHolder
     * @param direction  卡片滑动的方向
     */
    void onSwiping(RecyclerView.ViewHolder viewHolder, float dx,float dy, int direction);

    /**
     * 卡片完全滑出时回调
     *
     * @param viewHolder 该滑出卡片的viewHolder
     * @param t          该滑出卡片的数据
     * @param direction  卡片滑出的方向
     */
    void onSwipedOut(RecyclerView.ViewHolder viewHolder, T t, int direction);

    /**
     * 所有的卡片全部滑出时回调
     */
    void onSwipedClear();

}
