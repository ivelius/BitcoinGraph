package com.example.yanbraslavski.bitcoingraph.di;

import com.example.yanbraslavski.bitcoingraph.connectivity.NetworkStateReceiver;
import com.example.yanbraslavski.bitcoingraph.main.MainActivity;
import com.example.yanbraslavski.bitcoingraph.main.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by yan.braslavski on 8/18/16.
 */
@Singleton
@Component(
        modules = {
                GlobalModule.class
                //TODO : More modules here
        }
)
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(MainPresenter mainPresenter);

    void inject(NetworkStateReceiver networkStateReceiver);
}