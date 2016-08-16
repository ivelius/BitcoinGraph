package com.example.yanbraslavski.bitcoingraph;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.rx.RxUtils;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import rx.Subscription;


public class MainActivity extends AppCompatActivity {

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphView gv = (GraphView) findViewById(R.id.graph_view);

        BlockChainApi api = new BlockChainApi();
        subscription = api.getBitcoinGraph().compose(RxUtils.createBackgroundTransformer())
                //transform model data into graph data
                .map(graphModel ->
                        //use stream api to convert list of model data to list of graph data
                        Stream.of(graphModel.getValues())
                                .map(graphValueModel -> new GraphView.GraphPoint(graphValueModel.getX(), graphValueModel.getY()))
                                .collect(Collectors.toList()))

                .subscribe((dataList) -> {
                    //success
                    Log.d("yan", dataList.toString());
                    gv.setDataList(dataList);
                }, throwable -> {
                    //failiure
                    throwable.printStackTrace();
                });
    }
}
