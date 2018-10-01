package com.wp.car_breakdown_train.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wp.car_breakdown_train.Constant;
import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.activity.tip.TipConnFailedActivity;
import com.wp.car_breakdown_train.activity.tip.TipExitActivity;
import com.wp.car_breakdown_train.adapter.P2CarSystemAdapter;
import com.wp.car_breakdown_train.application.MyApplication;
import com.wp.car_breakdown_train.base.BaseActivity;
import com.wp.car_breakdown_train.decoration.MySpaceItemDecoration;
import com.wp.car_breakdown_train.dialog.LoadingDialogUtils;
import com.wp.car_breakdown_train.entity.CarSystem;
import com.wp.car_breakdown_train.enums.P2DrawableEnum;
import com.wp.car_breakdown_train.holder.CommonViewHolder;
import com.wp.car_breakdown_train.receiver.NetworkChangeReceiver;
import com.wp.car_breakdown_train.udp.UdpSystem;
import com.wp.car_breakdown_train.util.TimeUtil;
import com.wp.car_breakdown_train.util.WifiUtil;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 第二页的activity
 *
 * @author wangping
 * @date 2018/6/24 17:22
 */
public class Page2Activity extends BaseActivity implements CommonViewHolder.onItemCommonClickListener, NetworkChangeReceiver.OnConnectionChangeListener {

    private static final String TAG = "wangping";
    private static Pattern PATTERN_WIFI = Pattern.compile("^JG-VDB-II-[^-]+-([^-]+)-[0-9a-fA-F]+$");
    private static Pattern PATTERN_SYSTEM = Pattern.compile("^([a-zA-Z]+)\\d+$");
    private RecyclerView recycler_view_system;
    private static final String WIFI_PASSWORD = "jinggekeji";
    private static final String OTHER_SSID = "JG-VDB-II-OTHER-OTHER-0001";
    private boolean connectionFlag = false;
    private MyApplication application;
    private String currentSsid;
    private Dialog dialog;

    private List<CarSystem> data = new ArrayList<>();
    private WifiManager mWifiManager;
    private static Map<String, String> wifiMap = new HashMap<>();
    private NetworkChangeReceiver mReceiver;
    private int linkCount = 0;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_page2;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        dialog = LoadingDialogUtils.createLoadingDialog(Page2Activity.this, "加载中...");
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        application = (MyApplication) this.getApplication();
        UdpSystem.setApplication(application);
        registerBroadcast();
        Glide.with(this).load(R.drawable.bg1).into((ImageView) findViewById(R.id.iv_bg));
        recycler_view_system = (RecyclerView) findViewById(R.id.recycle_view_system);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_system.setLayoutManager(layoutManager);
        initData();

        recycler_view_system.setAdapter(new P2CarSystemAdapter(this, data, this));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dimen_10_dip);
        recycler_view_system.addItemDecoration(new MySpaceItemDecoration(spacingInPixels));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != dialog) {
                    LoadingDialogUtils.closeDialog(dialog);
                    dialog = null;
                }
                if (data.size() == 0) {
                    List<ScanResult> scanResults = mWifiManager.getScanResults();
                    Log.d(TAG, String.format("page2 resultList:%s", null == scanResults ? scanResults : scanResults.size()));
                    Intent intent = new Intent(Page2Activity.this, TipExitActivity.class);
                    startActivity(intent);
                }
            }
        }, 2000L);
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
        mReceiver.setOnConnectionChangeListener(this);
    }

    private void initData() {
        ArrayList<String> ssidList = this.getIntent().getStringArrayListExtra("ssidList");
        String str = "";
        P2DrawableEnum drawableEnum = null;
        CarSystem carSystem = null;
        for (String ssid : ssidList) {
            str = getSystemName(ssid);
            drawableEnum = P2DrawableEnum.getDrawableEnumByStr(str);
            carSystem = new CarSystem();
            carSystem.setSsid(ssid);
            if (null != drawableEnum && str.equals(drawableEnum.getStr())) {
                carSystem.setDrawableId(drawableEnum.getNormalDrawableId());
                wifiMap.put(str, ssid);
            } else {
                carSystem.setDrawableId(drawableEnum.getNormalDrawableId());
                wifiMap.put(drawableEnum.getStr(), ssid);
            }
            data.add(carSystem);
        }
    }

    /**
     * 取得汽车系统名
     *
     * @param wifiName
     * @return
     */
    private static String getSystemName(String wifiName) {
        String str = "";
        String result = "";
        if (null == wifiName || wifiName.trim().equals("")) return str;
        Matcher matcher = PATTERN_WIFI.matcher(wifiName);
        if (matcher.find()) {
            str = matcher.group(1);
            if (null != str && !"".equals(str)) {
                Matcher m = PATTERN_SYSTEM.matcher(str);
                if (m.find()) {
                    result = m.group(1);
                }
            }
        }
        return result;
    }

    @Override
    public void onItemClickListener(int position, View itemView) {
        dialog = LoadingDialogUtils.createLoadingDialog(Page2Activity.this, "加载中...");
        int normalDrawableId = data.get(position).getDrawableId();
//        int selectedDrawableId = 0;
        final P2DrawableEnum p2DrawableEnum = P2DrawableEnum.getEnumByNomalDrawableId(normalDrawableId);
        if (null != p2DrawableEnum) {
//            selectedDrawableId = p2DrawableEnum.getSelectedDrawableId();
//            Glide.with(this).load(selectedDrawableId).into((ImageView) itemView.findViewById(R.id.iv_p2_system));

            try {
                final MyApplication application = (MyApplication) Page2Activity.this.getApplication();
                application.setUdpState(Constant.STATE_NOT_CONNECT);
                mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiUtil wifiUtil = WifiUtil.newInstance(mWifiManager);
                currentSsid = wifiMap.get(p2DrawableEnum.getStr());

                if (!mWifiManager.isWifiEnabled()) {
                    connectionFlag = true;
                    wifiUtil.openWifi();
                } else if (mWifiManager.getConnectionInfo().getSSID().equals(String.format("\"%s\"", currentSsid))) {
                    connUdp();
                } else if (!mWifiManager.getConnectionInfo().getSSID().equals(String.format("\"%s\"", currentSsid))) {
//                    if (currentSsid.equals(OTHER_SSID)) {
//                        jumpFailed();
//                    } else {
                    connectionFlag = true;
                    connWifiBySsid(wifiUtil, currentSsid);
//                    }
                }

            } catch (Exception e) {
            }
        }
    }

    private void connUdp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = null;
                try {
                    jsonObject = UdpSystem.search();
                } catch (Exception e) {
                    Log.e(TAG, "search error!", e);
                }

                if (null == jsonObject) {
                    jumpFailed();
                    return;
                }

                try {
                    application.setUdpState(Constant.STATE_NOT_CONNECT);//连接成功
                    String deviceNo = "";
                    if (null != jsonObject) {
                        deviceNo = jsonObject.getString("SN");
                        Log.d("wangping", String.format("SN:%s", jsonObject.getString("SN")));
                        JSONObject obj = UdpSystem.connect(deviceNo);
                        if (null != obj) {
                            String result = obj.getString("result");
                            if ("ok".equals(result)) {
                                int customId = obj.getJSONObject("data").getInt("ID");
                                application.setCustomId(customId);
                                Log.d("wangping", String.format("customId:%s", customId));
                            } else if ("already connected".equals(result)) {
                                Log.d(TAG, String.format("deviceNo:%s is already connected!", deviceNo));
                                jumpFailed("连接失败", "已有用户正在使用中");
                                return;
                            }
                            application.setUdpState(Constant.STATE_CONNECTED);//连接成功
                            if (null != UdpSystem.getThread()) {
                                UdpSystem.getThread().interrupt();
                                UdpSystem.setThread(null);
                            }
                            UdpSystem.keepConnect(application, Page2Activity.this, TipConnFailedActivity.class);


                            String info = UdpSystem.getInfo(application.getCustomId());
                            Intent intent = new Intent(Page2Activity.this, Page3Activity.class);
                            intent.putExtra("info", info);
                            Page2Activity.this.startActivity(intent);
                            if (null != dialog) {
                                LoadingDialogUtils.closeDialog(dialog);
                                dialog = null;
                            }


                        }
                    }
                } catch (Exception e) {
                    Log.e("wangping", "error", e);
                }
            }
        }).start();
    }


    private void connWifiBySsid(WifiUtil wifiUtil, String ssid) {
        List<WifiConfiguration> configurationList = mWifiManager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int netId = -1;
        for (WifiConfiguration config : configurationList) {
            if (config.SSID.equals(String.format("\"%s\"", ssid))) {
                configuration = config;
                configuration.preSharedKey = String.format("\"%s\"", WIFI_PASSWORD);
                netId = configuration.networkId;
                break;
            }
        }
        if (null == configuration) {
            configuration = wifiUtil.createWifiInfo(ssid, WIFI_PASSWORD, WifiUtil.WifiCipherType.WIFICIPHER_WPA);
            netId = mWifiManager.addNetwork(configuration);
        }

        Method connectMethod = connectWifiByReflectMethod(netId);
        boolean enabled = false;
        if (connectMethod == null) {
            Log.i(TAG, "connect wifi by enableNetwork method, Add by jiangping.li");
            // 通用API
            enabled = mWifiManager.enableNetwork(netId, true);
        }
        Log.d("wangping", String.format("wifi link to ssid:%s,flag:%s", ssid, enabled));
    }

    private Method connectWifiByReflectMethod(int netId) {
        Method connectMethod = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.i(TAG, "connectWifiByReflectMethod road 1");
            // 反射方法： connect(int, listener) , 4.2 <= phone‘s android version
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId, null);
                } catch (Exception e) {
                    Log.i(TAG, "connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            // 反射方法: connect(Channel c, int networkId, ActionListener listener)
            // 暂时不处理4.1的情况 , 4.1 == phone‘s android version
            Log.i(TAG, "connectWifiByReflectMethod road 2");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Log.i(TAG, "connectWifiByReflectMethod road 3");
            // 反射方法：connectNetwork(int networkId) ,
            // 4.0 <= phone‘s android version < 4.1
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connectNetwork".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else {
            // < android 4.0
            return null;
        }
        return connectMethod;
    }

    @Override
    public void onItemLongClickListener(int position, View itemView) {

    }

    private void jumpFailed(String... tips) {
        Intent intent = new Intent(this, TipConnFailedActivity.class);
        if (tips.length > 0) {
            ArrayList<String> tipList = new ArrayList<String>();
            for (String tip : tips) {
                tipList.add(tip);
            }
            intent.putStringArrayListExtra("tipList", tipList);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        if (null != dialog) {
            if (null != dialog) {
                LoadingDialogUtils.closeDialog(dialog);
                dialog = null;
            }
            dialog = null;
        }
    }

    @Override
    public void onConnectionChangeHandler(boolean isConnection, String ssid) {
        Log.d(TAG, String.format("isConnect:%s, ssid:%s, date:%s", isConnection, ssid, TimeUtil.getNowStrTime()));
        if (isConnection) {
            if (!connectionFlag) return;

            if (!ssid.equals(String.format("\"%s\"", currentSsid))) {
                if (!currentSsid.startsWith("\"JG-VDB-II") && linkCount >= 3) {
                    linkCount = 0;
                    jumpFailed();
                } else {
                    linkCount++;
                    connectionFlag = true;
                    WifiUtil wifiUtil = WifiUtil.newInstance(mWifiManager);
                    connWifiBySsid(wifiUtil, currentSsid);
                    return;
                }

            } else {
                connUdp();
            }
            connectionFlag = false;
        }
    }
}
