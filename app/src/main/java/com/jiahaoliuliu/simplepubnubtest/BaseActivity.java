package com.jiahaoliuliu.simplepubnubtest;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.pubnub.api.Pubnub;

/**
 * Created by Jiahao on 11/24/15.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    // Internal variables
    protected Context mContext;
    protected Pubnub mPubnub;
    protected String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize variables
        mContext = this;
        mPubnub = new Pubnub(
                APISecret.PUBLISH_KEY,       // Publish key
                APISecret.SUBSCRIBE_KEY,     // Subscribe key
                APISecret.SECRET_KEY,        // Secret key
                "",                          // Cipher key
                false                        // SSL on?
        );

        // Set the device id
        uuid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        mPubnub.setUUID(uuid);

    }

    protected void displayTextToUser(final String text) {
        if (TextUtils.isEmpty(text)) {
            Log.e(TAG, "Cannot display empty text to the user");
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
