package com.wp.car_breakdown_train.udp;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.wp.car_breakdown_train.Constant;
import com.wp.car_breakdown_train.application.MyApplication;
import com.wp.car_breakdown_train.thread.KeepConnectThread;

import org.json.JSONObject;

/**
 * udp子系统的使用类
 *
 * @author wangping
 * @date 2018/6/30 9:28
 */
public class UdpSystem {

    private static final String TAG = "wangping";
    private static KeepConnectThread thread;
    private static MyApplication application;

    /**
     * 搜索
     *
     * @return
     * @throws Exception
     */
    public static JSONObject search() throws Exception {
        if (null != thread) thread.mySuspend();
        String str = "";
        JSONObject obj = null;
        JSONObject result = null;
        Thread.sleep(Constant.UDP_WAIT_TIME);
        int count = 0;
        while (true) {
            try {
                UdpClient.sendMsg("{\"cmd\":\"search\",\"parm\":{\"type\":\"JG-VDB-Type-II\"}}");
                Thread.sleep(Constant.UDP_WAIT_TIME);
                str = UdpClient.receive();
                obj = new JSONObject(str);
                Log.d("wangping", String.format("receive str:%s", str));
                result = obj.getJSONObject("data");
                count = 0;
                break;
            } catch (Exception e) {
                Log.e("wangping", String.format("search error!"), e);
                count++;
                Thread.sleep(Constant.UDP_WAIT_TIME);
                if (count <= 3) continue;
                else break;
            }
        }
        if (null != thread) thread.myResume();
        return result;
    }

    /**
     * 连接
     *
     * @return
     * @throws Exception
     */
    public static JSONObject connect(String deviceNo) throws Exception {
        if (null != thread) thread.mySuspend();
        String str = "";
        JSONObject obj = null;
        JSONObject result = null;
        Thread.sleep(Constant.UDP_WAIT_TIME);
        int count = 0;
        while (true) {
            try {
                UdpClient.sendMsg(String.format("{\"cmd\":\"connect\",\"parm\":{\"SN\":\"%s\"}}", deviceNo));
                Thread.sleep(Constant.UDP_WAIT_TIME);
                str = UdpClient.receive();
                Log.d("wangping", String.format("receive str:%s", str));
                obj = new JSONObject(str);
                count = 0;
                break;
            } catch (Exception e) {
                Log.e("wangping", String.format("connect error!"), e);
                count++;
                Thread.sleep(Constant.UDP_WAIT_TIME);
                if (count <= 3) continue;
                else break;
            }
        }

        if (null != thread) thread.myResume();
        return obj;
    }

    /**
     * 断开连接
     *
     * @return
     * @throws Exception
     */
    public static JSONObject disconnect(int customId) throws Exception {
        if (null != thread) thread.mySuspend();
        String str = "";
        JSONObject obj = null;
        JSONObject result = null;
        Thread.sleep(Constant.UDP_WAIT_TIME);
        int count = 0;
        while (true) {
            try {
                if (!KeepConnectThread.isLock && application.getUdpState() == Constant.STATE_CONNECTED) {
                    Thread.sleep(Constant.UDP_WAIT_TIME);
                    continue;
                }
                UdpClient.sendMsg(String.format("{\"cmd\":\"disconnect\",\"parm\":{\"ID\":\"%s\"}}", customId));
                Thread.sleep(Constant.UDP_WAIT_TIME);
                str = UdpClient.receive();
                Log.d("wangping", String.format("receive str:%s", str));
                obj = new JSONObject(str);
                count = 0;
                break;
            } catch (Exception e) {
                Log.e("wangping", String.format("disconnect error!"), e);
                count++;
                Thread.sleep(Constant.UDP_WAIT_TIME);
                if (count <= 3) continue;
                else throw e;
            }
        }

        if (null != thread) thread.myResume();

        return obj;
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
//        if (waitTime == Constant.UDP_WAIT_TIME) {
            Log.d("wangping", String.format("receive str:%s", str));
//        }
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
        thread.mySuspend();
        JSONObject data = null;
        Thread.sleep(Constant.UDP_WAIT_TIME);
        int count = 0;
        while (true) {
            try {
                if (!KeepConnectThread.isLock && application.getUdpState() == Constant.STATE_CONNECTED) {
                    Thread.sleep(Constant.UDP_WAIT_TIME);
                    continue;
                }
                data = UdpSystem.getInfoPart(customId, 1);
                count = 0;
                break;
            } catch (Exception e) {
                Log.e("wangping", String.format("getInfoPart num:%s error!", 1), e);
                count++;
                Thread.sleep(Constant.UDP_WAIT_TIME);
                if (count <= 3) continue;
                else break;
            }
        }
        StringBuffer sb = new StringBuffer();
        int sum = data.getInt("sum");
        sb.append(data.getString("data"));

        for (int i = 2; i <= sum; ) {
            while (true) {
                try {
                    data = UdpSystem.getInfoPart(customId, i);
                    i++;
                    count = 0;
                    break;
                } catch (Exception e) {
                    Log.e("wangping", String.format("getInfoPart num:%s error!", i), e);
                    count++;
                    Thread.sleep(Constant.UDP_WAIT_TIME);
                    if (count <= 3) continue;
                    else break;
                }
            }
            sb.append(data.getString("data"));
        }
        thread.myResume();
        return new String(Base64.decode(sb.toString(), Base64.DEFAULT));


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
        thread.mySuspend();
        String msg = String.format("{\"cmd\":\"setPoint\",\"parm\":{\"ID\":%s,\"aNum\":%s,\"aType\":%s}}", customId, aNum, aType);
        JSONObject data = null;
        int count = 0;
        while (true) {
            try {
                if (!KeepConnectThread.isLock && application.getUdpState() == Constant.STATE_CONNECTED) {
                    Thread.sleep(Constant.UDP_WAIT_TIME);
                    continue;
                }
                UdpClient.sendMsg(msg);
                Log.d("wangping", msg);
                Thread.sleep(Constant.UDP_WAIT_TIME);
                String str = UdpClient.receive();
                JSONObject obj = null;
                obj = new JSONObject(str);
                data = obj.getJSONObject("data");
                count = 0;
                break;
            } catch (Exception e) {
                Log.e("wangping", String.format("setPoint customId:%s, nNum:%s, aType:%s error!", customId, aNum, aType), e);
                count++;
                Thread.sleep(Constant.UDP_WAIT_TIME);
                if (count <= 3) continue;
                else break;
            }
        }
        thread.myResume();
        return data;

    }

    /**
     * 重置
     *
     * @param customId
     * @return
     * @throws Exception
     */
    public static JSONObject resetPoint(int customId) throws Exception {
        thread.mySuspend();
        JSONObject obj = null;
        int count = 0;
        Thread.sleep(Constant.UDP_WAIT_TIME);
        while (true) {
            try {
                if (!KeepConnectThread.isLock && application.getUdpState() == Constant.STATE_CONNECTED) {
                    Thread.sleep(Constant.UDP_WAIT_TIME);
                    continue;
                }
                UdpClient.sendMsg(String.format("{\"cmd\":\"resetPoint\",\"parm\":{\"ID\":%s}}", customId));
                Thread.sleep(Constant.UDP_WAIT_TIME);
                String str = UdpClient.receive();
                obj = null;
                obj = new JSONObject(str);
                count = 0;
                break;
            } catch (Exception e) {
                Log.e("wangping", String.format("resetPoint customId:%s error!", customId), e);
                count++;
                Thread.sleep(Constant.UDP_WAIT_TIME);
                if (count <= 3) continue;
                else break;
            }
        }
        thread.myResume();
        return obj;

    }

    public static KeepConnectThread getThread() {
        return thread;
    }

    public static void setThread(KeepConnectThread thread) {
        UdpSystem.thread = thread;
    }

    public static void setApplication(MyApplication application) {
        UdpSystem.application = application;
    }
}
