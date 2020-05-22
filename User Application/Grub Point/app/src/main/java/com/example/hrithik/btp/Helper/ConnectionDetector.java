package com.example.hrithik.btp.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionDetector {

    /*

    This class is used to detect device connectivity to Internet.

     */

    private Context mContext;

    public ConnectionDetector(Context context) {
        this.mContext = context;
    }
    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            //   checkNetworkConnection();
            Log.d("Network", "Not Connected");
            return false;
        }
    }
}
