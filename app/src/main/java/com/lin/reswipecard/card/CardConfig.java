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


    public abstract int getCardTranslateDistance();

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


    /**
     * {@link ReItemTouchHelper.Callback.getSwipeThreshold(ViewHolder viewHolder)}
     *
     * @return
     */
    public abstract float getSwipeThreshold();


    /**
     * 默认硬件加速
     * @return
     */
    public boolean enableHardWare() {
        return true;
    }

    /**
     * 默认k循环播放
     * @return
     */
    public boolean isLoopCard() {
        return true;
    }

    /**
     * 手势抬起后的动画执行时间
     * @return
     */
    public int getSwipeOutAnimDuration(){
        return -1;
    }

    /**
     * 四种堆叠方式，默认为下
     * ReItemTouchHelper.DOWN 下
     * ReItemTouchHelper.UP 上
     * ReItemTouchHelper.LEFT 左
     * ReItemTouchHelper.RIGHT 右
     * @return
     */
    public int getStackDirection(){
        return ReItemTouchHelper.DOWN;
    }

}
