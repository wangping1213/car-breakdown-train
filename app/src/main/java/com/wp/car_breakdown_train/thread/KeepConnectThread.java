package com.wp.car_breakdown_train.thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wp.car_breakdown_train.Constant;
import com.wp.car_breakdown_train.application.MyApplication;
import com.wp.car_breakdown_train.udp.UdpSystem;
import com.wp.car_breakdown_train.util.TimeUtil;

/**
 * 保持连接的线程
 * @author wangping
 * @date 2018/7/4 17:46
 */
public class KeepConnectThread extends Thread {

    private final int SUSPEND = 0;
    private final int RUNNING = 1;
    private int status = 1;
//    private long count = 0;

    private MyApplication application;
    private Context context;
    private Class activityClass;

    public KeepConnectThread(MyApplication application, Context context, Class activityClass) {
        this.application = application;
        this.context = context;
        this.activityClass = activityClass;
    }

    @Override
    public synchronized void run() {
        // 判断是否停止
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
        int customId = application.getCustomId();
        int udpState = application.getUdpState();
        int count = 0;
        while (udpState == Constant.STATE_CONNECTED) {
            try {
                if (status == SUSPEND) {
                    this.wait();
                }
                UdpSystem.getState(customId, 1000L);
//                Log.d("wangping", String.format("getState in keepConnect!date:%s", TimeUtil.getNowStrTime()));
                count = 0;
            } catch (Exception e) {
                Log.e("wangping", "getState error!", e);
                count++;
                if (count <= 3) continue;
                application.setUdpState(Constant.STATE_CONNECT_FAIL);//连接失败
                Intent intent = new Intent(context, activityClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } finally {
                udpState = application.getUdpState();
            }
        }
    }

    /**
     * 恢复
     */
    public synchronized void myResume()
    {
        // 修改状态
        status = RUNNING;
        // 唤醒
        notifyAll();
    }

    /**
     * 挂起
     */
    public void mySuspend()
    {
        // 修改状态
        status = SUSPEND;
    }

    /**
     * 停止
     */
    public void myStop()
    {
        // 修改状态
        application.setUdpState(Constant.STATE_CONNECT_FAIL);
    }
}
