package com.example.yanbraslavski.bitcoingraph.di;


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
}