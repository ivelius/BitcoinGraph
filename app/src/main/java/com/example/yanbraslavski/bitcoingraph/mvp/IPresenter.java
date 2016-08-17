package com.example.yanbraslavski.bitcoingraph.mvp;

/**
 * Created by yan.braslavski on 8/17/16.
 */

public interface IPresenter<T extends IView> {
    void bindView(T view);
    void unbindView();
}
