package com.example.yanbraslavski.bitcoingraph.mvp;

import com.example.yanbraslavski.bitcoingraph.utils.Preconditions;

/**
 * Created by yan.braslavski on 8/17/16.
 */

public abstract class BasePresenter<T extends IView> implements IPresenter<T> {

    protected T mView;

    @Override
    public void bindView(T view) {
        Preconditions.checkNotNull(view);
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }
}
