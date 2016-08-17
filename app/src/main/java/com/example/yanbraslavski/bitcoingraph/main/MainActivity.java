package com.example.yanbraslavski.bitcoingraph.main;

import com.example.yanbraslavski.bitcoingraph.R;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements MainContract.IMainView {

    private GraphView mGraphView;
    private TextView mTitleTextView;
    private MainContract.IMainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGraphView = (GraphView) findViewById(R.id.graph_view);
        mTitleTextView = (TextView) findViewById(R.id.graph_title_text_view);

        //create and bind to the presenter
        bindPresenter(new MainPresenter());
    }


    @Override
    public void bindPresenter(MainContract.IMainPresenter presenter) {
        mMainPresenter = presenter;
        mMainPresenter.bindView(this);
    }

    @Override
    public void unbindPresenter() {
        mMainPresenter = null;
    }


    @Override
    public void setDisplayModel(DisplayModel displayModel) {
        mGraphView.setDataList(displayModel.getDataList());

        //in landscape layout there is no title text view
        if (mTitleTextView != null) {
            mTitleTextView.setText(displayModel.getTitle());
        }
    }
}