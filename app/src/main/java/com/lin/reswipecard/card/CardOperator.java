package com.lin.reswipecard.card;

import android.support.v7.widget.RecyclerView;

/**
 * Created by linchen on 2018/2/6.
 * mail: linchen@sogou-inc.com
 */

public class CardOperator<T> {
    private CardConfig mCardConfig;
    private RecyclerView mRecyclerView;
    private OnSwipeCardListener<T> mOnSwipeCardListener;

     CardOperator(CardConfig cardConfig,
                        RecyclerView recyclerView,
                        OnSwipeCardListener<T> onSwipeCardListener) {
        mCardConfig = cardConfig;
        if (mCardConfig == null) {
            mCardConfig=new DefaultCardConfig();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView == null) {
            throw new IllegalArgumentException("you must call attachRecyclerView() from Builder");
        }
        mOnSwipeCardListener = onSwipeCardListener;
        startAssembled();
    }

    private void startAssembled(){

    }

    public static class Builder<T> {

        private RecyclerView mRecyclerView;
        private CardConfig mConfig;
        private OnSwipeCardListener<T> mOnSwipeCardListener;

        public Builder attachRecyclerView(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            return this;
        }

        public Builder setCardConfig(CardConfig cardConfig) {
            mConfig = cardConfig;
            return this;
        }

        public Builder setSwipeListener(OnSwipeCardListener<T> listener) {
            mOnSwipeCardListener = listener;
            return this;
        }

        public CardOperator build() {
            return new CardOperator(mConfig, mRecyclerView, mOnSwipeCardListener);
        }
    }
}
