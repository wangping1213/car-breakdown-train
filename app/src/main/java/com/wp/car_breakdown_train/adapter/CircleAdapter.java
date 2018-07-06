package com.wp.car_breakdown_train.adapter;

import android.content.Context;
import android.view.LayoutInflater;
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
public class CircleAdapter extends CommonRecycleAdapter<Integer> {

    private CommonViewHolder.onItemCommonClickListener commonClickListener;

    private Context myContext;


    public CircleAdapter(Context context, List<Integer> dataList, int layoutId) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.layoutId = layoutId;
    }

    public CircleAdapter(Context context, List<Integer> dataList) {
        this(context, dataList, R.layout.item_p1_circle);
        myContext = context;
    }

    public CircleAdapter(Context context, List<Integer> dataList, CommonViewHolder.onItemCommonClickListener commonClickListener) {
        this(context, dataList, R.layout.item_p1_circle);
        myContext = context;
        this.commonClickListener = commonClickListener;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        bindData(holder, dataList.get(position));
        int drawableId = R.drawable.shape_circle_no_select;
        if (position == 0) {
            drawableId = R.drawable.shape_circle_selected;
        }
        ImageView iv_circle = holder.getView(R.id.iv_circle);
//        Glide.with(myContext).load(drawableId).into(iv_circle);
        iv_circle.setBackgroundResource(drawableId);
    }

    @Override
    void bindData(CommonViewHolder holder, Integer data) {
//        ImageView iv_p1_car = holder.getView(R.id.iv_circle);

    }

}
