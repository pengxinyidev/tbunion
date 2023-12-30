package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomepageContentAdapter extends RecyclerView.Adapter<HomepageContentAdapter.InnerHolder> {

    private List<HomePagerContent.DataBean> data = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        HomePagerContent.DataBean dataBean = data.get(position);
        holder.setData(dataBean);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        data.clear();
        data.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<HomePagerContent.DataBean> contents) {
        int oldSize = data.size();
        data.addAll(contents);
        notifyItemRangeChanged(oldSize, contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.goods_after_off_prise)
        public TextView finalPriseTv;

        @BindView(R.id.goods_original_prise)
        public TextView originalPriseTv;

        @BindView(R.id.goods_sell_count)
        public TextView sellCountTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(HomePagerContent.DataBean dataBean) {
            Context context = itemView.getContext();
            title.setText(dataBean.getTitle());
            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            int coverSize = (width > height ? width : height) / 2;
//            LogUtils.d(this, width + "x" + height + ".jpg");
//            LogUtils.d(this, "src url ===> " + dataBean.getPict_url());
//            Glide.with(context).load(UrlUtils.getCoverPath(dataBean.getPict_url(), coverSize)).into(cover);
            Glide.with(context).load(UrlUtils.getCoverPath(dataBean.getPict_url())).into(cover);
            int couponAmount = dataBean.getCoupon_amount();
            String finalPrice = dataBean.getZk_final_price();
            float resultPrise = Float.parseFloat(finalPrice) - couponAmount;
            offPriseTv.setText(String.format(context.getString(R.string.text_goods_off_prise), couponAmount));
            finalPriseTv.setText(String.format("%.2f", resultPrise));
            originalPriseTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //增加删除线
            originalPriseTv.setText(String.format(context.getString(R.string.text_goods_original_prise), finalPrice));
            sellCountTv.setText(String.format(context.getString(R.string.text_goods_sell_count), dataBean.getVolume()));
        }
    }
}
