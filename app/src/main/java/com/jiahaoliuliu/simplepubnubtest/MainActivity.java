package com.jiahaoliuliu.simplepubnubtest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String DEFAULT_CHANNEL = "jiahaoliuliu";

    // Views
    private Button mPublishButton;

    // Internal variables
    private Context mContext;
    private Pubnub mPubnub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        mContext = this;
        mPubnub = new Pubnub(
                APISecret.PUBLISH_KEY,       // Publish key
                APISecret.SUBSCRIBE_KEY,     // Subscribe key
                "",                          // Secret key
                "",                          // Cipher key
                false                        // SSL on?
        );

        try {
            mPubnub.subscribe(DEFAULT_CHANNEL, new Callback() {

                @Override
                public void connectCallback(String channel, Object message) {
                    // This is called when the app has connected to the channel
                    mPubnub.publish(DEFAULT_CHANNEL, DEFAULT_CHANNEL + " correctly connected", new Callback(){});
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.v(TAG, "Subscribe : Disconnect on channel: " + channel + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    Log.v(TAG, "Subscribe : Reconnect on channel: " + channel + " : " + message.getClass() + " : "
                    + message.toString());
                }

                @Override
                public void successCallback(String channel, final Object message) {
                    // This is called when a new message arrives when the message arrives
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, message.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

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

//        // Check if it is connected or not
//        mPubnub.time(new Callback() {
//            @Override
//            public void successCallback(String channel, Object message) {
//                Log.v(TAG, "Success connected " + message.toString());
//            }
//
//            @Override
//            public void errorCallback(String channel, PubnubError error) {
//                Log.e(TAG, "Error on connect" + error.toString());
//            }
//        });

        // Link the views
        mPublishButton = (Button)findViewById(R.id.publish_button);
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishDefaultMessage();
            }
        });

    }

    private void publishDefaultMessage() {
        if (mPubnub == null) {
            throw new IllegalArgumentException("The pub nub must be initialized");
        }

        mPubnub.publish(DEFAULT_CHANNEL, "Hello jiahaoliuliu from the PubNub Java SDK!", new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Message published " + message.toString());
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.e(TAG, "Error publishing the message " + error.toString());
            }
        });
    }
}
