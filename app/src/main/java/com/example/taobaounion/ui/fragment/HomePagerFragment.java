package com.example.taobaounion.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.model.domain.Categories;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.presenter.impl.CategoryPagerPresenterImpl;
import com.example.taobaounion.ui.adapter.HomepageContentAdapter;
import com.example.taobaounion.ui.adapter.LooperPagerAdapter;
import com.example.taobaounion.utils.Constants;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.SizeUtils;
import com.example.taobaounion.utils.ToastUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback {

    private ICategoryPagerPresenter mCategoryPagerPresenter;
    private int mMaterialId;
    private HomepageContentAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;

    public static HomePagerFragment newInstance(Categories.DataBean category) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();

        LogUtils.w("HomePagerFragment", "HomePagerFragment newInstance IN");

        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID, category.getId());
        homePagerFragment.setArguments(bundle);

        return homePagerFragment;
    }


    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.looper_pager)
    public ViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitle;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;



    @Override
    protected int getRootViewResId() {
        LogUtils.w(this, "getRootViewResId IN");
        return R.layout.home_pager_content;
    }

    @Override
    protected void initPresenter() {
        mCategoryPagerPresenter = CategoryPagerPresenterImpl.getInstance();
        mCategoryPagerPresenter.registerViewCallback(this);

    }

    @Override
    protected void initListener() {
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int measuredHeight = homePagerParent.getMeasuredHeight();
//                LogUtils.d(this, "measuredHeight = " + measuredHeight);
                ViewGroup.LayoutParams layoutParams = mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0) {
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

            }
        });

        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if ( mLooperPagerAdapter.getDataSize() == 0) {
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                //切换指示器
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(this, "load more...");

                //加载更多内容
                if (mCategoryPagerPresenter != null) {
                    mCategoryPagerPresenter.loadMore(mMaterialId);
                }

//                mTwinklingRefreshLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTwinklingRefreshLayout.finishLoadmore();
//                        Toast.makeText(getViewContext(), "load more", Toast.LENGTH_SHORT).show();
//                    }
//                }, 3000);
            }
        });
    }

    private void updateLooperIndicator(int position) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == position) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected void initView(View rootView) {
        LogUtils.d(this, "initView IN");
        //设置布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;
                //单位：px
            }
        });
        //创建适配器
        mContentAdapter = new HomepageContentAdapter();
        //设置适配器
        mContentList.setAdapter(mContentAdapter);

        //轮播图适配器
        mLooperPagerAdapter = new LooperPagerAdapter();
        looperPager.setAdapter(mLooperPagerAdapter);

        //设置Refresh相关属性
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableLoadmore(true);
//        mTwinklingRefreshLayout.setBottomView();

    }

    @Override
    protected void loadData() {

        LogUtils.d(this, "loadData IN");

        Bundle bundle = getArguments();
        String title = bundle.getString(Constants.KEY_HOME_PAGER_TITLE);
        mMaterialId = bundle.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID);

        LogUtils.d(this, "title = " + title);
        LogUtils.d(this, "materialId = " + mMaterialId);

        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.getContentByCategoryId(mMaterialId);
        }
        if (currentCategoryTitle != null) {
            currentCategoryTitle.setText(title);
        }
    }

    @Override
    public void onContentLoaded(List<HomePagerContent.DataBean> contents) {
//        LogUtils.d(this, "test onContentLoaded");
        setUpstate(State.SUCCESS);
//        LogUtils.d(this, "contents ==> " + contents.toString());
        mContentAdapter.setData(contents);
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onLoading() {
        setUpstate(State.LOADING);
    }

    @Override
    public void onError() {
        //网络错误
        setUpstate(State.ERROR);
    }

    @Override
    public void onEmpty() {
        setUpstate(State.EMPTY);
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtils.showToast("more error");
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtils.showToast("no more");
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //添加到适配器数据的底部
        mContentAdapter.addData(contents);
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
        ToastUtils.showToast("load more");

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataBean> contents) {
        LogUtils.d(this, "looper size ===> " + contents.size());
        mLooperPagerAdapter.setData(contents);
//        looperPager.setOffscreenPageLimit(contents.size());

        //设置到中间点
//        looperPager.setCurrentItem(Integer.MAX_VALUE / 2);

        //中间点 % 数据的size 不一定为0， 所以不是显示的第一张图片，处理如下
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        int targetCenterPosition = (Integer.MAX_VALUE / 2) - dx;
        looperPager.setCurrentItem(targetCenterPosition);
        LogUtils.d(this, "url ===> " + contents.get(0).getPict_url());

        looperPointContainer.removeAllViews();


        //添加点
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(getViewContext());
            int size = SizeUtils.dip2px(getViewContext(), 8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.leftMargin = SizeUtils.dip2px(getViewContext(), 5);
            layoutParams.rightMargin = SizeUtils.dip2px(getViewContext(), 5);
            point.setLayoutParams(layoutParams);

            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }

            looperPointContainer.addView(point);
        }
    }

    @Override
    protected void release() {
        if (mCategoryPagerPresenter != null) {
            mCategoryPagerPresenter.unregisterViewCallback(this);
        }
    }
}
