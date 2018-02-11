package com.lin.cardlib;


import com.lin.cardlib.utils.ReItemTouchHelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * @author yuqirong
 *         modified by linchen
 */

public class CardLayoutManager extends RecyclerView.LayoutManager {

    private final RecyclerView mRecyclerView;
    private final ReItemTouchHelper mItemTouchHelper;
    private CardSetting mConfig;

    public CardLayoutManager(@NonNull ReItemTouchHelper itemTouchHelper, CardSetting cardConfig) {
        this.mRecyclerView = itemTouchHelper.getRecyclerView();
        this.mItemTouchHelper = itemTouchHelper;
        mConfig = cardConfig;
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        int showCount = mConfig.getShowCount();
        float defaultScale = mConfig.getCardScale();
        if (itemCount > showCount) {
            for (int position = showCount; position >= 0; position--) {
                View layout = recycler.getViewForPosition(position);
                if (!(layout instanceof SwipeTouchLayout)) {
                    throw new IllegalArgumentException("pls use SwipeTouchLayout as root on your item xml");
                }
                layout.setClickable(true);
                addView(layout);
                measureChildWithMargins(layout, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(layout);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(layout);
                layoutDecoratedWithMargins(layout,
                        widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(layout),
                        heightSpace / 2 + getDecoratedMeasuredHeight(layout));

                if (position == showCount) {
                    layout.setScaleX(1 - (position - 1) * defaultScale);
                    layout.setScaleY(1 - (position - 1) * defaultScale);
                    switch (mConfig.getStackDirection()) {
                        case ReItemTouchHelper.UP:
                            layout.setTranslationY(-(position - 1) * layout.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.RIGHT:
                            layout.setTranslationX((position - 1) * layout.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.LEFT:
                            layout.setTranslationX(-(position - 1) * layout.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.DOWN:
                        default:
                            layout.setTranslationY((position - 1) * layout.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                    }
                } else if (position > 0) {
                    layout.setScaleX(1 - position * defaultScale);
                    layout.setScaleY(1 - position * defaultScale);
                    switch (mConfig.getStackDirection()) {
                        case ReItemTouchHelper.UP:
                            layout.setTranslationY(-position * layout.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.RIGHT:
                            layout.setTranslationX(position * layout.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.LEFT:
                            layout.setTranslationX(-position * layout.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.DOWN:
                        default:
                            layout.setTranslationY(position * layout.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                    }
                } else {
                    ((SwipeTouchLayout)layout).setSwipeTouchListener(mSwipeTouchListener);
                }
            }
        } else {
            for (int position = itemCount - 1; position >= 0; position--) {
                View view = recycler.getViewForPosition(position);
                view.setClickable(true);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));

                if (position > 0) {
                    view.setScaleX(1 - position * defaultScale);
                    view.setScaleY(1 - position * defaultScale);
                    switch (mConfig.getStackDirection()) {
                        case ReItemTouchHelper.UP:
                            view.setTranslationY(-position * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.RIGHT:
                            view.setTranslationX(position * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.LEFT:
                            view.setTranslationX(-position * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.DOWN:
                        default:
                            view.setTranslationY(position * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                    }
                } else {
                    if (view instanceof SwipeTouchLayout) {
                        ((SwipeTouchLayout) view).setSwipeTouchListener(mSwipeTouchListener);
                    } else {
                        view.setOnTouchListener(mOnTouchListener);
                    }
                }
            }
        }
    }


    private float touchDownX;
    private float touchDownY;
    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(v);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    boolean needSwipe = (Math.abs(touchDownX - event.getX()) >= ViewConfiguration.get(
                            mRecyclerView.getContext()).getScaledTouchSlop())
                            || (Math.abs(touchDownY - event.getY()) >= ViewConfiguration.get(
                            mRecyclerView.getContext()).getScaledTouchSlop());
                    if (needSwipe) {
                        mItemTouchHelper.startSwipe(childViewHolder);
                        return false;
                    }
                    return true;
            }
            return v.onTouchEvent(event);
        }
    };
    private SwipeTouchLayout.SwipeTouchListener mSwipeTouchListener = new SwipeTouchLayout.SwipeTouchListener() {
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