package com.wp.car_breakdown_train.enums;


import com.wp.car_breakdown_train.R;

/**
 * drawable与字符串的转换
 * @author wangping
 * @version 1.0
 * @since 2018/5/17 15:02
 */
public enum DrawableEnum {
//    P2_CAR_BAIC_EV160_1(R.drawable.p2_car_baic_ev160_1, "p2_car_baic_ev160_1.png"),
//    P2_CAR_BAIC_EV160_2(R.drawable.p2_car_baic_ev160_2, "p2_car_baic_ev160_2.png"),
//    P2_CAR_BC_1(R.drawable.p2_car_bc_1, "p2_car_bc_1.png"),
//    P2_CAR_BC_2(R.drawable.p2_car_bc_2, "p2_car_bc_2.png"),
//    P2_CAR_BYDQ_1(R.drawable.p2_car_bydq_1, "p2_car_bydq_1.png"),
//    P2_CAR_BYDQ_2(R.drawable.p2_car_bydq_2, "p2_car_bydq_2.png"),
//    P2_CAR_CRUZE_1(R.drawable.p2_car_cruze_1, "p2_car_cruze_1.png"),
//    P2_CAR_CRUZE_2(R.drawable.p2_car_cruze_2, "p2_car_cruze_2.png"),
//    P2_CAR_MAGOTAN_1(R.drawable.p2_car_magotan_1, "p2_car_magotan_1.png"),
//    P2_CAR_MAGOTAN_2(R.drawable.p2_car_magotan_2, "p2_car_magotan_2.png"),
//    P2_CAR_PASSAT_1(R.drawable.p2_car_passat_1, "p2_car_passat_1.png"),
//    P2_CAR_PASSAT_2(R.drawable.p2_car_passat_2, "p2_car_passat_2.png"),
//
//
//    P3_IMG_AIR_CONDITION(R.drawable.p3_img_air_condition, "p3_img_air_condition.png"),
//    P3_IMG_CAR_BODY(R.drawable.p3_img_car_body, "p3_img_car_body.png"),
//    P3_IMG_CHASSIS(R.drawable.p3_img_chassis, "p3_img_chassis.png"),
//    P3_IMG_ENGINE(R.drawable.p3_img_engine, "p3_img_engine.png"),

    P2_AIR_CONDITION_NORMAL(R.drawable.p2_air_condition_normal, "AIR"),
    P2_CAR_BODY_NORMAL(R.drawable.p2_car_body_normal, "ECU"),
    P2_CHASSIS_NORMAL(R.drawable.p2_chassis_normal, "CHA"),
    P2_ENGINE_NORMAL(R.drawable.p2_engine_normal, "ENG")


    ;

    /**
     * 图片drawable的id
     */
    private int myDrawableId;

    /**
     * 图片全称
     */
    private String str;

    DrawableEnum(int myDrawableId, String str) {
        this.myDrawableId = myDrawableId;
        this.str = str;
    }

    public int getMyDrawableId() {
        return myDrawableId;
    }


    public String getStr() {
        return str;
    }


    /**
     * 根据图片名称取得对应enum
     *
     * @param str
     * @return
     */
    public static DrawableEnum getMyDrawableIdByStr(String str) {
        DrawableEnum tmpEnum = null;
        if (null == str && str.trim().endsWith("")) return null;
        for (DrawableEnum drawableEnum : DrawableEnum.values()) {
            if (str.equals(drawableEnum.str)) {
                tmpEnum = drawableEnum;
                break;
            }
        }
        return tmpEnum;
    }

}
