package com.lin.reswipecard;

import com.lin.reswipecard.card.CardLayoutManager;
import com.lin.reswipecard.card.CardTouchHelperCallback;
import com.lin.reswipecard.card.DefaultCardConfig;
import com.lin.reswipecard.card.OnSwipeCardListener;
import com.lin.reswipecard.card.utils.ReItemTouchHelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
        helperCallback.setOnSwipedListener(new OnSwipeCardListener() {
            @Override
            public void onSwiping(RecyclerView.ViewHolder viewHolder, float dx, float dy, int direction) {
                switch (direction) {
                    case ReItemTouchHelper.DOWN:
                        Log.e("aaa", "swiping direction=down");
                        break;
                    case ReItemTouchHelper.UP:
                        Log.e("aaa", "swiping direction=up");
                        break;
                    case ReItemTouchHelper.LEFT:
                        Log.e("aaa", "swiping direction=left");
                        break;
                    case ReItemTouchHelper.RIGHT:
                        Log.e("aaa", "swiping direction=right");
                        break;
                }
            }

            @Override
            public void onSwipedOut(RecyclerView.ViewHolder viewHolder, Object o, int direction) {
                switch (direction) {
                    case ReItemTouchHelper.DOWN:
                        Log.e("aaa", "swipe out direction=down");
                        break;
                    case ReItemTouchHelper.UP:
                        Log.e("aaa", "swipe out direction=up");
                        break;
                    case ReItemTouchHelper.LEFT:
                        Log.e("aaa", "swipe out direction=left");
                        break;
                    case ReItemTouchHelper.RIGHT:
                        Log.e("aaa", "swipe out direction=right");
                        break;
                }
            }

            @Override
            public void onSwipedClear() {

            }
        });
        ReItemTouchHelper helper = new ReItemTouchHelper(helperCallback);
        CardLayoutManager layoutManager = new CardLayoutManager(helper, new DefaultCardConfig());
        mRecyclerView.setLayoutManager(layoutManager);
        CardAdapter cardAdapter = new CardAdapter(list);
        mRecyclerView.setAdapter(cardAdapter);
    }
}
