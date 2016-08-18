package com.example.yanbraslavski.bitcoingraph.app;


import com.example.yanbraslavski.bitcoingraph.di.AppComponent;
import com.example.yanbraslavski.bitcoingraph.di.DaggerAppComponent;
import com.example.yanbraslavski.bitcoingraph.di.GlobalModule;

import android.app.Application;
import android.content.Context;

/**
 * Created by yan.braslavski on 8/18/16.
 */

public class BitcoinApp extends Application {

    private static Context context;
    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        //It is perfectly fine to retain a static reference to app context
        //google it if you have doubts...
        context = this;
        initDaggerAppComponent();
    }

    public static Context getContext() {
        return context;
    }

    private void initDaggerAppComponent() {
        component = DaggerAppComponent.builder()
                .globalModule(new GlobalModule())
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
