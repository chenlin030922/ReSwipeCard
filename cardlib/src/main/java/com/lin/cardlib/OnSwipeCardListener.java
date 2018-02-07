package com.lin.cardlib;

import android.support.v7.widget.RecyclerView;

/**
 * Created by linchen on 2018/2/6.
 * mail: linchen@sogou-inc.com
 */

public interface OnSwipeCardListener<T> {


    void onSwiping(RecyclerView.ViewHolder viewHolder, float dx,float dy, int direction);


    void onSwipedOut(RecyclerView.ViewHolder viewHolder, T t, int direction);


    void onSwipedClear();

}
