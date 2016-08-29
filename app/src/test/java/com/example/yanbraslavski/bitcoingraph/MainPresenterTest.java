package com.example.yanbraslavski.bitcoingraph;

import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.di.MockAppComponent;
import com.example.yanbraslavski.bitcoingraph.di.MockGlobalModule;
import com.example.yanbraslavski.bitcoingraph.main.MainContract;
import com.example.yanbraslavski.bitcoingraph.main.MainPresenter;
import com.example.yanbraslavski.bitcoingraph.rx.eventbus.RxBus;
import com.example.yanbraslavski.bitcoingraph.utils.AppUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

/**
 * Unit tests for the main Presenter
 */
public class MainPresenterTest {

    private MainPresenter mMainPresenter;


    @Before
    public void setup() throws Exception {

        //FIXME : Does not compile :(
        MockAppComponent component = MockAppComponent.builder()
                .mockGlobalModule(new MockGlobalModule())
                .build();

        component.inject(this);

        //in case we would have annotated members in this test case
        MockitoAnnotations.initMocks(this);
        mMainPresenter = Mockito.spy(createPresenterWithFakeParams());
        assertNotNull(mMainPresenter);
    }

    /**
     * Make sure that view binding is correct.
     */
    @Test
    public void bindViewTest() throws Exception {
        //we bind a mocked view to the presenter
        final MainContract.IMainView mockView = Mockito.mock(MainContract.IMainView.class);
        mMainPresenter.bindView(mockView);

        //now we getting private bounded view via reflection
        Field field = PowerMockito.field(MainPresenter.class, "mView");
        Object boundedView = field.get(mMainPresenter);

        //make sure that bounded view exist and is exactly the view we have bounded
        assertNotNull(boundedView);
        assertSame(boundedView, mockView);
    }

    /**
     * We verify that our presenter is able to cache and restore it's own loaded
     * data
     */
    @Test
    public void cacheDataTest() throws Exception {

        //mock display model
        final MainContract.IMainView.DisplayModel mockDisplayModel =
                Mockito.mock(MainContract.IMainView.DisplayModel.class);

        //now we setting private cached data via reflection
        Field field = PowerMockito.field(MainPresenter.class, "mCachedDisplayModel");
        field.set(mMainPresenter, mockDisplayModel);

        //mock bundle object
        Bundle bundle = Mockito.mock(Bundle.class);
        doReturn(mockDisplayModel).when(bundle).getParcelable(anyString());

        //call save instance state
        mMainPresenter.onSaveInstanceState(bundle);

        //verify that data was stored to the bundle
        verify(bundle).putParcelable(anyString(), eq(mockDisplayModel));

        //create a new presenter and restore state on it from the bundle
        mMainPresenter = Mockito.spy(createPresenterWithFakeParams());
        mMainPresenter.restoreState(bundle);

        //verify that data was fetched from the bundle
        verify(bundle).getParcelable(anyString());

        //now we getting private cached data via reflection
        field = PowerMockito.field(MainPresenter.class, "mCachedDisplayModel");
        Object cachedData = field.get(mMainPresenter);

        //verify cached data is not null , and exactly the same that we stored before
        assertNotNull(cachedData);
        assertSame(mockDisplayModel, cachedData);

        //bind a view so the load data will not crash on NPE
        //alternately we could doNothing on "onDisplayModelLoaded" method call...
        mMainPresenter.bindView(Mockito.mock(MainContract.IMainView.class));

        //now call load data , we expect that data will return from cache
        mMainPresenter.loadData();

        //make sure that data is there and the onDisplayModelLoaded method is called
        verifyPrivate(mMainPresenter, times(1)).invoke("onDisplayModelLoaded", eq(mockDisplayModel));
    }


    @NonNull
    private MainPresenter createPresenterWithFakeParams() {
        RxBus rxBus = new RxBus();
        BlockChainApi api = Mockito.mock(BlockChainApi.class);
        AppUtils appUtils = Mockito.mock(AppUtils.class);
        return new MainPresenter(rxBus, api, appUtils);
    }
}