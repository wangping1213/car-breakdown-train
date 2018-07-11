package com.wp.car_breakdown_train.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.activity.Page4Activity;
import com.wp.car_breakdown_train.application.MyApplication;
import com.wp.car_breakdown_train.entity.CarPartPin;
import com.wp.car_breakdown_train.holder.CommonViewHolder;
import com.wp.car_breakdown_train.listener.OnMyClickListener;
import com.wp.car_breakdown_train.viewTarget.MyViewTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 汽车针脚adapter
 *
 * @author wangping
 * @version 1.0
 * @since 2018/5/17 16:23
 */
public class P4CarPinPartAdapter extends CommonRecycleAdapter<CarPartPin> {

    private CommonViewHolder.onItemCommonClickListener commonClickListener;

    private Context myContext;

    private MyApplication application;

    public P4CarPinPartAdapter(Context context, List<CarPartPin> dataList) {
        super(context, dataList, R.layout.item_p4_pin);
        myContext = context;
        application = (MyApplication) ((Page4Activity) myContext).getApplication();
    }

    public P4CarPinPartAdapter(Context context, List<CarPartPin> dataList, CommonViewHolder.onItemCommonClickListener commonClickListener) {
        super(context, dataList, R.layout.item_p4_pin);
        myContext = context;
        this.commonClickListener = commonClickListener;
        application = (MyApplication) ((Page4Activity) myContext).getApplication();
    }


    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    void bindData(final CommonViewHolder holder, CarPartPin data) {
        final ImageView iv_p4_pin = holder.getView(R.id.iv_p4_pin);

        final List<Integer> viewIdList = Arrays.asList(R.id.ck_cut_off, R.id.ck_conn, R.id.ck_bonding, R.id.tv_conn, R.id.tv_bonding, R.id.tv_cut_off);

        final List<CheckBox> cbList = new ArrayList<>();
        final List<TextView> tvList = new ArrayList<>();
        Glide.with(myContext).load(R.drawable.p4_line).into((ImageView) holder.getView(R.id.iv_pID_2));
        Glide.with(myContext).load(R.drawable.p4_line).into((ImageView) holder.getView(R.id.iv_cID_2));
        holder
                .setText(R.id.tv_pin_name, data.getName())
//                .setText(R.id.tv_pin_name, "爱的是卡号发来看手法可适当萨法第三方的总偶实在凑IM是的")
                .setText(R.id.tv_pID, data.getPid())
                .setText(R.id.tv_cID, data.getCid())
                .setCommonClickListener(commonClickListener);
        int[] typeArr = data.getAtype();
        if (data.getAnum() != 0) {
            int currentType = -1;
            if (null != application.getPointMap().get(data.getAnum())) {
                currentType = application.getPointMap().get(data.getAnum());
            }
            if (typeArr.length == 2) {
                holder
                        .setViewVisibility(R.id.ck_bonding, View.GONE)
                        .setViewVisibility(R.id.tv_bonding, View.GONE);
            }

            for (int i=0; i<typeArr.length; i++) {
                switch (typeArr[i]) {
                    case 0:
                        setCheckBox(holder, R.id.ck_conn, R.id.tv_conn, typeArr[i] == currentType, cbList, tvList, data);
                        break;
                    case 1:
                        setCheckBox(holder, R.id.ck_cut_off, R.id.tv_cut_off, typeArr[i] == currentType, cbList, tvList, data);
                        break;
                    case 2:
                        setCheckBox(holder, R.id.ck_bonding, R.id.tv_bonding, typeArr[i] == currentType, cbList, tvList, data);
                        break;
                }
            }

        } else {
            holder
                    .setViewVisibility(R.id.ck_conn, View.GONE)
                    .setViewVisibility(R.id.tv_conn, View.GONE)
                    .setViewVisibility(R.id.ck_cut_off, View.GONE)
                    .setViewVisibility(R.id.tv_cut_off, View.GONE)
                    .setViewVisibility(R.id.ck_bonding, View.GONE)
                    .setViewVisibility(R.id.tv_bonding, View.GONE);
        }

        final TextView tv = holder.getView(R.id.tv_pin_name);
        Glide.with(myContext).load(R.drawable.p4_list_bg)
//                .listener(new RequestListener<Integer, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, Integer integer, Target<GlideDrawable> target, boolean b) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable glideDrawable, Integer integer, Target<GlideDrawable> target, boolean b, boolean b1) {
//                        if (iv_p4_pin == null) {
//                            return false;
//                        }
//                        if (iv_p4_pin.getScaleType() != ImageView.ScaleType.FIT_XY) {
//                            iv_p4_pin.setScaleType(ImageView.ScaleType.FIT_XY);
//                        }
//                        ViewGroup.LayoutParams params = iv_p4_pin.getLayoutParams();
//
//                        int perHeight = (66 * 3 + 66 * tv.getLineCount()) / 2;
//                        params.height = perHeight * 3;
//                        iv_p4_pin.setLayoutParams(params);
//
//
//
//                        return false;
//                    }
//                })
                .into(iv_p4_pin);

//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (Integer vId : viewIdList) {
//                    ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) holder.getView(vId).getLayoutParams();
////                            lp.bottomToBottom = perHeight / 2;
////                            lp.topToBottom = perHeight / 2;
////                    lp.topMargin = 66 * 2;
////                    lp.bottomMargin = 60;
//                    Log.d("wangping", String.format("%s", lp.bottomMargin));
//                }
//            }
//        }, 2000L);
    }

    private void setCheckBox(CommonViewHolder holder, int ckId, int tvId, boolean isChecked, List<CheckBox> cbList, List<TextView> tvList, CarPartPin data) {
        CheckBox cb = holder.getView(ckId);
        TextView tv = holder.getView(tvId);
        if (!cbList.contains(cb)) cbList.add(cb);
        if (!tvList.contains(tv)) tvList.add(tv);

        int drawableId;
        if (isChecked) {//选中
            tv.setTextColor(myContext.getResources().getColor(R.color.color_circle_2));
            drawableId = R.drawable.p4_icon_select;
            cb.setChecked(true);
        } else {
            tv.setTextColor(myContext.getResources().getColor(R.color.color_btn_cancel));
            drawableId = R.drawable.p4_icon_normal;
            cb.setChecked(false);
        }
        Glide.with(myContext).load(drawableId).into(new MyViewTarget(cb));

        cb.setOnClickListener(new OnMyClickListener(holder, tv, myContext, cbList, tvList, data, (MyApplication) ((Page4Activity) myContext).getApplication()));
        tv.setOnClickListener(new OnMyClickListener(holder, cb, myContext, cbList, tvList, data, (MyApplication) ((Page4Activity) myContext).getApplication()));

    }


}
