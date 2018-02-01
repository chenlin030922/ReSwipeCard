package com.lin.reswipecard.card;


import com.lin.reswipecard.card.utils.SogouItemTouchHelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author yuqirong
 */

public class CardLayoutManager extends RecyclerView.LayoutManager {

    private final RecyclerView mRecyclerView;
    private final SogouItemTouchHelper mItemTouchHelper;

    public CardLayoutManager(@NonNull SogouItemTouchHelper itemTouchHelper) {
        this.mRecyclerView = itemTouchHelper.getRecyclerView();
        this.mItemTouchHelper = itemTouchHelper;
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        // 当数据源个数大于最大显示数时
        if (itemCount > CardConfig.DEFAULT_SHOW_ITEM) {
            for (int position = CardConfig.DEFAULT_SHOW_ITEM; position >= 0; position--) {
                View view = recycler.getViewForPosition(position);
                addView(view);
                if (position == CardConfig.DEFAULT_SHOW_ITEM) {
                    view.setAlpha(0);
                } else {
                    view.setAlpha(1);
                }
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                // recyclerview 布局
                /**
                 *   layoutDecoratedWithMargins(view, widthSpace / 2, 0,
                 widthSpace / 2 + getDecoratedMeasuredWidth(view),
                 heightSpace / 2 + getDecoratedMeasuredHeight(view)); 这里去掉上边距
                 */
                layoutDecoratedWithMargins(view,
                        widthSpace / 2, 0,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));

                if (position == CardConfig.DEFAULT_SHOW_ITEM) {
                    view.setScaleX(1 - (position - 1) * CardConfig.DEFAULT_SCALE);
                    view.setScaleY(1 - (position - 1) * CardConfig.DEFAULT_SCALE);
                    view.setTranslationY((position - 1) * view.getMeasuredHeight() / CardConfig.DEFAULT_TRANSLATE_Y);
                } else if (position > 0) {
                    view.setScaleX(1 - position * CardConfig.DEFAULT_SCALE);
                    view.setScaleY(1 - position * CardConfig.DEFAULT_SCALE);
                    view.setTranslationY(position * view.getMeasuredHeight() / CardConfig.DEFAULT_TRANSLATE_Y);
                } else {
                    if (view instanceof SwipeTouchLayout) {
                        ((SwipeTouchLayout) view).setSwipeTouchListener(mSwipeTouchListener);

                    }
                }

            }
        } else {
            // 当数据源个数小于或等于最大显示数时
            for (int position = itemCount - 1; position >= 0; position--) {
                View view = (SwipeTouchLayout) recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                // recyclerview 布局
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));

                if (position > 0) {
                    view.setScaleX(1 - position * CardConfig.DEFAULT_SCALE);
                    view.setScaleY(1 - position * CardConfig.DEFAULT_SCALE);
                    view.setTranslationY(position * view.getMeasuredHeight() / CardConfig.DEFAULT_TRANSLATE_Y);
                } else {
                    if (view instanceof SwipeTouchLayout) {
                        ((SwipeTouchLayout) view).setSwipeTouchListener(mSwipeTouchListener);
                    }
                }
            }
        }
    }

    private final SwipeTouchLayout.SwipeTouchListener mSwipeTouchListener = new SwipeTouchLayout.SwipeTouchListener() {
        @Override
        public void onTouchDown(MotionEvent event) {

        }

        @Override
        public void onTouchUp(MotionEvent event) {

        }

        @Override
        public void onTouchMove(View v, MotionEvent event) {
            RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(v);
            mItemTouchHelper.startSwipe(childViewHolder);
        }
    };

}