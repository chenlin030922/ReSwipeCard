package com.lin.reswipecard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by linchen on 2018/1/26.
 * mail: linchen@sogou-inc.com
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {

    private List<CardBean> mCardBeanList;

    public CardAdapter(List<CardBean> cardBeanList) {
        mCardBeanList = cardBeanList;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_img_txt, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCardBeanList.size();
    }

    static class CardHolder extends RecyclerView.ViewHolder {
        public CardHolder(View itemView) {
            super(itemView);
        }
    }
}
