package com.example.yanbraslavski.bitcoingraph.main;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.mvp.BasePresenter;
import com.example.yanbraslavski.bitcoingraph.rx.RxUtils;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;

import java.util.List;

import rx.Subscription;

/**
 * Created by yan.braslavski on 8/17/16.
 */

public class MainPresenter extends BasePresenter<MainContract.IMainView> implements MainContract.IMainPresenter {

    public static final String KEY_CACHED_DATA = "cached_data";
    private Subscription subscription;
    private BlockChainApi mApi = new BlockChainApi();
    private MainContract.IMainView.DisplayModel mCachedDisplayModel;

    public MainPresenter() {
        mApi = new BlockChainApi();
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
        subscription = mApi.getBitcoinGraph().compose(RxUtils.createBackgroundTransformer())
                //transform model data into view data
                .map(graphModel -> {
                    //use stream api to convert list of model data to list of graph data
                    List<GraphView.GraphPoint> dataList = Stream.of(graphModel.getValues())
                            .map(graphValueModel -> new GraphView.GraphPoint(graphValueModel.getX(), graphValueModel.getY()))
                            .collect(Collectors.toList());

                    return new MainContract.IMainView.DisplayModel(graphModel.getName(), dataList);
                })
                .subscribe(this::onDisplayModelLoaded,
                        throwable -> throwable.printStackTrace()
                );
    }

    private void onDisplayModelLoaded(final MainContract.IMainView.DisplayModel displayModel) {
        mCachedDisplayModel = displayModel;
        mView.setDisplayModel(displayModel);
    }

    @Override
    public void unbindView() {
        super.unbindView();
        if (subscription != null) subscription.unsubscribe();
    }
}
