package com.example.yanbraslavski.bitcoingraph.api;


import com.example.yanbraslavski.bitcoingraph.BuildConfig;
import com.example.yanbraslavski.bitcoingraph.api.models.GraphModel;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class BlockChainApi {

    private static final String REQUEST_DATA_FORMAT = "json";
    private final BlockChainService mService;

    public BlockChainApi() {
        mService = new Retrofit.Builder()
                .baseUrl(BuildConfig.ENDPOINT)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(BlockChainService.class);
    }

    /**
     * Request observable list of availible airlines
     */
    public Observable<GraphModel> getBitcoinGraph() {
        return mService.getBitcoinGraph(REQUEST_DATA_FORMAT);
    }
}