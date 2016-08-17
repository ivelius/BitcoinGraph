package com.example.yanbraslavski.bitcoingraph.mvp;

/**
 * Created by yan.braslavski on 8/17/16.
 */

public interface IView<T extends IPresenter> {
    void bindPresenter(T presenter);

    void unbindPresenter();
}
