package com.wp.car_breakdown_train.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.wp.car_breakdown_train.R;
import com.wp.car_breakdown_train.util.ScreenUtil;


/**
 * 画廊模式显示帮助类
 * @author wangping
 * @version 1.0
 * @since 2018/6/17 9:37
 */
public class CardScaleHelper {
    private RecyclerView mRecyclerView;
    private Context mContext;

    private float minScale = 0.85f; // 两边视图scale
    private float maxScale = 1.00f; // 两边视图scale
    private int mPagePadding = 5; // 卡片的padding(dp), 卡片间的距离等于2倍的mPagePadding
    private int mShowLeftCardWidth = 50;   // 左边卡片显示大小(dp)
    private int mCenterWidth = 540;//卡片中间位置(px)

    private int mCardWidth; // 卡片宽度
    private int mOnePageWidth; // 滑动一页的距离
    private int mCardGalleryWidth;

    private int mCurrentItemPos;
    private int mCurrentItemOffset;

    private RecyclerView circleRecyclerView;

    public CardScaleHelper(RecyclerView circleRecyclerView) {
        this.circleRecyclerView = circleRecyclerView;
    }

    private CardLinearSnapHelper mLinearSnapHelper = new CardLinearSnapHelper();

    public void attachToRecyclerView(final RecyclerView mRecyclerView) {
        // 开启log会影响滑动体验, 调试时才开启
        this.mRecyclerView = mRecyclerView;
        mContext = mRecyclerView.getContext();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
                if(dx != 0){//去掉奇怪的内存疯涨问题
                    mCurrentItemOffset += dx;
//                    Log.d("wangping", String.format("dx:%d", dx));
                    computeCurrentItemPos();
                    onScrolledChangedCallback();
                }
            }
        });

        initWidth();
        mLinearSnapHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * 初始化卡片宽度
     */
    private void initWidth() {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mCardGalleryWidth = mRecyclerView.getWidth();
                mCardWidth = mCardGalleryWidth - ScreenUtil.dip2px(mContext, 2 * (mPagePadding + mShowLeftCardWidth));
                mOnePageWidth = mCardWidth;
                mRecyclerView.smoothScrollToPosition(mCurrentItemPos);
                onScrolledChangedCallback();
            }
        });
    }

    public void setCurrentItemPos(int currentItemPos) {
        this.mCurrentItemPos = currentItemPos;
    }

    public int getCurrentItemPos() {
        return mCurrentItemPos;
    }

    private int getDestItemOffset(int destPos) {
        return mOnePageWidth * destPos;
    }

    /**
     * 计算mCurrentItemOffset
     */
    private void computeCurrentItemPos() {
        if (mOnePageWidth <= 0) return;
        View view = mLinearSnapHelper.findSnapView(mRecyclerView.getLayoutManager());
        mCurrentItemPos = mRecyclerView.getChildAdapterPosition(view);
        int drawableId = R.drawable.shape_circle_no_select;
        for (int i=0; i<circleRecyclerView.getChildCount(); i++) {
            if (i == mCurrentItemPos) {
                drawableId = R.drawable.shape_circle_selected;
            } else {
                drawableId = R.drawable.shape_circle_no_select;
            }
            circleRecyclerView.getChildAt(i).findViewById(R.id.iv_circle).setBackgroundResource(drawableId);
        }

    }

    /**
     * RecyclerView位移事件监听, view大小随位移事件变化
     */
    private void onScrolledChangedCallback() {
        View leftView = null;
        View currentView;
        View rightView = null;
        leftView = mRecyclerView.getLayoutManager().findViewByPosition((mCurrentItemPos - 1 == -1) ? (mRecyclerView.getAdapter().getItemCount() - 1) : (mCurrentItemPos - 1));
        currentView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentItemPos);
        rightView = mRecyclerView.getLayoutManager().findViewByPosition((mCurrentItemPos + 1 == mRecyclerView.getAdapter().getItemCount()) ? 0 : (mCurrentItemPos + 1));

//        Log.d("wangping", String.format("leftPos:%s, currentPos:%s, rightPos:%s",

        if (leftView != null) {
            double x = (leftView.getRight() + leftView.getLeft()) * 1.0 / 2;
            double y = getY(x);
            leftView.setScaleY(new Float(y));
        }
        if (currentView != null) {
            double x = (currentView.getRight() + currentView.getLeft()) * 1.0 / 2;
            double y = getY(x);
            currentView.setScaleY(new Float(y));

        }
        if (rightView != null) {
            double x = (rightView.getRight() + rightView.getLeft()) * 1.0 / 2;
            double y = getY(x);
            rightView.setScaleY(new Float(y));
        }
    }

    private double getY(double x) {
        double y = -1 * minScale * Math.pow((Math.abs(x) - mCenterWidth), 2) / Math.pow(mCenterWidth, 2) + (1.90- minScale);
        return Math.min(Math.max(y, minScale), maxScale);
    }

    public void setScale(float scale) {
        minScale = scale;
    }

    public void setPagePadding(int pagePadding) {
        mPagePadding = pagePadding;
    }

    public void setShowLeftCardWidth(int showLeftCardWidth) {
        mShowLeftCardWidth = showLeftCardWidth;
    }

    public void setCenterWidth(int mCenterWidth) {
        this.mCenterWidth = mCenterWidth;
    }
}
