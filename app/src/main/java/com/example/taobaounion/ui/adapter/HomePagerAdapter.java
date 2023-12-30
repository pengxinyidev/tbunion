package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.ui.fragment.HomePagerFragment;
import com.example.taobaounion.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends PagerAdapter {

    private List<Categories.DataBean> categoriesList = new ArrayList<>();
    private List<View> mViewList = new ArrayList<>();
    private Context mContext;
    private View mView;

    public HomePagerAdapter(List<View> viewList) {
        mViewList = viewList;
    }

    public HomePagerAdapter(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categoriesList.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        mView = LayoutInflater.from(mContext).inflate(R.layout.home_pager_content, null);
//        container.addView(mView);
//        return mView;
        LogUtils.w(this, "instantiateItem IN");
        Categories.DataBean dataBean = categoriesList.get(position);
        HomePagerFragment homePagerFragment = HomePagerFragment.newInstance(dataBean);
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = homePagerFragment.onCreateView(inflater, container, null);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setCategories(Categories categories) {
        categoriesList.clear();
        List<Categories.DataBean> data = categories.getData();
        categoriesList.addAll(data);
        notifyDataSetChanged();
    }
}
