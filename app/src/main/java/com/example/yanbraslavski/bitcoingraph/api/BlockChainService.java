package com.example.yanbraslavski.bitcoingraph.api;


import com.example.yanbraslavski.bitcoingraph.api.models.GraphModel;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface BlockChainService {

    /**
     * Request observable of graph model
     */
    @GET("charts/market-price")
    Observable<GraphModel> getBitcoinGraph(@Query("format") String format);
}
