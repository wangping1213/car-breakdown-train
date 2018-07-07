package com.wp.car_breakdown_train.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.entity.CarSystem;
import com.wp.car_breakdown_train.holder.CommonViewHolder;

import java.util.List;

/**
 * 汽车系统adapter
 *
 * @author wangping
 * @version 1.0
 * @since 2018/5/17 16:23
 */
public class P2CarSystemAdapter extends CommonRecycleAdapter<CarSystem> {

    private CommonViewHolder.onItemCommonClickListener commonClickListener;

    private Context myContext;

    public P2CarSystemAdapter(Context context, List<CarSystem> dataList) {
        super(context, dataList, R.layout.item_p2_system);
        myContext = context;
    }

    public P2CarSystemAdapter(Context context, List<CarSystem> dataList, CommonViewHolder.onItemCommonClickListener commonClickListener) {
        super(context, dataList, R.layout.item_p2_system);
        myContext = context;
        this.commonClickListener = commonClickListener;
    }


    @Override
    void bindData(CommonViewHolder holder, CarSystem data) {
        ImageView iv_p2_system = holder.getView(R.id.iv_p2_system);
//        Glide.with(myContext).load(data).into(iv_p2_system);
        iv_p2_system.setBackgroundResource(data.getDrawableId());
        holder
                .setText(R.id.tv_wifi_name, data.getSsid())
                .setCommonClickListener(commonClickListener);
    }

}
