package com.lin.cardlib;

/**
 * @author yuqirong
 * modified by linchen
 */

public final class DefaultCardConfig extends CardConfig {

    public static final int DEFAULT_SHOW_ITEM = 3;

    public static final float DEFAULT_SCALE = 0.1f;

    public static final int DEFAULT_TRANSLATE_Y = 14;

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
    public int getCardTranslateDistance() {
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

    @Override
    public float getSwipeThreshold() {
        return 0.3f;
    }
}
