package com.example.yanbraslavski.bitcoingraph.main;

import com.example.yanbraslavski.bitcoingraph.R;
import com.example.yanbraslavski.bitcoingraph.app.BitcoinApp;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The entry point of the app.
 * It is also implements the view part of the MVP structure.
 */
public class MainActivity extends AppCompatActivity implements MainContract.IMainView {

    @BindView(R.id.graph_view)
    GraphView mGraphView;
    @Nullable
    @BindView(R.id.graph_title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.layout_container)
    ViewGroup mLayoutContainer;
    private View mNoConnectionView;

    @Inject
    protected MainContract.IMainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dagger inject
        BitcoinApp.getComponent().inject(this);
        ButterKnife.bind(this);

        //this view is created by inflation. It will be added and removed as needed
        mNoConnectionView = View.inflate(this, R.layout.no_connection_notification_layout, null);

        //create and bind to the presenter
        bindPresenter(mMainPresenter);
        mMainPresenter.restoreState(savedInstanceState);
        mMainPresenter.loadData();
    }


    @Override
    public void bindPresenter(MainContract.IMainPresenter presenter) {
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
    public void showDataLoadFailure(String message) {
        showReloadDataProposition(message);
    }

    private void showReloadDataProposition(String message) {
        Snackbar.make(mCoordinatorLayout, "Error Fetching Data : " + message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Reload", view -> mMainPresenter.loadData()).show();
    }

    @Override
    public void onConnectionLost() {
        mLayoutContainer.addView(mNoConnectionView, 0);
    }

    @Override
    public void onConnectionRestored() {
        mLayoutContainer.removeView(mNoConnectionView);
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