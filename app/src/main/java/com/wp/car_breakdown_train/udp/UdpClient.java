package com.wp.car_breakdown_train.udp;


import android.util.Log;

import com.wp.car_breakdown_train.Constant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 发送udp广播消息
 * @author wangping
 * @date 2018/6/24 21:12
 */
public class UdpClient {
    private static MulticastSocket ms;
    private static InetAddress address;

    private static void initSocket() {
        if (null == ms) {
            try {
                ms = new MulticastSocket(Constant.UDP_PORT);
                address = InetAddress.getByName(Constant.UDP_IP);
                if (!address.isMulticastAddress()) {
                    Log.w("wangping", "Not MulticastAddress!");
                }
                ms.setSoTimeout(Constant.UDP_TIMEOUT);
//                ms.joinGroup(address);
            } catch (IOException e) {
                Log.w("wangping", e.getMessage());
            }
        }
    }

    public static void sendMsg(String str) {
        initSocket();
        DatagramPacket dataPacket = null;
        try {
            ms.setTimeToLive(4);
            byte[] data = str.getBytes();
//                //这个地方可以输出判断该地址是不是广播类型的地址
//                System.out.println(String.format("%s", address.isMulticastAddress()));
            dataPacket = new DatagramPacket(data, data.length, address, Constant.UDP_PORT);
            ms.send(dataPacket);
        } catch (Exception e) {
            Log.e("wangping", "udp send msg error!", e);
        }
    }

    public static String receive() throws Exception {
        String resultStr = "";
        initSocket();
        byte buf[] = new byte[Constant.UDP_BYTE_LENGTH];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        ms.receive(dp);
        resultStr = new String(dp.getData(), 0, dp.getLength());
        return resultStr;
    }



    public static void main(String[] args) throws Exception {
//        JSONObject jsonObject = UdpClient.search();
//        String deviceNo = "";
//        if (null != jsonObject) {
//            deviceNo = jsonObject.getString("SN");
//            Log.d("wangping", String.format("SN:%s", jsonObject.getString("SN")));
//            JSONObject obj = UdpClient.connect(deviceNo);
//            if (null != obj)
//                Log.d("wangping", String.format("customId:%s", obj.getInt("ID")));
//        }
//        UdpClient.sendMsg("{\"cmd\":\"search\",\"parm\":{\"type\":\"JG-VDB-Type-II\"}}");
//        String str = UdpClient.receive();
//        JSONObject obj = JSON.parseObject(str);
//        JSONObject data = obj.getJSONObject("data");
//        System.out.println(data.get("SN"));
//
//        UdpClient.sendMsg(String.format("{\"cmd\":\"connect\",\"parm\":{\"SN\":\"%s\"}}", data.get("SN")));
//        str = UdpClient.receive();
//        obj = JSON.parseObject(str);
//        data = obj.getJSONObject("data");
//        System.out.println(data.get("ID"));
//
//
////        UdpClient.sendMsg(String.format("{\"cmd\":\"disconnect\",\"parm\":{\"ID\":%s}}", data.get("ID")));
////        str = UdpClient.receive();
////        obj = JSON.parseObject(str);
////        System.out.println(obj.get("result"));
//
//        int customId = data.getIntValue("ID");
//        UdpClient.sendMsg(String.format("{\"cmd\":\"getInfo\",\"parm\":{\"ID\":%s,\"num\":1}}", customId));
//        str = UdpClient.receive();
//        obj = JSON.parseObject(str);
//        data = obj.getJSONObject("data");
//
//        int sum = data.getIntValue("sum");
//        StringBuffer sb = new StringBuffer();
//        sb.append(data.getString("data"));
//        for (int i=2; i<=sum; i++) {
//            UdpClient.sendMsg(String.format("{\"cmd\":\"getInfo\",\"parm\":{\"ID\":%s,\"num\":%s}}", customId, i));
//            Thread.sleep(100);
//            str = UdpClient.receive();
//            obj = JSON.parseObject(str);
//            data = obj.getJSONObject("data");
//            sb.append(data.getString("data"));
//        }
//
//
//        System.out.println(JSON.parseObject(new String(Base64Utils.decodeFromString(sb.toString()))));
//
//        UdpClient.sendMsg(String.format("{\"cmd\":\"getState\",\"parm\":{\"ID\":%s}}", customId));
//        str = UdpClient.receive();
//        obj = JSON.parseObject(str);
//        System.out.println(obj.get("result"));

    }
}
