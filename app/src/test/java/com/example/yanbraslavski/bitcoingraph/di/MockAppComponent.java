package com.example.yanbraslavski.bitcoingraph.di;

import com.example.yanbraslavski.bitcoingraph.MainPresenterTest;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by yan.braslavski on 8/18/16.
 */
@Singleton
@Component(
        modules = {
                MockGlobalModule.class
                //TODO : More modules here
        }
)
public interface MockAppComponent extends AppComponent {
    void inject(MainPresenterTest mainPresenterTest);

}