package com.wp.car_breakdown_train.entity;

import java.io.Serializable;

/**
 * 子系统类
 * @author wangping
 * @date 2018/7/7 10:51
 */
public class CarSystem implements Serializable {
    private String ssid;
    private int drawableId;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
