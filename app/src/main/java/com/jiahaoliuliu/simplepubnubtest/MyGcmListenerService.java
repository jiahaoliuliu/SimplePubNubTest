package com.jiahaoliuliu.simplepubnubtest;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;


/**
 *
 * Created by Jiahao on 11/19/15.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private static final String BUNDLE_KEY_MESSAGE = "message";


    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "Message received from " + from + ": " + data.getString(BUNDLE_KEY_MESSAGE));
    }
}
