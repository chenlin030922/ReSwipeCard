package com.lin.reswipecard;

import com.lin.reswipecard.card.CardLayoutManager;
import com.lin.reswipecard.card.CardTouchHelperCallback;
import com.lin.reswipecard.card.DefaultCardConfig;
import com.lin.reswipecard.card.utils.ReItemTouchHelper;

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
        CardTouchHelperCallback helperCallback = new CardTouchHelperCallback(mRecyclerView, list, new DefaultCardConfig());
        ReItemTouchHelper helper = new ReItemTouchHelper(helperCallback);
        CardLayoutManager layoutManager = new CardLayoutManager(helper, new DefaultCardConfig());
        mRecyclerView.setLayoutManager(layoutManager);
        CardAdapter cardAdapter = new CardAdapter(list);
        mRecyclerView.setAdapter(cardAdapter);
    }
}
