package com.wp.car_breakdown_train.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.holder.CommonViewHolder;

import java.util.List;

/**
 * 设备类型的adapter
 *
 * @author wangping
 * @version 1.0
 * @since 2018/5/17 16:23
 */
public class CarImageAdapter extends CommonRecycleAdapter<Integer> {

    private CommonViewHolder.onItemCommonClickListener commonClickListener;

    private Context myContext;
    private int size;

    public CarImageAdapter(Context context, List<Integer> dataList) {
        super(context, dataList, R.layout.item_p1_img);
        size = dataList.size();
        myContext = context;
    }

    public CarImageAdapter(Context context, List<Integer> dataList, CommonViewHolder.onItemCommonClickListener commonClickListener) {
        super(context, dataList, R.layout.item_p1_img);
        myContext = context;
        size = dataList.size();
        this.commonClickListener = commonClickListener;
    }


    @Override
    void bindData(CommonViewHolder holder, Integer data) {
        ImageView iv_p1_car = holder.getView(R.id.iv_p1_car);
        Glide.with(myContext).load(data).into(iv_p1_car);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        bindData(holder, dataList.get(position%size));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}
