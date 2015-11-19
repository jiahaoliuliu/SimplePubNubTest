package com.jiahaoliuliu.simplepubnubtest;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.prefs.PreferenceChangeEvent;

/**
 * Intent service used to retrieve and save the registration token needed
 * Extracted from here
 * https://github.com/googlesamples/google-services/blob/master/android/gcm/app/src/main/java/gcm/play/android/samples/com/gcmquickstart/RegistrationIntentService.java
 */
public class RegistrationIntentService extends IntentService {

    public static final String TAG = "RegistrationIntentService";
    public static final String INTENT_KEY_UPDATE_SERVER_TOKEN_CALLBACK =
            "com.jiahaoliuliu.simplepubnubtest.RegistrationIntentService.INTENT_KEY_UPDATE_SERVER_TOKEN_CALLBACK";

    private static final String SENT_TOKEN_TO_SERVER = "SentTokenToServer";

    private ResultReceiver mResultReceiver;
    public static final String BUNDLE_KEY_GCM_TOKEN =
            "com.jiahaoliuliu.simplepubnubtest.RegistrationIntentService.BUNDLE_KEY_GCM_TOKEN";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get the result receiver
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(INTENT_KEY_UPDATE_SERVER_TOKEN_CALLBACK)) {
            mResultReceiver = (ResultReceiver)extras.get(INTENT_KEY_UPDATE_SERVER_TOKEN_CALLBACK);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceId = InstanceID.getInstance(this);
            String token = instanceId.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();

            // TODO: Send registration token to the server
            if (mResultReceiver != null) {
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_KEY_GCM_TOKEN, token);
                mResultReceiver.send(0, bundle);
            }

        } catch (Exception e) {
            Log.d(TAG, "Faield to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
    }
}
