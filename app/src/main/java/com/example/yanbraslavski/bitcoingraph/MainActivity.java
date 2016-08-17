package com.example.yanbraslavski.bitcoingraph;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.example.yanbraslavski.bitcoingraph.api.BlockChainApi;
import com.example.yanbraslavski.bitcoingraph.rx.RxUtils;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import rx.Subscription;


public class MainActivity extends AppCompatActivity {

    private Subscription subscription;
    private GraphView mgraphView;
    private TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mgraphView = (GraphView) findViewById(R.id.graph_view);
        mTitleTextView = (TextView) findViewById(R.id.graph_title_text_view);

        BlockChainApi api = new BlockChainApi();
        subscription = api.getBitcoinGraph().compose(RxUtils.createBackgroundTransformer())
                //transform model data into graph data
                .map(graphModel -> {
                    //use stream api to convert list of model data to list of graph data
                    List<GraphView.GraphPoint> dataList = Stream.of(graphModel.getValues())
                            .map(graphValueModel -> new GraphView.GraphPoint(graphValueModel.getX(), graphValueModel.getY()))
                            .collect(Collectors.toList());

                    return new DisplayModel(graphModel.getName(), dataList);
                })

                .subscribe((displayModel) -> {
                    //success
                    mgraphView.setDataList(displayModel.getDataList());

                    //in landscape layout there is no title text view
                    if (mTitleTextView != null) {
                        mTitleTextView.setText(displayModel.getTitle());
                    }

                }, throwable -> {
                    //failiure
                    throwable.printStackTrace();
                });
    }

    public static class DisplayModel {
        String mTitle;
        List<GraphView.GraphPoint> mDataList;

        public DisplayModel(String title, List<GraphView.GraphPoint> dataList) {
            mDataList = dataList;
            mTitle = title;
        }

        public List<GraphView.GraphPoint> getDataList() {
            return mDataList;
        }

        public String getTitle() {
            return mTitle;
        }
    }
}