package com.wp.car_breakdown_train.udp.receiver;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 接收setPoint的udp返回值
 * @author wangping
 * @date 2018/12/30 18:32
 */
public class SetPointUdpReceiver extends BaseUdpReceiver {

    private static final String CMD_TYPE = "setPoint";

    @Override
    public JSONObject handle(String type, String result) {
        JSONObject retObj = null;
        try {
            retObj = new JSONObject("{'flag':'1'}");
        } catch (JSONException e) {
            Log.d(TAG, "setPoint json translate error!");
        }
        getApplication().getMap().put("setPoint_retObj", retObj);
        return retObj;
    }
}
