package com.rabihsalamey.countdown;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    int countdown = 0;
    String message;
    Button setButton, startButton;
    EditText enteredText, countdownText;
    Spinner mSpinner;
    List<String> spinnerArray;


    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "Countdown_channel";
    private static final String TAG = "Countdown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButton = (Button) findViewById(R.id.set_countdown_button);
        startButton = (Button) findViewById(R.id.start_countdown);
        countdownText = (EditText) findViewById(R.id.countdown_box);
        enteredText = (EditText) findViewById(R.id.message_box);
        mSpinner = (Spinner) findViewById(R.id.our_spinner);

        mSpinner.setEnabled(false);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown = Integer.parseInt(countdownText.getText().toString());

                if(countdown % 5 == 0 && countdown > 5 && countdown <= 120) {
                    mSpinner.setEnabled(true);
                    loadSpinnerValues(countdown);
                } else {
                    mSpinner.setEnabled(false);
                    Toast.makeText(getApplicationContext(),
                    "Please enter a number divisible by 5, greater than 5 and less than 120.",
                        Toast.LENGTH_LONG).show();
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "start button was clicked");
                message = enteredText.getEditableText().toString();
                scheduleNotifications();
            }
        });

    }

    private void loadSpinnerValues(int countdown) {
        spinnerArray =  new ArrayList<String>();

        int[] values = {1, 5, 10, 20, 30, 60, 90};

        for(int i = 0; i < values.length; i++) {
            if(countdown >= values[i]) {
                spinnerArray.add(Integer.toString(values[i]));
            }
        }

        //spinnerArray.add(Integer.toString(countdown));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    public void scheduleNotifications() {

        final Handler handler = new Handler();
        int runningTime = 0;

        Toast.makeText(getApplicationContext(), "Countdown has been started.",
                Toast.LENGTH_LONG).show();


        for(int i = mSpinner.getSelectedItemPosition(); i >= 0; i--) {
            final int time = Integer.parseInt(spinnerArray.get(i));
            final int index = i + 1;    //we add 1 to index to allow for 0 to be a unique #.
            final Runnable r = new Runnable() {
                public void run() {
                    setupNotification(getMessage(time), index);
                    Log.w(TAG, "printing at time = " + time);
                }
            };
            handler.postDelayed(r, (runningTime) * 1000);
            runningTime += time;
        }

        final Runnable r = new Runnable() {
            public void run() {
                setupNotification(getMessage(0), 0);
                Log.w(TAG, "printing at time = " + 0);
            }
        };
        handler.postDelayed(r, (runningTime + 1) * 1000);

        Log.w(TAG, mSpinner.getSelectedItemPosition() + ":" + spinnerArray.toString());
    }

    public String getMessage(int countdown) {
        if(countdown == 120 || Integer.parseInt((String) mSpinner.getSelectedItem()) == countdown) {
            return "Countdown has been started";
        } else if(countdown == 0){
            return "Time for: " + message;
        } else
            return (countdown) + " seconds until " + message;
    }

    public void setupNotification(String message, int index) {

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        Intent notificationIntent = new Intent(this, MainActivity.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Notification.Builder mBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_insert_emoticon_black_24dp)
                .setContentTitle("Countdown")
                .setContentText(message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(index, mBuilder.build());

    }
}
