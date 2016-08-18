package com.example.yanbraslavski.bitcoingraph.connectivity;

import com.example.yanbraslavski.bitcoingraph.app.BitcoinApp;
import com.example.yanbraslavski.bitcoingraph.rx.eventbus.RxBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by yan.braslavski on 8/18/16.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    @Inject
    RxBus mRxBus;

    public NetworkStateReceiver() {
        //dagger inject
        BitcoinApp.getComponent().inject(this);
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("app", "Network connectivity change");
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Log.i("app", "Network " + ni.getTypeName() + " connected");
                mRxBus.send(new ConnectionRestoredEvent());
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.d("app", "There's no network connectivity");
                mRxBus.send(new ConnectionLostEvent());
            }
        }
    }
}