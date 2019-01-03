package com.wp.car_breakdown_train.udp.receiver;

import android.util.Log;

import com.wp.car_breakdown_train.entity.UdpResult;
import com.wp.car_breakdown_train.udp.client.UdpClientFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 接收search的udp返回值
 *
 * @author wangping
 * @date 2018/12/30 18:32
 */
public class SearchUdpReceiver extends BaseUdpReceiver {

    private static final String CMD_TYPE = "search";

    @Override
    public JSONObject handle(String type, String result) {
        UdpResult udpResult = getApplication().getMapData("udpResult", UdpResult.class);
        if (null == udpResult) {
            udpResult = new UdpResult(CMD_TYPE);
            getApplication().getMap().put("udpResult", udpResult);
        }
        JSONObject retObj = null;
        try {
            JSONObject obj = new JSONObject(result);
            retObj = obj.getJSONObject("data");
        } catch (JSONException e) {
            Log.w(TAG, "search cmd error_1!");
        }
        udpResult.setData(retObj);
        udpResult.setFlag("1");

        return retObj;
    }
}
