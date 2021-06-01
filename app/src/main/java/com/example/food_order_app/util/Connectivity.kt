package com.example.food_order_app.util

import android.content.Context
import android.net.NetworkInfo

class Connectivity {

    fun checkConnectivity(context: Context): Boolean {
        val connectivityManager  = context.getSystemService(Context.CONNECTIVITY_SERVICE)  as android.net.ConnectivityManager

        val activeNetwork : NetworkInfo? = connectivityManager.activeNetworkInfo

        if(activeNetwork?.isConnected!=null){
            return activeNetwork.isConnected
        }else {
            return false
        }
    }

}