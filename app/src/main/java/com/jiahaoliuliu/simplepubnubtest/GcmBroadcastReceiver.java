package com.jiahaoliuliu.simplepubnubtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GcmBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GcmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Implement this
        Log.v(TAG, "New intent received " + intent);
    }
}
