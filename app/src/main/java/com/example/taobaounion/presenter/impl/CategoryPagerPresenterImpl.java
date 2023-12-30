package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.presenter.ICategoryPagerPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ICategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Url;

public class CategoryPagerPresenterImpl implements ICategoryPagerPresenter {

    private Integer mCurrentPage;

    private CategoryPagerPresenterImpl() {

    }

    private static ICategoryPagerPresenter sInstance = null;

    public static ICategoryPagerPresenter getInstance() {
        if (sInstance == null) {
            sInstance = new CategoryPagerPresenterImpl();
        }
        return sInstance;
    }

    private List<ICategoryPagerCallback> mCallbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(ICategoryPagerCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(ICategoryPagerCallback callback) {
        mCallbacks.remove(callback);
    }

    private Map<Integer, Integer> pagesInfo = new HashMap<>();
    public static final int DEFAULT_PAGE = 1;

    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoading();
            }
        }

        Integer targetPage = pagesInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagesInfo.put(categoryId, targetPage);
        }
        Call<HomePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
                LogUtils.d(CategoryPagerPresenterImpl.this, "code = " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent pagerContent = response.body();
                    LogUtils.d(CategoryPagerPresenterImpl.this, "pageContent = " + pagerContent);
                    //把数据给到UI
                    handleHomePageContentResult(pagerContent, categoryId);
                } else {
                    handleNetworkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(CategoryPagerPresenterImpl.this, "failure, t = " + t.toString() );
                handleNetworkError(categoryId);
            }
        });
    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        String homePagerUrl = UrlUtils.createHomePagerUrl(categoryId, targetPage);
        LogUtils.d(this, "homePagerUrl = " + homePagerUrl );
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<HomePagerContent> task = api.getHomePageContent(homePagerUrl);
        return task;
    }

    private void handleNetworkError(int categoryId) {
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onError();
            }
        }
    }

    private void handleHomePageContentResult(HomePagerContent pagerContent, int categoryId) {
        List<HomePagerContent.DataBean> data = pagerContent.getData();
        for (ICategoryPagerCallback callback : mCallbacks) {

            if (callback.getCategoryId() == categoryId) {
                if ((pagerContent == null || data.size() == 0)) {
                    callback.onEmpty();
                } else {
                    List<HomePagerContent.DataBean> looperData = data.subList(data.size() - 5, data.size());//最后五个商品轮播
                    callback.onLooperListLoaded(looperData);
                    callback.onContentLoaded(data);
                }
            }

        }

    }

    @Override
    public void loadMore(int categoryId) {
        //加载更多的数据

        //1.拿到当前页面
        mCurrentPage = pagesInfo.get(categoryId);
        if (pagesInfo == null) {
            mCurrentPage = 1;
        }
        //2.页码++
        mCurrentPage++;
        //3.加载数据
        Call<HomePagerContent> task = createTask(categoryId, mCurrentPage);
        //4.处理数据结果
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
                LogUtils.d(this, "code = " +code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent result = response.body();
                    LogUtils.d(this, "result ===> " + result);
                    handleLoadMoreResult(result, categoryId);
                } else {
                    handleLoadMoreError(categoryId, mCurrentPage);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(this, t.toString());
                handleLoadMoreError(categoryId, mCurrentPage);
            }
        });
    }

    private void handleLoadMoreResult(HomePagerContent result, int categoryId) {
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (result == null || result.getData().size() == 0 ) {
                    callback.onLoaderMoreEmpty();
                } else {
                    callback.onLoaderMoreLoaded(result.getData());
                }
            }
        }

    }

    private void handleLoadMoreError(int categoryId, int currentPage) {
        mCurrentPage--;
        pagesInfo.put(categoryId, mCurrentPage);
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoaderMoreError();
            }
        }

    }

    @Override
    public void reload(int categoryId) {

    }
}
