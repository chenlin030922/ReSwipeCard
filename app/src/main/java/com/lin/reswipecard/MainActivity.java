package com.lin.reswipecard;


import com.lin.cardlib.CardConfig;
import com.lin.cardlib.CardLayoutManager;
import com.lin.cardlib.CardTouchHelperCallback;
import com.lin.cardlib.OnSwipeCardListener;
import com.lin.cardlib.utils.ReItemTouchHelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView mRecyclerView;
    Button mChangeBtn,mLeftBtn,mRightBtn;
    private BottomDialog mDialog;
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id) {
            case R.id.turn_left:
                break;
            case R.id.turn_right:
                break;
            case R.id.change_type:
                break;
        }
    }
    private void showDialog(){
        if (mDialog == null) {
            mDialog=new BottomDialog(this);
            View view= LayoutInflater.from(this).inflate(R.layout.layout_dialog,null);
            mDialog.setContentView(view);
        }
        mDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        List<CardBean> list = CardMaker.initCards();
        CardTouchHelperCallback helperCallback = new CardTouchHelperCallback(mRecyclerView, list, new CardConfig());
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
//                        Log.e("aaa", "swipe out direction=down");
                        Toast.makeText(MainActivity.this, "swipe down out", Toast.LENGTH_SHORT).show();
                        break;
                    case ReItemTouchHelper.UP:
//                        Log.e("aaa", "swipe out direction=up");
                        Toast.makeText(MainActivity.this, "swipe up out ", Toast.LENGTH_SHORT).show();
                        break;
                    case ReItemTouchHelper.LEFT:
//                        Log.e("aaa", "swipe out direction=left");
                        Toast.makeText(MainActivity.this, "swipe down left", Toast.LENGTH_SHORT).show();
                        break;
                    case ReItemTouchHelper.RIGHT:
                        Toast.makeText(MainActivity.this, "swipe right out", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onSwipedClear() {

            }
        });
        ReItemTouchHelper helper = new ReItemTouchHelper(helperCallback);
        CardLayoutManager layoutManager = new CardLayoutManager(helper, new CardConfig());
        mRecyclerView.setLayoutManager(layoutManager);
        CardAdapter cardAdapter = new CardAdapter(list);
        mRecyclerView.setAdapter(cardAdapter);
        mLeftBtn=findViewById(R.id.turn_left);
        mRightBtn=findViewById(R.id.turn_right);
        mChangeBtn=findViewById(R.id.change_type);
        mLeftBtn.setOnClickListener(this);
        mChangeBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
    }
}
