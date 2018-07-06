package com.wp.car_breakdown_train.udp;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.wp.car_breakdown_train.Constant;
import com.wp.car_breakdown_train.application.MyApplication;
import com.wp.car_breakdown_train.thread.KeepConnectThread;
import com.wp.car_breakdown_train.util.TimeUtil;

import org.json.JSONObject;

/**
 * udp子系统的使用类
 *
 * @author wangping
 * @date 2018/6/30 9:28
 */
public class UdpSystem {

    private static KeepConnectThread thread;


    public static boolean isLock = false;

    /**
     * 搜索
     *
     * @return
     * @throws Exception
     */
    public static JSONObject search() throws Exception {
//        synchronized (thread) {
        if (null != thread) thread.mySuspend();
        String str = "";
        JSONObject obj = null;
        JSONObject result = null;
        UdpClient.sendMsg("{\"cmd\":\"search\",\"parm\":{\"type\":\"JG-VDB-Type-II\"}}");
        Thread.sleep(Constant.UDP_WAIT_TIME);
        str = UdpClient.receive();
        obj = new JSONObject(str);
        Log.d("wangping", String.format("receive str:%s", str));
        result = obj.getJSONObject("data");
        if (null != thread) thread.myResume();
        return result;
//        }
    }

    /**
     * 连接
     *
     * @return
     * @throws Exception
     */
    public static JSONObject connect(String deviceNo) throws Exception {
//        synchronized (thread) {
        if (null != thread) thread.mySuspend();
        String str = "";
        JSONObject obj = null;
        JSONObject result = null;
        UdpClient.sendMsg(String.format("{\"cmd\":\"connect\",\"parm\":{\"SN\":\"%s\"}}", deviceNo));
        Thread.sleep(Constant.UDP_WAIT_TIME);
        str = UdpClient.receive();
        Log.d("wangping", String.format("receive str:%s", str));
        obj = new JSONObject(str);
        if (null != thread) thread.myResume();
        return obj;
//        }
    }

    /**
     * 连接
     *
     * @return
     * @throws Exception
     */
    public static JSONObject getState(int customId, long... waitTimes) throws Exception {
        String str = "";
        JSONObject obj = null;
        JSONObject result = null;
        UdpClient.sendMsg(String.format("{\"cmd\":\"getState\",\"parm\":{\"ID\":%s}}", customId));
        long waitTime = Constant.UDP_WAIT_TIME;
        if (null != waitTimes && waitTimes.length > 0) {
            waitTime = waitTimes[0];
        }
        Thread.sleep(waitTime);
        str = UdpClient.receive();
        if (waitTime == Constant.UDP_WAIT_TIME) {
        Log.d("wangping", String.format("receive str:%s", str));
        }
        obj = new JSONObject(str);
        result = obj.getJSONObject("data");
        return result;
    }

    public static Thread keepConnect(final MyApplication application, final Context context, final Class activityClass) throws Exception {
        if (null != thread) return thread;
        thread = new KeepConnectThread(application, context, activityClass);
        thread.start();
        return thread;
    }

    public static JSONObject getInfoPart(int customId, int num) throws Exception {
        UdpClient.sendMsg(String.format("{\"cmd\":\"getInfo\",\"parm\":{\"ID\":%s,\"num\":%s}}", customId, num));
        Thread.sleep(100L);
        String str = UdpClient.receive();
        Log.d("wangping", String.format("receive getInfoPart:%s", str));
        JSONObject obj = null;
        obj = new JSONObject(str);
        JSONObject data = obj.getJSONObject("data");
        return data;
    }

    /**
     * 取得汽车元器件信息
     *
     * @param customId
     * @return
     * @throws Exception
     */
    public static String getInfo(int customId) throws Exception {
//        synchronized (thread) {
        thread.mySuspend();
        JSONObject data = UdpSystem.getInfoPart(customId, 1);
        StringBuffer sb = new StringBuffer();
        int sum = data.getInt("sum");
        sb.append(data.getString("data"));
        for (int i = 2; i <= sum; i++) {
            data = UdpSystem.getInfoPart(customId, i);
            sb.append(data.getString("data"));
        }
        thread.myResume();
        return new String(Base64.decode(sb.toString(), Base64.DEFAULT));
//        }

    }

    /**
     * 操作
     *
     * @param customId
     * @param aNum
     * @param aType
     * @return
     * @throws Exception
     */
    public static JSONObject setPoint(int customId, int aNum, int aType) throws Exception {
//        synchronized (thread) {
        thread.mySuspend();
        String msg = String.format("{\"cmd\":\"setPoint\",\"parm\":{\"ID\":%s,\"aNum\":%s,\"aType\":%s}}", customId, aNum, aType);
        UdpClient.sendMsg(msg);
        Log.d("wangping", msg);
        Thread.sleep(Constant.UDP_WAIT_TIME);
        String str = UdpClient.receive();
        JSONObject obj = null;
        obj = new JSONObject(str);
        JSONObject data = obj.getJSONObject("data");
        thread.myResume();
        return data;
//        }
    }

    /**
     * 重置
     *
     * @param customId
     * @return
     * @throws Exception
     */
    public static JSONObject resetPoint(int customId) throws Exception {
//        synchronized (thread) {
        thread.mySuspend();
        UdpClient.sendMsg(String.format("{\"cmd\":\"resetPoint\",\"parm\":{\"ID\":%s}}", customId));
        Thread.sleep(Constant.UDP_WAIT_TIME);
        String str = UdpClient.receive();
        JSONObject obj = null;
        obj = new JSONObject(str);
        thread.myResume();
        return obj;
//        }
    }

    public static KeepConnectThread getThread() {
        return thread;
    }

    public static void setThread(KeepConnectThread thread) {
        UdpSystem.thread = thread;
    }
}
