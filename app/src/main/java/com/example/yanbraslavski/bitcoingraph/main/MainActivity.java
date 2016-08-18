package com.example.yanbraslavski.bitcoingraph.main;

import com.example.yanbraslavski.bitcoingraph.R;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * The entry point of the app.
 * It is also implements the view part of the MVP structure.
 */
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
        mMainPresenter.restoreState(savedInstanceState);
        mMainPresenter.loadData();
    }


    @Override
    public void bindPresenter(MainContract.IMainPresenter presenter) {
        mMainPresenter = presenter;
        mMainPresenter.bindView(this);
    }

    @Override
    public void unbindPresenter() {
        mMainPresenter.unbindView();
        mMainPresenter = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMainPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setDisplayModel(DisplayModel displayModel) {
        mGraphView.setDataList(displayModel.getDataList());

        //in landscape layout there is no title text view
        if (mTitleTextView != null) {
            mTitleTextView.setText(displayModel.getTitle());
        }
    }

    @Override
    protected void onDestroy() {
        //There are different approaches to MVP
        //some prefer presenters to survive orientation changes
        //I personally prefer them to be destroyed
        unbindPresenter();
        super.onDestroy();
    }

    public MainContract.IMainPresenter getMainPresenter() {
        return mMainPresenter;
    }
}