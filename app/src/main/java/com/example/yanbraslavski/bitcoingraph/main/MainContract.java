package com.example.yanbraslavski.bitcoingraph.main;

import com.example.yanbraslavski.bitcoingraph.mvp.IPresenter;
import com.example.yanbraslavski.bitcoingraph.mvp.IView;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.support.annotation.UiThread;

import java.util.List;

/**
 * Created by yan.braslavski on 8/17/16.
 */
public interface MainContract {

    @UiThread
    interface IMainView extends IView<IMainPresenter> {
        //TODO : contract here for interaction between view -> presenter

        void setDisplayModel(DisplayModel displayModel);

        /**
         * Describes the data model that view requires to display itself
         */
        class DisplayModel {
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

    interface IMainPresenter extends IPresenter<IMainView> {
        //TODO : contract here for interaction between presenter -> view
    }
}
