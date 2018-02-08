package com.lin.reswipecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DemoActivity extends AppCompatActivity {
    @BindView(R.id.tantan)
    Button mTantan;
    @BindView(R.id.fanyijun)
    Button mFanyijun;
    @BindView(R.id.normal)
    Button mNormal;

    @OnClick({R.id.tantan, R.id.fanyijun,R.id.normal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tantan:

                break;
            case R.id.fanyijun:
                break;
            case R.id.normal:
                Intent intent = new Intent(DemoActivity.this, NormalActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
    }
}
