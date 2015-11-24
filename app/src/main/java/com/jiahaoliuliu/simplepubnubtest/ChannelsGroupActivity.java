package com.jiahaoliuliu.simplepubnubtest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class ChannelsGroupActivity extends BaseActivity {

    private static final String TAG = "ChannelsGroupActivity";

    private static final String DEFAULT_CHANNELS_GROUP = "jiahaoliuliu_group";

    private static final String DEFAULT_CHANNEL_PREFIX = "default_channel";

    // Internal variables
    private Context mContext;
    private int nextChannelToAdd = 1;

    // Views
    private Button mAuditButton;
    private Button mGrantPermissionButton;
    private Button mSubscribeChannelsGroupButton;
    private Button mAddChannelButton;
    private Button mListingButton;
    private Button mRemoveChanelButton;
    private Button mUnsubscribeChannelsGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels_group);

        // Initialie the internal variables
        mContext = this;

        // Link the views
        mAuditButton = (Button) findViewById(R.id.audit_permission_button);
        mAuditButton.setOnClickListener(mOnClickListener);

        mGrantPermissionButton = (Button) findViewById(R.id.grant_permission_button);
        mGrantPermissionButton.setOnClickListener(mOnClickListener);

        mSubscribeChannelsGroupButton = (Button) findViewById(R.id.subscribe_channels_group_button);
        mSubscribeChannelsGroupButton.setOnClickListener(mOnClickListener);

        mAddChannelButton = (Button) findViewById(R.id.add_channel_button);
        mAddChannelButton.setOnClickListener(mOnClickListener);

        mListingButton = (Button) findViewById(R.id.listing_button);
        mListingButton.setOnClickListener(mOnClickListener);

        mRemoveChanelButton = (Button) findViewById(R.id.remove_channel_button);
        mRemoveChanelButton.setOnClickListener(mOnClickListener);

        mUnsubscribeChannelsGroupButton = (Button) findViewById(R.id.unsubscribe_channels_group_button);
        mUnsubscribeChannelsGroupButton.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.audit_permission_button:
                    auditPermissions();
                    break;
                case R.id.grant_permission_button:
                    grantPermissions();
                    break;
                case R.id.subscribe_channels_group_button:
                    subscribeToChannelsGroup();
                    break;
                case R.id.add_channel_button:
                    addChannel();
                    break;
                case R.id.listing_button:
                    listing();
                    break;
                case R.id.remove_channel_button:
                    removeChannel();
                    break;
                case R.id.unsubscribe_channels_group_button:
                    unsubscribeChannelGroup();
                    break;
            }
        }
    };

    private void auditPermissions() {
        // Audit to make sure we have the righ permissions
        mPubnub.pamAuditChannelGroup(DEFAULT_CHANNELS_GROUP, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "The permission for the default channel group is " + message);
                // Example of data returned
                //{"level":"channel-group","subscribe_key":"sub-c-f7ec3fa2-87d4-11e5-83e3-02ee2ddab7fe","channel-groups":{}}
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.v(TAG, "Error auditing the permissions. " + error.getErrorString() + ". " + error.errorCode);
            }
        });
    }

    private void grantPermissions() {
        mPubnub.pamGrantChannelGroup(DEFAULT_CHANNELS_GROUP, true, true, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Granting the permission for the channel group works " + message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.v(TAG, "Error garanting the permission " + error.getErrorString() + ". " + error.errorCode);
            }
        });
    }

    private void subscribeToChannelsGroup() {
        // Subscribe to a channel group
        try {
            mPubnub.channelGroupSubscribe(DEFAULT_CHANNELS_GROUP, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.v(TAG, "SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());

                    displayTextToUser("SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.v(TAG, "SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());

                    displayTextToUser("SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
//                    if (error.errorCode == PubnubError.PNERR_TIMEOUT)
//                        mPubnub.disconnectAndResubscribe();
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.v(TAG, "SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());

                    displayTextToUser("SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void reconnectCallback(String channel, Object message) {
                    Log.v(TAG, "SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());

                    displayTextToUser("SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.v(TAG, "SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());

                    displayTextToUser("SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }
            });
        } catch (PubnubException pubnubException) {
            Log.e(TAG, "Error subscribing to the channel group ", pubnubException);
        }
    }

    private void addChannel() {
        String channelName = DEFAULT_CHANNEL_PREFIX + String.valueOf(nextChannelToAdd);
        Log.v(TAG, "Adding the channel " + channelName + " to the channel group " + DEFAULT_CHANNELS_GROUP);

        mPubnub.channelGroupAddChannel(DEFAULT_CHANNELS_GROUP,channelName, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Correctly added the channel " + channel + " to the channel group. " + message);
                nextChannelToAdd++;
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.e(TAG, "Error adding the channel " + channel + " to the channel group. " + error.getErrorString()
                + " error Code " + error.errorCode);
            }

        });
    }

    private void listing() {
        mPubnub.channelGroupListChannels(DEFAULT_CHANNELS_GROUP, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.v(TAG, "Got the list of channels in the channels group " + message);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.v(TAG, "Error getting the list of channels in the channels group." + error.getErrorString() + ", " + error.errorCode);
            }
        });
    }

    private void removeChannel() {
        // TODO: implement this
    }

    private void unsubscribeChannelGroup() {
        // TODO: Implement this.
    }
}
