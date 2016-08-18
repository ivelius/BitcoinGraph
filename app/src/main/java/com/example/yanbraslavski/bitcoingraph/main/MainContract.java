package com.example.yanbraslavski.bitcoingraph.main;

import com.example.yanbraslavski.bitcoingraph.mvp.IPresenter;
import com.example.yanbraslavski.bitcoingraph.mvp.IView;
import com.example.yanbraslavski.bitcoingraph.views.GraphView;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.UiThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan.braslavski on 8/17/16.
 */
public interface MainContract {

    /**
     * Contract  for interaction between view -> presenter
     */
    @UiThread
    interface IMainView extends IView<IMainPresenter> {

        void setDisplayModel(DisplayModel displayModel);

        void showDataLoadFailure(String message);

        void onConnectionLost();

        void onConnectionRestored();

        /**
         * Describes the data model that view requires to display itself
         */
        class DisplayModel implements Parcelable {
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


            protected DisplayModel(Parcel in) {
                mTitle = in.readString();
                if (in.readByte() == 0x01) {
                    mDataList = new ArrayList<>();
                    in.readList(mDataList, GraphView.GraphPoint.class.getClassLoader());
                } else {
                    mDataList = null;
                }
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(mTitle);
                if (mDataList == null) {
                    dest.writeByte((byte) (0x00));
                } else {
                    dest.writeByte((byte) (0x01));
                    dest.writeList(mDataList);
                }
            }

            @SuppressWarnings("unused")
            public static final Parcelable.Creator<DisplayModel> CREATOR = new Parcelable.Creator<DisplayModel>() {
                @Override
                public DisplayModel createFromParcel(Parcel in) {
                    return new DisplayModel(in);
                }

                @Override
                public DisplayModel[] newArray(int size) {
                    return new DisplayModel[size];
                }
            };
        }
    }

    /**
     * Contract  for interaction between presenter -> view
     */
    interface IMainPresenter extends IPresenter<IMainView> {
        /**
         * We will offer the presenter to persist it's data
         * while activities killed in order to be restored
         */
        void onSaveInstanceState(Bundle outState);

        /**
         * We will try to restore saved data of the presenter
         */
        void restoreState(Bundle savedInstanceState);

        void loadData();

    }
}