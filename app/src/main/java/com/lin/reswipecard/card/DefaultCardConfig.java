package com.lin.reswipecard.card;

/**
 * @author yuqirong
 */

public final class DefaultCardConfig extends CardConfig {
    /**
     * 显示可见的卡片数量
     */
    public static final int DEFAULT_SHOW_ITEM = 3;
    /**
     * 默认缩放的比例
     */
    public static final float DEFAULT_SCALE = 0.1f;
    /**
     * 卡片Y轴偏移量时按照14等分计算
     */
    public static final int DEFAULT_TRANSLATE_Y = 14;
    /**
     * 卡片滑动时默认倾斜的角度
     */
    public static final float DEFAULT_ROTATE_DEGREE = 15f;


    @Override
    public int getShowCount() {
        return DEFAULT_SHOW_ITEM;
    }

    @Override
    public float getCardScale() {
        return DEFAULT_SCALE;
    }

    @Override
    public int getCardTranslateY() {
        return DEFAULT_TRANSLATE_Y;
    }

    @Override
    public float getCardRotateDegree() {
        return DEFAULT_ROTATE_DEGREE;
    }

    @Override
    public int getSwipeDirection() {
        return CardConfig.SWIPED_DOWN
                | CardConfig.SWIPED_UP
                | CardConfig.SWIPED_RIGHT
                | CardConfig.SWIPED_LEFT;
    }

    @Override
    public int couldSwipeOutDirection() {
        return CardConfig.SWIPED_DOWN
                | CardConfig.SWIPED_UP
                | CardConfig.SWIPED_RIGHT
                | CardConfig.SWIPED_LEFT;
    }
}
