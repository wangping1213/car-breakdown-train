package com.wp.car_breakdown_train.activity.tip;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.base.BaseActivity;

public class TipConnFailedActivity extends BaseActivity {


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_tip_conn_failed;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                TipConnFailedActivity.this.finish();
//            }
//        };
//
//        new Handler().postDelayed(runnable, 2000L);
    }


    public void closeTip(View view) {
        this.finish();
    }
}
