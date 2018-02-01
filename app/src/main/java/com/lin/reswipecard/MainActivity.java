package com.lin.reswipecard;

import com.lin.reswipecard.card.CardLayoutManager;
import com.lin.reswipecard.card.CardTouchHelperCallback;
import com.lin.reswipecard.card.utils.SogouItemTouchHelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        List<CardBean> list = CardMaker.initCards();
        CardTouchHelperCallback helperCallback = new CardTouchHelperCallback(mRecyclerView, list);
        SogouItemTouchHelper helper = new SogouItemTouchHelper(helperCallback);
        CardLayoutManager layoutManager = new CardLayoutManager(helper);
        mRecyclerView.setLayoutManager(layoutManager);
        CardAdapter cardAdapter = new CardAdapter(list);
        mRecyclerView.setAdapter(cardAdapter);
    }
}
