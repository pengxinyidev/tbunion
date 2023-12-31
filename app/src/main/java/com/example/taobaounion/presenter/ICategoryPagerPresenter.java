package com.example.taobaounion.presenter;

import com.example.taobaounion.base.IBasePresenter;
import com.example.taobaounion.view.ICategoryPagerCallback;

public interface ICategoryPagerPresenter extends IBasePresenter<ICategoryPagerCallback> {

    /**
     * 根据分类id获取内容
     * @param categoryId
     */
    void getContentByCategoryId(int categoryId);

    void loadMore(int categoryId);

    void reload(int categoryId);


}
