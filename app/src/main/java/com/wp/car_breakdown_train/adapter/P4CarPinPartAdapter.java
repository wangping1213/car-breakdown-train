package com.wp.car_breakdown_train.adapter;

import android.content.Context;
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

    public P4CarPinPartAdapter(Context context, List<CarPartPin> dataList) {
        super(context, dataList, R.layout.item_p4_pin);
        myContext = context;
    }

    public P4CarPinPartAdapter(Context context, List<CarPartPin> dataList, CommonViewHolder.onItemCommonClickListener commonClickListener) {
        super(context, dataList, R.layout.item_p4_pin);
        myContext = context;
        this.commonClickListener = commonClickListener;
    }


    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    void bindData(CommonViewHolder holder, CarPartPin data) {
        ImageView iv_p4_pin = holder.getView(R.id.iv_p4_pin);

        List<CheckBox> cbList = new ArrayList<>();
        List<TextView> tvList = new ArrayList<>();
        Glide.with(myContext).load(R.drawable.p4_list_bg).into(iv_p4_pin);
        Glide.with(myContext).load(R.drawable.p4_line).into((ImageView) holder.getView(R.id.iv_pID_2));
        Glide.with(myContext).load(R.drawable.p4_line).into((ImageView) holder.getView(R.id.iv_cID_2));
        holder
                .setText(R.id.tv_pin_name, data.getName())
                .setText(R.id.tv_pID, data.getPid())
                .setText(R.id.tv_cID, data.getCid())
                .setCommonClickListener(commonClickListener);

        int[] typeArr = data.getAtype();
        if (data.getAnum() != 0) {
            int currentType = Page4Activity.checkboxMap.get(data.getAnum());
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
