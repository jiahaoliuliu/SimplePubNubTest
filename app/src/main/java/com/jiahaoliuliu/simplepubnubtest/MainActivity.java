package com.jiahaoliuliu.simplepubnubtest;

import android.content.Context;
import android.provider.Settings;
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
    private Button mSubscribeButton;
    private Button mPublishButton;
    private Button mUnsubscribeButton;

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

        // Set the device id
        String uuid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        mPubnub.setUUID(uuid);

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

        // Presence
        // The presence works exactly as another channel. When the app creates presence,
        // the app will subscribe the channel channelName + "-pnpres". The message is an json data
        // i.e. {"action":"join","timestamp":1447184765,"uuid":"db7b661b-376a-4652-8955-70833b22a6e4","occupancy":4}
        try {
            mPubnub.presence(DEFAULT_CHANNEL, new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    Log.v(TAG, "CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.v(TAG, "DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void reconnectCallback(String channel, Object message) {
                    Log.v(TAG, "RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    Log.v(TAG, "New state arrived " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.e(TAG, "ERROR on channel " + channel
                            + " : " + error.toString());
                }
            });
        } catch (PubnubException e) {
            Log.e(TAG, "Error detecting the presence ", e);
        }

        // Link the views
        mSubscribeButton = (Button)findViewById(R.id.subscribe_button);
        mSubscribeButton.setOnClickListener(mOnClickListener);

        mPublishButton = (Button)findViewById(R.id.publish_button);
        mPublishButton.setOnClickListener(mOnClickListener);

        mUnsubscribeButton = (Button)findViewById(R.id.unsubscribe_button);
        mUnsubscribeButton.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.subscribe_button:
                    subscribe();
                    break;
                case R.id.publish_button:
                    publishDefaultMessage();
                    break;
                case R.id.unsubscribe_button:
                    unsubscribe();
                    break;
            }
        }
    };

    private void publishDefaultMessage() {
        if (mPubnub == null) {
            return;
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

    private void subscribe() {
        if (mPubnub == null) {
            return;
        }

        try {
            mPubnub.subscribe(DEFAULT_CHANNEL, new Callback() {

                @Override
                public void connectCallback(String channel, Object message) {
                    // This is called when the app has connected to the channel
                    Log.v(TAG, "Correctly connected to the channel");
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
    }

    private void unsubscribe() {
        if (mPubnub == null) {
            return;
        }

        mPubnub.unsubscribe(DEFAULT_CHANNEL);
    }
}
