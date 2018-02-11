package com.lin.cardlib;


import com.lin.cardlib.utils.ReItemTouchHelper;

/**
 * Created by linchen on 2018/2/6.
 * mail: linchen@sogou-inc.com
 */

public class CardSetting {
    public static final int DEFAULT_SHOW_ITEM = 3;

    public static final float DEFAULT_SCALE = 0.1f;

    public static final int DEFAULT_TRANSLATE = 14;

    public static final float DEFAULT_ROTATE_DEGREE = 15f;
    private OnSwipeCardListener mListener;

    public int getShowCount() {
        return DEFAULT_SHOW_ITEM;
    }

    public float getCardScale() {
        return DEFAULT_SCALE;
    }

    public int getCardTranslateDistance() {
        return DEFAULT_TRANSLATE;
    }

    public float getCardRotateDegree() {
        return DEFAULT_ROTATE_DEGREE;
    }

    public int getSwipeDirection() {
        return ReItemTouchHelper.LEFT | ReItemTouchHelper.RIGHT
                | ReItemTouchHelper.UP | ReItemTouchHelper.DOWN;
    }

    public int couldSwipeOutDirection() {
        return ReItemTouchHelper.LEFT | ReItemTouchHelper.RIGHT
                | ReItemTouchHelper.UP | ReItemTouchHelper.DOWN;
    }

    public float getSwipeThreshold() {
        return 0.3f;
    }

    public boolean enableHardWare() {
        return true;
    }

    public boolean isLoopCard() {
        return true;
    }

    public int getSwipeOutAnimDuration() {
        return 400;
    }

    public int getStackDirection() {
        return ReItemTouchHelper.DOWN;
    }

    public void setSwipeListener(OnSwipeCardListener listener) {
        mListener = listener;
    }

    public OnSwipeCardListener getListener() {
        return mListener;
    }
}
