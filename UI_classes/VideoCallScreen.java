package com.example.mmtapp.UI_classes;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mmtapp.R;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class VideoCallScreen extends AppCompatActivity {
    URL serverURL;
    BroadcastReceiver broadcastReceiver;
    LinearLayout joinButton;
    EditText meetingRoomTextView;
    String meetingRoom;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_call_screen);
        meetingRoomTextView=findViewById(R.id.meeting_passCodeEditText);
        joinButton=findViewById(R.id.joinButton);

        //Start meet
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions
                    = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        //setup default conference options
        //registerForBroadcastMessages();
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingRoom = meetingRoomTextView.getText().toString();
                if (meetingRoom.isEmpty()) {
                    meetingRoomTextView.setError("Enter your room");
                    meetingRoomTextView.requestFocus();
                    return;
                }
                if (Integer.parseInt(meetingRoom) == 186500) {
                    Toast.makeText(getApplicationContext(), "Launching", Toast.LENGTH_SHORT).show();
                    startVideoCall();
                } else {
                    meetingRoomTextView.setError("Pass code incorrect");
                    meetingRoomTextView.requestFocus();
                }
            }
        });


        //Initialize broadcast receiver to listen to incoming calls
       /* broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBroadcastReceived(intent);
            }
        };*/
    }

    private void startVideoCall() {
        // Build options object for joining the conference. The SDK will merge the default
        // one we set earlier and this one when joining.
        try {

            JitsiMeetConferenceOptions roomOptions
                    = new JitsiMeetConferenceOptions.Builder()
                   // .setServerURL(serverURL)
                    .setRoom("MMT Meeting Room")
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeetActivity.launch(VideoCallScreen.this, roomOptions);

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
        intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());

        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }

    @Override
    protected void onDestroy() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    //My own private method
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
            }
        }
    }
}
