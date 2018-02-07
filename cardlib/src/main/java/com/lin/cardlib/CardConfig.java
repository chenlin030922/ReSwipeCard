package com.lin.cardlib;


import com.lin.cardlib.utils.ReItemTouchHelper;

/**
 * Created by linchen on 2018/2/6.
 * mail: linchen@sogou-inc.com
 */

public abstract class CardConfig {
    public static final int SWIPING_NONE = 0;
    public static final int SWIPED_LEFT = ReItemTouchHelper.LEFT;
    public static final int SWIPED_RIGHT = ReItemTouchHelper.RIGHT;
    public static final int SWIPED_UP = ReItemTouchHelper.UP;
    public static final int SWIPED_DOWN = ReItemTouchHelper.DOWN;

    public abstract int getShowCount();

    public abstract float getCardScale();


    public abstract int getCardTranslateDistance();

    public abstract float getCardRotateDegree();

    public abstract int getSwipeDirection();

    public abstract int couldSwipeOutDirection();

    public abstract float getSwipeThreshold();

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
