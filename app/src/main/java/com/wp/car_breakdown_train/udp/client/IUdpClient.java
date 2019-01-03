package com.wp.car_breakdown_train.udp.client;

/**
 * udp客户端接口
 * @author wangping
 * @date 2018/12/26 19:20
 */
public interface IUdpClient {

    /**
     * 发送消息
     * @param str
     */
    void sendMsg(String str);

    /**
     * 接收消息
     * @return
     * @throws Exception
     */
    String receive() throws Exception;
}
