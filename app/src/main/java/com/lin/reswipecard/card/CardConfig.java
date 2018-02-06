package com.lin.reswipecard.card;

import com.lin.reswipecard.card.utils.ReItemTouchHelper;

/**
 * Created by linchen on 2018/2/6.
 * mail: linchen@sogou-inc.com
 */

public abstract class CardConfig {
    /**
     * 卡片滑动时不偏左也不偏右
     */
    public static final int SWIPING_NONE = 0;
    public static final int SWIPED_LEFT = ReItemTouchHelper.LEFT;
    public static final int SWIPED_RIGHT = ReItemTouchHelper.RIGHT;
    public static final int SWIPED_UP = ReItemTouchHelper.UP;
    public static final int SWIPED_DOWN = ReItemTouchHelper.DOWN;

    public abstract int getShowCount();

    public abstract float getCardScale();


    public abstract int getCardTranslateY();

    public abstract float getCardRotateDegree();

    /**
     * 可以滑动的方向
     *
     * @return
     */
    public abstract int getSwipeDirection();

    /**
     * 可以滑出的方向
     *
     * @return
     */
    public abstract int couldSwipeOutDirection();

}
