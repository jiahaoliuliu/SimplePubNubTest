package com.jiahaoliuliu.simplepubnubtest;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiahaoliuliu.simplepubnubtest.model.Message;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Class used to check how the SDK provided by PubNub works. The data collected so far are:
 * 1. The user must subscribe to a channel to receive a message
 * 2. When the user publishes any message to a channel, all the users will receive that message, including
 * the sender itself
 * 3. The user can unsubscribe to a channel in any moment
 * 4. At the moment when the user unsubscribe a channel, the user will not receive any message
 * 5. If the user unsubscribes a channel and then, subscribes again, he will lost all the previous messages
 * 6. The presence works exactly as a channel. The user is subscribed to the channel channelName+ "-pnpres"
 * 7. After the user has unsubscribed a channel, he is still able to publish message to that channel
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String DEFAULT_CHANNEL = "jiahaoliuliu";

    private static final Long YESTERDAY = 1447099666000L;

    // Views
    private Button mSubscribeButton;
    private Button mPublishButton;
    private Button mHistoryButton;
    private Button mChannelsListButton;
    private Button mUsersListButton;
    private Button mUnsubscribeButton;

    // Internal variables
    private Context mContext;
    private Pubnub mPubnub;
    private int mMessageCounter = 0;
    private String uuid;
    private String gcmToken;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        mContext = this;
        mPubnub = new Pubnub(
                APISecret.PUBLISH_KEY,       // Publish key
                APISecret.SUBSCRIBE_KEY,     // Subscribe key
                APISecret.SECRET_KEY,        // Secret key
                "",                          // Cipher key
                false                        // SSL on?
        );
        mGson = new Gson();

        // Set the device id
        uuid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        mPubnub.setUUID(uuid);

        // Link the views
        mSubscribeButton = (Button)findViewById(R.id.subscribe_button);
        mSubscribeButton.setOnClickListener(mOnClickListener);

        mPublishButton = (Button)findViewById(R.id.publish_button);
        mPublishButton.setOnClickListener(mOnClickListener);

        mHistoryButton = (Button)findViewById(R.id.history_button);
        mHistoryButton.setOnClickListener(mOnClickListener);

        mChannelsListButton = (Button)findViewById(R.id.channels_list_button);
        mChannelsListButton.setOnClickListener(mOnClickListener);

        mUsersListButton = (Button)findViewById(R.id.users_list_button);
        mUsersListButton.setOnClickListener(mOnClickListener);

        mUnsubscribeButton = (Button)findViewById(R.id.unsubscribe_button);
        mUnsubscribeButton.setOnClickListener(mOnClickListener);

        // Get the channel groups that the user has subscribed
        mPubnub.channelGroupListChannels(uuid, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Channel group retrieved " + message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.e(TAG, "Error retrieving the group of channels " + error.getErrorString());
            }
        });

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
                case R.id.history_button:
                    showHistory();
                    break;
                case R.id.channels_list_button:
                    showChannelsList();
                    break;
                case R.id.users_list_button:
                    showUsersList();
                    break;
                case R.id.unsubscribe_button:
                    unsubscribe();
                    break;
            }
        }
    };

    // Subscribe
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
                    displayTextToUser("User correctly subscribed to the channel " + channel + ". " + message);
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.v(TAG, "Subscribe : Disconnect on channel: " + channel + " : " + message.getClass() + " : "
                            + message.toString());
                    displayTextToUser("User disconnected from the channel " + channel + ". " + message);
                }

                public void reconnectCallback(String channel, Object message) {
                    Log.v(TAG, "Subscribe : Reconnect on channel: " + channel + " : " + message.getClass() + " : "
                            + message.toString());
                    displayTextToUser("User reconnected to the channel " + channel + ". " + message);
                }

                @Override
                public void successCallback(String channel, final Object message) {
                    // This is called when a new message arrives when the message arrives
                    Log.v(TAG, "Subscribe : " + channel + " : " + message.getClass() + " : " + message.toString());

                    Message messageReceived = mGson.fromJson(message.toString(), Message.class);
                    Log.v(TAG, "Message received " + messageReceived.toString());

                    displayTextToUser("New message from the channel " + channel + " received. The message is " + message);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.e(TAG, "Subscribe : Error on channel " + channel + " : " + error.toString());
                    displayTextToUser("Error subscribing to the channel " + channel + ". " + error.getErrorString());
                }
            });

        } catch (PubnubException e) {
            Log.e(TAG, "Error subscribing", e);
        }

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
                    displayTextToUser("New state arrived to the channel " + channel + ". " + message);
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

        // Register for token
        Intent startRegistrationIntentServiceIntent = new Intent(this, RegistrationIntentService.class);
        startRegistrationIntentServiceIntent.putExtra(
            RegistrationIntentService.INTENT_KEY_UPDATE_SERVER_TOKEN_CALLBACK, new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultData == null) {
                        Log.e(TAG, "Error getting gcm tokens. The result data is null");
                        return;
                    }
                    gcmToken = resultData.getString(RegistrationIntentService.BUNDLE_KEY_GCM_TOKEN);
                    Log.v(TAG, "Token received " + gcmToken);
                    mPubnub.enablePushNotificationsOnChannel(DEFAULT_CHANNEL, gcmToken, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.v(TAG, "Push notification correctly enabled for the channel " + DEFAULT_CHANNEL +
                                    ". " + message);
                            displayTextToUser("Push notification correctly enabled for the channel " + DEFAULT_CHANNEL +
                                    ". " + message);
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            Log.e(TAG, "Error enabling the push notification for the channel " + DEFAULT_CHANNEL +
                                    ". " + error.getErrorString());
                            displayTextToUser("Error enabling the push notification for the channel " + DEFAULT_CHANNEL +
                                    ". " + error.getErrorString());
                        }
                    });
                }
            });
        startService(startRegistrationIntentServiceIntent);

    }

    // Publish
    private void publishDefaultMessage() {
        if (mPubnub == null) {
            return;
        }

        Message dummyMessage = new Message();
        dummyMessage.setMessageId(String.valueOf(mMessageCounter));
        dummyMessage.setMessageContent("This is a simple message");
        dummyMessage.setSenderId("Jiahao Liu Liu");
        dummyMessage.setTime(new Date().getTime());

        String dummyMessageJson = mGson.toJson(dummyMessage);

        Log.v(TAG, "Dummy message " + dummyMessageJson);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(dummyMessageJson);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing the json object", e);
            return;
        }

        mPubnub.publish(DEFAULT_CHANNEL, jsonObject
                , new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Message published " + message.toString());
                mMessageCounter++;
                displayTextToUser("Message correctly published on the channel " + channel + ". " + message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.e(TAG, "Error publishing the message " + error.toString());
                displayTextToUser("Error publishing the message to the channel " + channel + ". " + error.getErrorString());
            }
        });
    }

    // History
    private void showHistory() {
        if (mPubnub == null) {
            return;
        }

        // Get the history information
        mPubnub.history(DEFAULT_CHANNEL, YESTERDAY, -1, 1000, true, new Callback() {
            public void successCallback(String channel, Object response) {
                Log.v(TAG, "Historical data received " + response.toString());
                displayTextToUser("Showing the historical information from the channel " + channel + ". " + response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                Log.v(TAG, "Error receiving historical data " + error.toString());
                displayTextToUser("Error getting historical information from the channel " + channel + ". " + error.toString());
            }
        });
    }

    /**
     * Show the list of channels that the current user is subscribed on
     */
    private void showChannelsList() {
        if (mPubnub == null) {
            return;
        }

        mPubnub.whereNow(uuid, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Getting the list of channels " + message);
                displayTextToUser("The list of channels that the user has subscribed on are " + message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.v(TAG, "Error getting the list of channels " + error);
                displayTextToUser("Error getting the list of the channels that the user has subscribed on. " + error.toString());
            }
        });
    }


    /**
     * Show the list of users that the current user is subscribed on the actual channel
     */
    private void showUsersList() {
        if (mPubnub == null) {
            return;
        }

        mPubnub.hereNow(DEFAULT_CHANNEL, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Getting the list of users " + message);
                displayTextToUser("The list of the users in this channel are " + message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.v(TAG, "Error getting the list of users " + error);
                displayTextToUser("Error getting the list of users in the channel ." + error.getErrorString());
            }
        });
    }

    private void unsubscribe() {
        if (mPubnub == null) {
            return;
        }

        mPubnub.unsubscribe(DEFAULT_CHANNEL);
        Log.v(TAG, "User unsubscribed from the channel " + DEFAULT_CHANNEL);
        displayTextToUser("User unsubscribed from the channel " + DEFAULT_CHANNEL);
    }

    private void displayTextToUser(final String text) {
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
