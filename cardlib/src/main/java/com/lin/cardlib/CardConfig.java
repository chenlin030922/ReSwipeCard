package com.lin.cardlib;


import com.lin.cardlib.utils.ReItemTouchHelper;

/**
 * Created by linchen on 2018/2/6.
 * mail: linchen@sogou-inc.com
 */

public  class CardConfig {
    public static final int DEFAULT_SHOW_ITEM = 3;

    public static final float DEFAULT_SCALE = 0.1f;

    public static final int DEFAULT_TRANSLATE_Y = 14;

    public static final float DEFAULT_ROTATE_DEGREE = 15f;

    public static final int SWIPING_NONE = 0;
    public static final int SWIPED_LEFT = ReItemTouchHelper.LEFT;
    public static final int SWIPED_RIGHT = ReItemTouchHelper.RIGHT;
    public static final int SWIPED_UP = ReItemTouchHelper.UP;
    public static final int SWIPED_DOWN = ReItemTouchHelper.DOWN;

    public int getShowCount() {
        return DEFAULT_SHOW_ITEM;
    }

    public float getCardScale() {
        return DEFAULT_SCALE;
    }

    public int getCardTranslateDistance() {
        return DEFAULT_TRANSLATE_Y;
    }

    public float getCardRotateDegree() {
        return DEFAULT_ROTATE_DEGREE;
    }

    public int getSwipeDirection() {
        return CardConfig.SWIPED_DOWN
                | CardConfig.SWIPED_UP
                | CardConfig.SWIPED_RIGHT
                | CardConfig.SWIPED_LEFT;
    }

    public int couldSwipeOutDirection() {
        return CardConfig.SWIPED_DOWN
                | CardConfig.SWIPED_UP
                | CardConfig.SWIPED_RIGHT
                | CardConfig.SWIPED_LEFT;
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

}
