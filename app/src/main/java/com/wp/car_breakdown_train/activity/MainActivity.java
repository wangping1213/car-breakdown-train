package com.wp.car_breakdown_train.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.activity.tip.TipConnFailedActivity;
import com.wp.car_breakdown_train.adapter.CarImageAdapter;
import com.wp.car_breakdown_train.adapter.CircleAdapter;
import com.wp.car_breakdown_train.base.BaseActivity;
import com.wp.car_breakdown_train.decoration.CircleSpaceItemDecoration;
import com.wp.car_breakdown_train.decoration.MySpaceItemDecoration;
import com.wp.car_breakdown_train.gallery.CardScaleHelper;
import com.wp.car_breakdown_train.receiver.NetworkChangeReceiver;
import com.wp.car_breakdown_train.util.ScreenUtil;
import com.wp.car_breakdown_train.util.TimeUtil;
import com.wp.car_breakdown_train.util.WifiUtil;
import com.wp.car_breakdown_train.view.MarqueeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 主页
 * @author wangping
 * @date 2018/6/24 17:23
 */
public class MainActivity extends BaseActivity implements NetworkChangeReceiver.OnScanResultsListener {

    private static final String TAG = "wangping";
    private MarqueeView home_recycle_view;
    private RecyclerView recycle_view_circle;

    private List<Integer> data;
    private WifiManager mWifiManager;

    private NetworkChangeReceiver mReceiver;
    private boolean scanResultFlag = false;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.onStop();
        Glide.with(this).load(R.drawable.bg1).into((ImageView) findViewById(R.id.iv_bg));
        home_recycle_view = (MarqueeView) findViewById(R.id.home_recycle_view);
        recycle_view_circle = (RecyclerView) findViewById(R.id.recycle_view_circle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        home_recycle_view.setLayoutManager(linearLayoutManager);
        RecyclerView.LayoutManager circleLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycle_view_circle.setLayoutManager(circleLayoutManager);

        application.getMap().put("returnFlag", false);
        application.setCurrentActivityClass(this.getClass());
        registerBroadcast();

        initData();

        // 设置适配器
        home_recycle_view.setAdapter(new CarImageAdapter(this, data));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dimen_10_dip);
        home_recycle_view.addItemDecoration(new MySpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_5_dip)));
        CardScaleHelper cardScaleHelper = new CardScaleHelper(recycle_view_circle);
        cardScaleHelper.setSize(data.size());
        cardScaleHelper.setPagePadding(getResources().getDimensionPixelSize(R.dimen.dimen_5_dip));
        cardScaleHelper.setShowLeftCardWidth(getResources().getDimensionPixelSize(R.dimen.dimen_50_dip));
        cardScaleHelper.setCenterWidth(getResources().getDimensionPixelSize(R.dimen.dimen_180_dip));
        cardScaleHelper.attachToRecyclerView(home_recycle_view);

        recycle_view_circle.setAdapter(new CircleAdapter(this, data));
        recycle_view_circle.addItemDecoration(new CircleSpaceItemDecoration(spacingInPixels));

        Configuration config = getResources().getConfiguration();
        int smallestScreenWidth = config.smallestScreenWidthDp;
        Log.d(TAG, "smallest width : " + smallestScreenWidth);
        Log.d(TAG, String.format("540px-pd:%s", ScreenUtil.px2dip(this, 540F)));
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        mReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
        if (this instanceof NetworkChangeReceiver.OnScanResultsListener) {
            mReceiver.setOnScanResultsListener(this);
        }
    }

    private void initData() {
        data = new ArrayList<Integer>(Arrays.asList(
                R.drawable.p1_car_baic_ec200_select, R.drawable.p1_car_baic_ev160_select, R.drawable.p1_car_bc_select,
                R.drawable.p1_car_bydq_select, R.drawable.p1_car_byd_e5_select, R.drawable.p1_car_cruze_select,
                R.drawable.p1_car_magotan_select, R.drawable.p1_car_roewe_e550_select, R.drawable.p1_car_superb_select,
                R.drawable.p1_car_tiguan_select, R.drawable.p1_car_more_select
        ));
    }

    public void jumpInto(View view) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiUtil wifiUtil = WifiUtil.newInstance(mWifiManager);
                if (!mWifiManager.isWifiEnabled()) {
                    wifiUtil.openWifi();
                } else {
                    mWifiManager.startScan();
                    Log.d("wangping", String.format("jumpInto:%s", TimeUtil.getNowStrTime()));
                }
                scanResultFlag = true;

                try {
                    Thread.sleep(2000L);
                } catch (Exception e) {
                }
                if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && scanResultFlag){
                    // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
                    Log.d(TAG, String.format("location is not opened!"));
                    //弹出提示，未打开位置开关
                    Intent intent = new Intent(MainActivity.this, TipConnFailedActivity.class);
                    ArrayList<String> tipList = new ArrayList<String>();
                    tipList.add("扫描WIFI列表失败");
                    tipList.add("请打开位置（GPS）开关");
                    intent.putStringArrayListExtra("tipList", tipList);
                    startActivity(intent);
                    return;
                }
            }
        }).start();
    }

    @Override
    public void onScanResultHandler(List<ScanResult> resultList) {
        ArrayList<String> ssidList = new ArrayList<>();

        if (null == resultList || !scanResultFlag) return;

        Log.d(TAG, "接收到" + TimeUtil.getNowStrTime());
        Log.d(TAG, "mScanResults.size()===" + resultList.size());

        for (ScanResult result : resultList) {
            if (result.SSID.startsWith("JG-VDB-II")) {
                ssidList.add(result.SSID);
            }
        }
        Collections.sort(ssidList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1 != null && !"".equals(o1) && o2 != null && !"".equals(o2)) {
                    return o1.compareTo(o2);
                }
                return 0;
            }
        });


        Intent t = new Intent(this, Page2Activity.class);
        t.putStringArrayListExtra("ssidList", ssidList);
        this.startActivity(t);
        scanResultFlag = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        home_recycle_view.stopMarquee();
        Log.d(TAG, String.format("main page onPause!"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        home_recycle_view.startMarquee(-1);
        Log.d(TAG, String.format("main page onResume!"));
    }
}
