package com.example.yanbraslavski.bitcoingraph.di;


import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.main.MainContract;
import com.example.yanbraslavski.bitcoingraph.main.MainPresenter;
import com.example.yanbraslavski.bitcoingraph.rx.eventbus.RxBus;
import com.example.yanbraslavski.bitcoingraph.utils.AppUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class MockGlobalModule {

    public MockGlobalModule() {
    }

    @Provides
    MainContract.IMainPresenter provideMainPresenter(RxBus rxBus, BlockChainApi api, AppUtils appUtils) {
        return new MainPresenter(rxBus, api, appUtils);
    }

    @Singleton
    @Provides
    RxBus provideRxBus() {
        return new RxBus();
    }

    @Singleton
    @Provides
    BlockChainApi provideApi() {
        return new BlockChainApi();
    }

    @Singleton
    @Provides
    AppUtils provideAppUtils() {
        return new AppUtils();
    }
}