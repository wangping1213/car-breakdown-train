package com.wp.car_breakdown_train.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wp.car_breakdown_train.Constant;
import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.activity.tip.TipResetActivity;
import com.wp.car_breakdown_train.adapter.P4CarPinPartAdapter;
import com.wp.car_breakdown_train.application.MyApplication;
import com.wp.car_breakdown_train.base.BaseActivity;
import com.wp.car_breakdown_train.decoration.MySpaceItemDecoration;
import com.wp.car_breakdown_train.dialog.LoadingDialogUtils;
import com.wp.car_breakdown_train.entity.CarPart;
import com.wp.car_breakdown_train.entity.CarPartPin;
import com.wp.car_breakdown_train.holder.CommonViewHolder;
import com.wp.car_breakdown_train.receiver.NetworkChangeReceiver;
import com.wp.car_breakdown_train.udp.UdpSystem;
import com.wp.car_breakdown_train.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 第三页的activity
 *
 * @author wangping
 * @date 2018/6/24 17:22
 */
public class Page4Activity extends BaseActivity implements CommonViewHolder.onItemCommonClickListener {

    private static final String TAG = "wangping";
    private RecyclerView recycler_view_system;
    private TextView app_title_name;
    private MyApplication application;
    private boolean initFlag = false;
    private P4CarPinPartAdapter adapter;
    private NetworkChangeReceiver mReceiver;
    public static Map<Integer, Integer> checkboxMap = new HashMap<>();

    private List<CarPartPin> data = new ArrayList<>();


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_page4;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        final Dialog dialog = LoadingDialogUtils.createLoadingDialog(this, "加载中....");
        Log.d(TAG, String.format("page4-initView-start:%s", TimeUtil.getNowStrTime()));
        long start = System.currentTimeMillis();
        application = (MyApplication) this.getApplication();
        Glide.with(this).load(R.drawable.bg1).into((ImageView) findViewById(R.id.iv_bg));
        Glide.with(this).load(R.drawable.p4_icon_menu).into((ImageView) findViewById(R.id.iv_back));
        Glide.with(this).load(R.drawable.p3_icon_reset).into((ImageView) findViewById(R.id.iv_reset));
        recycler_view_system = (RecyclerView) findViewById(R.id.recycle_view_system);

        app_title_name = (TextView) findViewById(R.id.app_title_name);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_system.setLayoutManager(layoutManager);
//        Log.d(TAG, String.format("start:%s", TimeUtil.getNowStrTime()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                long start1 = System.currentTimeMillis();
                FutureTask<List<CarPartPin>> promise = initData();
                try {
                    List<CarPartPin> partPinList = promise.get();
                    setAdapter(partPinList);

                    long end1 = System.currentTimeMillis();
                    Log.d(TAG, String.format("getData waste time:%s", end1-start1));
                } catch (Exception e) {
                    Log.e(TAG, "initView:promise error!", e);
                }
                LoadingDialogUtils.closeDialog(dialog);
            }
        }, 1000);


//        CarPart carPart = (CarPart) this.getIntent().getSerializableExtra("carPart");
//        app_title_name.setText(carPart.getName());
//        if (null != carPart) {
//            data = carPart.getPinList();
//        }



        long end = System.currentTimeMillis();

        Log.d(TAG, String.format("all-time:%s", end-start));
    }


    private void initCheckBoxMap(List<CarPartPin> partPinList) {

    }

    private void setAdapter(List<CarPartPin> partPinList) {
        adapter = new P4CarPinPartAdapter(this, partPinList, this);
        recycler_view_system.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dimen_10_dip);
        recycler_view_system.addItemDecoration(new MySpaceItemDecoration(spacingInPixels));
    }


    private FutureTask<List<CarPartPin>> initData() {
        CarPart carPart = (CarPart) this.getIntent().getSerializableExtra("carPart");
        FutureTask<List<CarPartPin>> promise = null;
                app_title_name.setText(carPart.getName());
        if (null != carPart) {
            data = carPart.getPinList();

            Callable<List<CarPartPin>> callable = new Callable<List<CarPartPin>>() {
                @Override
                public List<CarPartPin> call() throws Exception {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    int customId = application.getCustomId();
                    try {
                        JSONObject obj = null;
                        Map<Integer, Integer> pointMap = application.getPointMap();
                        Set<Integer> aNumSet = pointMap.keySet();
                        int aNum = -1;
//                        Map<Integer, Integer> map = null;
                        for (Integer num : aNumSet) {
                            aNum = num;
                        }
//                        Log.d(TAG, String.format("page4, getState-start:%s", TimeUtil.getNowStrTime()));
                        UdpSystem.getThread().mySuspend();
                        for (int i=0; i<3; i++) {
                            try {
                                obj = UdpSystem.getState(customId, Constant.UDP_WAIT_TIME);
//                                String str = "{\"message\":\"getState\",\"result\":\"ok\",\"data\":{\"state\":[1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}}";
//                                JSONObject myObj = new JSONObject(str);
//                                obj = myObj.getJSONObject("data");

                                Log.d(TAG, String.format("search getState:%s", obj.toString()));
                                break;
                            } catch (Exception e) {
                                Thread.sleep(1000L);
                            }
                        }
                        UdpSystem.getThread().myResume();
//                        Log.d(TAG, String.format("page4, getState-end:%s", TimeUtil.getNowStrTime()));
                        JSONArray stateArr = obj.getJSONArray("state");
//                        map = new HashMap<>();
                        checkboxMap.clear();
                        for (int i = 0; i < stateArr.length(); i++) {
//                            map.put(i + 1, stateArr.getInt(i));
                            checkboxMap.put(i + 1, stateArr.getInt(i));
                        }
//                        if (pointMap.size() == 0 || pointMap.get(aNum) == map.get(aNum) || null == pointMap.get(aNum)) {
//                            Log.d(TAG, String.format("success, aNum:%s, aType:%s, mapType:%s", aNum, pointMap.get(aNum), map.get(aNum)));
//                        }

                        if (null != data) {
                            for (CarPartPin partPin : data) {
                                if (partPin.getAnum() != 0) {
                                    partPin.setCurrentType(checkboxMap.get(partPin.getAnum()));
                                }
                            }
                        }
                        initFlag = true;
                    } catch (Exception e) {
                        Log.e(TAG, "getState in page4 error!", e);
                    }

                    return data;
                }
            };

            promise = new FutureTask<List<CarPartPin>>(callable);

            new Thread(promise).start();

        }
        return promise;
    }



    @Override
    public void onItemClickListener(int position, View itemView) {
//        ImageView iv_p2_system = (ImageView) itemView.findViewById(R.id.iv_p2_system);
//        Glide.with(this).load(R.drawable.p3_list_bg_select).into(iv_p2_system);

    }

    @Override
    public void onItemLongClickListener(int position, View itemView) {

    }

    public void back(View view) {
        this.finish();
//        super.onCreate(null);
    }

    public void reset(View view) {
        Intent intent = new Intent(this, TipResetActivity.class);
        intent.putExtra("page", 4);
        this.startActivity(intent);
        reloadContent();
    }

    public void reloadContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<100; i++) {
                    try {
                        Thread.sleep(100L);
                    } catch (Exception e) {
                    }
                    if (application.getReloadFlag() == 1) {
                        application.setReloadFlag(0);
                        Intent intent = new Intent(Page4Activity.this, Page4Activity.class);
                        CarPart carPart = (CarPart) Page4Activity.this.getIntent().getSerializableExtra("carPart");
                        intent.putExtra("carPart", carPart);
                        Page4Activity.this.startActivity(intent);
                        Page4Activity.this.finish();
                        break;
                    }
                }
            }
        }).start();
    }

}
