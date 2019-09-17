package com.example.dcmotorgprs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver {
	private static final String TAG = ConnectivityReceiver.class.getName();
	public static ConnectivityReceiverListener connectivityReceiverListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null
			                      && activeNetwork.isConnectedOrConnecting();
			
			if ( connectivityReceiverListener != null ) {
				connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
			}
		}
		catch (Exception e) {
			Log.e(TAG, "Error In onReceive : " + e);
		}
	}
	

	
}
