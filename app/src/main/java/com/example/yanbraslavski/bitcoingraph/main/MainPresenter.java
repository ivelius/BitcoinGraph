package com.example.yanbraslavski.bitcoingraph.main;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.app.BitcoinApp;
import com.example.yanbraslavski.bitcoingraph.connectivity.ConnectionLostEvent;
import com.example.yanbraslavski.bitcoingraph.connectivity.ConnectionRestoredEvent;
import com.example.yanbraslavski.bitcoingraph.mvp.BasePresenter;
import com.example.yanbraslavski.bitcoingraph.rx.RxUtils;
import com.example.yanbraslavski.bitcoingraph.rx.eventbus.RxBus;
import com.example.yanbraslavski.bitcoingraph.utils.AppUtils;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by yan.braslavski on 8/17/16.
 * Main Presenter is a part of the main MVP.
 */
public class MainPresenter extends BasePresenter<MainContract.IMainView> implements MainContract.IMainPresenter {

    public static final String KEY_CACHED_DATA = "cached_data";
    private final CompositeSubscription mSubscriptions;
    private final BlockChainApi mApi;
    private MainContract.IMainView.DisplayModel mCachedDisplayModel;

    public MainPresenter() {
        mApi = new BlockChainApi();
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCachedDisplayModel != null)
            outState.putParcelable(KEY_CACHED_DATA, mCachedDisplayModel);
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        mCachedDisplayModel = savedInstanceState.getParcelable(KEY_CACHED_DATA);
    }

    @Override
    public void loadData() {

        //we don't want to fetch the data again if we have the
        //data already stored (maybe in some cases we will want a fresh data , but it deppends
        // on the business requirements)
        if (mCachedDisplayModel != null) {
            onDisplayModelLoaded(mCachedDisplayModel);
            return;
        }

        //if there is no data , we need to fetch it through the api
        mSubscriptions.add(mApi.getBitcoinGraph().compose(RxUtils.createBackgroundTransformer())
                //transform model data into view data
                .map(graphModel -> {
                    //use stream api to convert list of model data to list of graph data
                    List<GraphView.GraphPoint> dataList = Stream.of(graphModel.getValues())
                            .map(graphValueModel -> new GraphView.GraphPoint(graphValueModel.getX(), graphValueModel.getY()))
                            .collect(Collectors.toList());

                    return new MainContract.IMainView.DisplayModel(graphModel.getName(), dataList);
                })
                .subscribe(this::onDisplayModelLoaded, this::onDataLoadFailure));
    }

    private void onDisplayModelLoaded(final MainContract.IMainView.DisplayModel displayModel) {
        mCachedDisplayModel = displayModel;
        mView.setDisplayModel(displayModel);
    }

    private void onDataLoadFailure(Throwable throwable) {
        throwable.printStackTrace();
        mView.showDataLoadFailure(throwable.getMessage());
    }

    @Override
    public void bindView(MainContract.IMainView view) {
        super.bindView(view);

        //we want to tell user in case there is no connection right now
        if(!AppUtils.isConnected(BitcoinApp.getContext())){
            mView.onConnectionLost();
            return;
        }

        //register for connectivity events
        mSubscriptions.add(RxBus.instance.subscribeOnMainThread(ConnectionLostEvent.class,
                event -> mView.onConnectionLost()));
        mSubscriptions.add(RxBus.instance.subscribeOnMainThread(ConnectionRestoredEvent.class,
                event -> mView.onConnectionRestored()));
    }

    @Override
    public void unbindView() {
        super.unbindView();
        mSubscriptions.unsubscribe();
    }

    /**
     * Returns the cached data used by this presenter.
     * The data is not promised to exist , hence return type is optional
     */
    public Optional<MainContract.IMainView.DisplayModel> getCachedDisplayModel() {
        return (mCachedDisplayModel != null) ? Optional.of(mCachedDisplayModel) : Optional.empty();
    }
}