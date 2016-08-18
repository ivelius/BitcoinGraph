package com.example.yanbraslavski.bitcoingraph.di;


import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.main.MainContract;
import com.example.yanbraslavski.bitcoingraph.main.MainPresenter;
import com.example.yanbraslavski.bitcoingraph.rx.eventbus.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class GlobalModule {

    public GlobalModule() {
    }


    @Singleton
    @Provides
    RxBus providePreferences() {
        return new RxBus();
    }

    @Provides
    MainContract.IMainPresenter provideMainPresenter() {
        return new MainPresenter();
    }

    @Singleton
    @Provides
    BlockChainApi provideApi() {
        return new BlockChainApi();
    }
}