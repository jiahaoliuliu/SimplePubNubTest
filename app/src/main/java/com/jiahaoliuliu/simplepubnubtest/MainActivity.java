package com.jiahaoliuliu.simplepubnubtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PubNub
        final Pubnub pubnub = new Pubnub(
                "demo",     // Publish key
                "demo",     // Subscribe key
                "",         // Secret key
                "",         // Cipher key
                false       // SSL on?
        );

        try {
            pubnub.subscribe("my_channel", new Callback() {

                @Override
                public void connectCallback(String channel, Object message) {
                    pubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback(){});
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.v(TAG, "Subscribe : Disconnect on channel: " + channel + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    Log.v(TAG, "Subscribe : REconnect on channel: " + channel + " : " + message.getClass() + " : "
                    + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    Log.v(TAG, "Subscribe : " + channel + " : " + message.getClass() + " : " + message.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.e(TAG, "Subscribe : Error on channel " + channel + " : " + error.toString());
                }
            });
        } catch (PubnubException e) {
            Log.e(TAG, "Error subscribing", e);
        }
    }
}
