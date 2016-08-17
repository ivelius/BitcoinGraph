package com.example.yanbraslavski.bitcoingraph.main;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.mvp.BasePresenter;
import com.example.yanbraslavski.bitcoingraph.rx.RxUtils;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import java.util.List;

import rx.Subscription;

/**
 * Created by yan.braslavski on 8/17/16.
 */

public class MainPresenter extends BasePresenter<MainContract.IMainView> implements MainContract.IMainPresenter {

    private Subscription subscription;
    private BlockChainApi mApi = new BlockChainApi();

    public MainPresenter() {
        mApi = new BlockChainApi();
    }

    @Override
    public void bindView(MainContract.IMainView view) {
        super.bindView(view);
        loadData();
    }

    private void loadData() {
        subscription = mApi.getBitcoinGraph().compose(RxUtils.createBackgroundTransformer())
                //transform model data into graph data
                .map(graphModel -> {
                    //use stream api to convert list of model data to list of graph data
                    List<GraphView.GraphPoint> dataList = Stream.of(graphModel.getValues())
                            .map(graphValueModel -> new GraphView.GraphPoint(graphValueModel.getX(), graphValueModel.getY()))
                            .collect(Collectors.toList());

                    return new MainContract.IMainView.DisplayModel(graphModel.getName(), dataList);
                })

                .subscribe(
                        displayModel -> mView.setDisplayModel(displayModel),
                        throwable -> throwable.printStackTrace()
                );
    }

    @Override
    public void unbindView() {
        super.unbindView();
        if (subscription != null) subscription.unsubscribe();
    }
}
