package com.wp.car_breakdown_train.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.wp.car_breakdown_train.view.MarqueeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
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
    private Handler myHandler;
    private long time;

    private List<CarPartPin> data = new ArrayList<>();


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_page4;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        final long page4Time = System.currentTimeMillis();
        Log.d(TAG, String.format("page4-initView-start:%s", TimeUtil.getNowStrTime()));
        final long start = System.currentTimeMillis();
        application = (MyApplication) this.getApplication();
        Glide.with(this).load(R.drawable.bg1).into((ImageView) findViewById(R.id.iv_bg));
        Glide.with(this).load(R.drawable.p4_icon_menu).into((ImageView) findViewById(R.id.iv_back));
        Glide.with(this).load(R.drawable.p3_icon_reset).into((ImageView) findViewById(R.id.iv_reset));
        recycler_view_system = (RecyclerView) findViewById(R.id.recycle_view_system);

        app_title_name = (TextView) findViewById(R.id.app_title_name);

        initData2();
        setAdapter(data);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_system.setLayoutManager(layoutManager);
//        Log.d(TAG, String.format("start:%s", TimeUtil.getNowStrTime()));

        final Dialog dialog = LoadingDialogUtils.createLoadingDialog(this, "加载中....");
        if (null != dialog) application.getMap().put("dialog", dialog);
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Bundle bundle = msg.getData();
                        long startTime = page4Time;
                        int pos = -1;
                        if (null != bundle) {
                            startTime = bundle.getLong("page4Time");
                            pos = bundle.getInt("position");
                        }
                        refreshData((Dialog) application.getMap().get("dialog"), startTime, pos);
                        break;
                }
            }
        };
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMsg(null);
            }
        }, 1000L);
    }

    /**
     * 发送消息
     * @param bundle
     */
    public void sendMsg(Bundle bundle) {
        Message msg = myHandler.obtainMessage();
        msg.what = 1;
        if (null != bundle) msg.setData(bundle);
        msg.sendToTarget();
    }

    private void refreshData(Dialog dialog, long page4Time, int pos) {
        Log.d(TAG, String.format("stateTime:%s, page4Time:%s", application.getStateTime(), page4Time));
        for (int i=0; i<20; i++) {
            if (application.getStateTime() >= page4Time) {
                checkboxMap.clear();
                checkboxMap.putAll(application.getPointMap());
                if (pos != -1) {
                    adapter.notifyItemChanged(pos, 1);
                } else {
                    for (int j = 0; j < data.size(); j++) {
                        adapter.notifyItemChanged(j, 1);
                    }
                }
                break;
            } else {
                try {
                    Thread.sleep(Constant.UDP_WAIT_TIME);
                } catch (InterruptedException e) {
                    Log.e(TAG, "sleep error!", e);
                }
            }
        }

        if (null != dialog) LoadingDialogUtils.closeDialog(dialog);
    }


    private void initCheckBoxMap(List<CarPartPin> partPinList) {

    }

    private void setAdapter(List<CarPartPin> partPinList) {
        adapter = new P4CarPinPartAdapter(this, partPinList, this);
        recycler_view_system.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dimen_10_dip);
        recycler_view_system.addItemDecoration(new MySpaceItemDecoration(spacingInPixels));
    }


//    private FutureTask<List<CarPartPin>> initData() {
//        CarPart carPart = (CarPart) this.getIntent().getSerializableExtra("carPart");
//        FutureTask<List<CarPartPin>> promise = null;
//                app_title_name.setText(carPart.getName());
//        if (null != carPart) {
//            data = carPart.getPinList();
//
//            Callable<List<CarPartPin>> callable = new Callable<List<CarPartPin>>() {
//                @Override
//                public List<CarPartPin> call() throws Exception {
//                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
//                    int customId = application.getCustomId();
//                    try {
//                        JSONObject obj = null;
//                        Map<Integer, Integer> pointMap = application.getPointMap();
//                        Set<Integer> aNumSet = pointMap.keySet();
//                        int aNum = -1;
////                        Map<Integer, Integer> map = null;
//                        for (Integer num : aNumSet) {
//                            aNum = num;
//                        }
////                        Log.d(TAG, String.format("page4, getState-start:%s", TimeUtil.getNowStrTime()));
//                        UdpSystem.getThread().mySuspend();
//                        for (int i=0; i<3; i++) {
//                            try {
//                                obj = UdpSystem.getState(customId, Constant.UDP_WAIT_TIME);
////                                String str = "{\"message\":\"getState\",\"result\":\"ok\",\"data\":{\"state\":[1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}}";
////                                JSONObject myObj = new JSONObject(str);
////                                obj = myObj.getJSONObject("data");
//
//                                Log.d(TAG, String.format("search getState:%s", obj.toString()));
//                                break;
//                            } catch (Exception e) {
//                                Thread.sleep(1000L);
//                            }
//                        }
//                        UdpSystem.getThread().myResume();
////                        Log.d(TAG, String.format("page4, getState-end:%s", TimeUtil.getNowStrTime()));
//                        JSONArray stateArr = obj.getJSONArray("state");
////                        map = new HashMap<>();
//                        checkboxMap.clear();
//                        for (int i = 0; i < stateArr.length(); i++) {
////                            map.put(i + 1, stateArr.getInt(i));
//                            checkboxMap.put(i + 1, stateArr.getInt(i));
//                        }
////                        if (pointMap.size() == 0 || pointMap.get(aNum) == map.get(aNum) || null == pointMap.get(aNum)) {
////                            Log.d(TAG, String.format("success, aNum:%s, aType:%s, mapType:%s", aNum, pointMap.get(aNum), map.get(aNum)));
////                        }
//
////                        if (null != data) {
////                            for (CarPartPin partPin : data) {
////                                if (partPin.getAnum() != 0) {
////                                    partPin.setCurrentType(checkboxMap.get(partPin.getAnum()));
////                                }
////                            }
////                        }
//                        initFlag = true;
//                    } catch (Exception e) {
//                        Log.e(TAG, "getState in page4 error!", e);
//                    }
//
//                    return data;
//                }
//            };
//
//            promise = new FutureTask<List<CarPartPin>>(callable);
//
//            new Thread(promise).start();
//
//        }
//        return promise;
//    }

    private void initData2() {
        CarPart carPart = (CarPart) this.getIntent().getSerializableExtra("carPart");

        app_title_name.setText(carPart.getName());
//        app_title_name.setText("爱的是卡号发来看手法可适当萨法第三方的总偶实在凑IM是的");
        if (null != carPart) {
            data = carPart.getPinList();
        }
    }


    @Override
    public void onItemClickListener(int position, View itemView) {

    }

    @Override
    public void onItemLongClickListener(int position, View itemView) {

    }

    public void back(View view) {
        this.finish();
    }

    public void reset(View view) {
        if (System.currentTimeMillis() - time > 2000) {
            //获得当前的时间
            time = System.currentTimeMillis();

            Intent intent = new Intent(this, TipResetActivity.class);
            intent.putExtra("page", 4);
            this.startActivity(intent);
            reloadContent();
        }
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
