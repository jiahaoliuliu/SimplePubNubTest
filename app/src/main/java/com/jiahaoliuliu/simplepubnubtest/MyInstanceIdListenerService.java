package com.jiahaoliuliu.simplepubnubtest;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Class used to call the RegistrationIntentService for new Token
 * Extracted from
 * https://github.com/googlesamples/google-services/blob/master/android/gcm/app/src/main/java/gcm/play/android/samples/com/gcmquickstart/MyInstanceIDListenerService.java
 */
public class MyInstanceIdListenerService extends InstanceIDListenerService {

    /**
     * Call if InstanceId token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceId provider
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable)
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
