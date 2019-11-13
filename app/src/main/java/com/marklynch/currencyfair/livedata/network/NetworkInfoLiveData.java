package com.marklynch.currencyfair.livedata.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.lifecycle.LiveData;

enum ConnectionType {CONNECTED, NO_CONNECTION}

public class NetworkInfoLiveData extends LiveData<ConnectionType> {

    private ConnectivityManager connectivityManager;
    public NetworkInfoLiveData(Context context)
    {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    private ConnectivityManager.NetworkCallback networkCallback =

            new ConnectivityManager.NetworkCallback() {

                public void onAvailable(Network network) {
                    postValue(ConnectionType.CONNECTED);
                }

                public void onLost(Network network) {
                    postValue(ConnectionType.NO_CONNECTION);
                }
            };
}