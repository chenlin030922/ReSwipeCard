package com.lin.cardlib;


import com.lin.cardlib.utils.ReItemTouchHelper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * @author yuqirong
 *         modified by linchen
 */

public class CardTouchHelperCallback<T> extends ReItemTouchHelper.Callback {

    private final RecyclerView mRecyclerView;
    private final List<T> mList;
    private OnSwipeCardListener<T> mListener;
    private CardSetting mConfig;

    /**
     * The variable controls the button {@link ReItemTouchHelper#swipeManually(int)}
     * Allows not to use the button ahead of time
     */
    private boolean switchListener = true;

    public CardTouchHelperCallback(@NonNull RecyclerView recyclerView, @NonNull List<T> dataList, CardSetting cardConfig) {
        this.mRecyclerView = recyclerView;
        this.mList = dataList;
        mConfig = cardConfig;
        mListener = mConfig.getListener();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof CardLayoutManager) {
            swipeFlags = mConfig.getSwipeDirection();
        }
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public boolean isForbidUpSwipeOut() {
        return (mConfig.couldSwipeOutDirection() & ReItemTouchHelper.UP)
                != ReItemTouchHelper.UP;
    }

    @Override
    public boolean isForbidDownSwipeOut() {
        return (mConfig.couldSwipeOutDirection() &
                ReItemTouchHelper.DOWN) != ReItemTouchHelper.DOWN;
    }

    @Override
    public boolean isForbidLeftSwipeOut() {
        return (mConfig.couldSwipeOutDirection() &
                ReItemTouchHelper.LEFT) != ReItemTouchHelper.LEFT;
    }

    @Override
    public boolean isForbidRightSwipeOut() {
        return (mConfig.couldSwipeOutDirection() &
                ReItemTouchHelper.RIGHT) != ReItemTouchHelper.RIGHT;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder.itemView instanceof SwipeTouchLayout) {
            ((SwipeTouchLayout) viewHolder.itemView).setSwipeTouchListener(null);
        } else {
            viewHolder.itemView.setOnTouchListener(null);
        }
        int layoutPosition = viewHolder.getLayoutPosition();
        T remove = mList.remove(layoutPosition);
        switchListener = true;
        if (mConfig.isLoopCard()) {
            mList.add(remove);
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
        if (mListener != null) {
            mListener.onSwipedOut(viewHolder, remove, direction);
        }
        if (mRecyclerView.getAdapter().getItemCount() == 0) {
            if (mListener != null) {
                mListener.onSwipedClear();
            }
        }
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
//        if (actionState == ReItemTouchHelper.ACTION_STATE_SWIPE) {
        float ratio;
        float ratioX = dX / getXThreshold(recyclerView);
        int direction;
        if (Math.abs(dX) > Math.abs(dY)) {
            ratio = ratioX;
            if (dX > 0) {
                direction = ReItemTouchHelper.RIGHT;
            } else {
                direction = ReItemTouchHelper.LEFT;
            }
        } else {
            ratio = dY / getYThreshold(recyclerView);
            if (dY > 0) {
                direction = ReItemTouchHelper.DOWN;
            } else {
                direction = ReItemTouchHelper.UP;
            }
        }
        if (ratio > 1) {
            ratio = 1;
        } else if (ratio < -1) {
            ratio = -1;
        }
        if (ratioX > 1) {
            ratioX = 1;
        } else if (ratioX < -1) {
            ratioX = -1;
        }
        itemView.setRotation(ratioX * mConfig.getCardRotateDegree());
        int childCount = recyclerView.getChildCount();
        float defaultScale = mConfig.getCardScale();
        if (childCount > mConfig.getShowCount()) {
            for (int position = 1; position < childCount - 1; position++) {
                int index = childCount - position - 1;
                float scale = 1 - index * defaultScale + Math.abs(ratio) * defaultScale;
                View view = recyclerView.getChildAt(position);
                view.setScaleX(scale);
                view.setScaleY(scale);
                switch (mConfig.getStackDirection()) {
                    case ReItemTouchHelper.UP:
                        view.setTranslationY(-(index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                        break;
                    case ReItemTouchHelper.RIGHT:
                        view.setTranslationX((index - Math.abs(ratio)) * itemView.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                        break;
                    case ReItemTouchHelper.LEFT:
                        view.setTranslationX(-(index - Math.abs(ratio)) * itemView.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                        break;
                    case ReItemTouchHelper.DOWN:
                    default:
                        view.setTranslationY((index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                        break;
                }
            }
        } else {
            for (int position = 0; position < childCount - 1; position++) {
                int index = childCount - position - 1;
                View view = recyclerView.getChildAt(position);
                float scale = 1 - index * defaultScale + Math.abs(ratio) * defaultScale;
                view.setScaleX(scale);
                view.setScaleY(scale);
                switch (mConfig.getStackDirection()) {
                    case ReItemTouchHelper.UP:
                        view.setTranslationY(-(index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                        break;
                    case ReItemTouchHelper.RIGHT:
                        view.setTranslationX((index - Math.abs(ratio)) * itemView.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                        break;
                    case ReItemTouchHelper.LEFT:
                        view.setTranslationX(-(index - Math.abs(ratio)) * itemView.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                        break;
                    case ReItemTouchHelper.DOWN:
                    default:
                        view.setTranslationY((index - Math.abs(ratio)) * itemView.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                        break;
                }
            }
        }
        if (mListener != null && ratio != 0) {
            mListener.onSwiping(viewHolder, dX, dY, direction);
        }
    }

    @Override
    public void setSwitchListener(boolean switchListener) {
        this.switchListener = switchListener;
    }

    @Override
    public boolean getSwitchListener() {
        return switchListener;
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return mConfig.getSwipeThreshold();
    }

    @Override
    public boolean enableHardWare() {
        return mConfig.enableHardWare();
    }

    @Override
    public int getDefaultSwipeAnimationDuration() {
        return mConfig.getSwipeOutAnimDuration();
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0f);
    }

    private float getXThreshold(RecyclerView recyclerView) {
        return recyclerView.getWidth() * 0.4f;
    }

    private float getYThreshold(RecyclerView recyclerView) {
        return getXThreshold(recyclerView);
    }
}
