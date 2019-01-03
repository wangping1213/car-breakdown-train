package com.wp.car_breakdown_train.udp.client;

/**
 * udp工厂
 * @author wangping
 * @date 2018/12/26 19:25
 */
public class UdpClientFactory {

    public final static String VBS_KEY = "VBS";
    public final static String TBOX_KEY = "TBOX";
    public final static String TD_KEY = "TD";

    public static IUdpClient getUdpClient(String type) {
        IUdpClient client = UdpBoxClient.newInstane();
//        if (UdpClientFactory.TBOX_KEY.equals(type.toUpperCase())) {
//            client = UdpBoxClient.newInstane();
//        }

        return client;
    }
}
