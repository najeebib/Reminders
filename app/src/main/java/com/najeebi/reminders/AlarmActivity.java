package com.najeebi.reminders;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class AlarmActivity extends AppCompatActivity {
    private TextView Date,Title;
    private String incTitle,incDate;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        //get the title and date of the reminder from previous activity
        incTitle = getIntent().getStringExtra("TITLE");
        incDate = getIntent().getStringExtra("Date");
        Date = (TextView) findViewById(R.id.Date);
        Title = (TextView) findViewById(R.id.Title);
        Title.setText(incTitle);
        Date.setText(incDate);
        //get the tune to play
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune = prefs.getString("tune",null);
        MediaPlayer mediaplayer = new MediaPlayer();
        //if there is no tune saved yet (the user hasn't changed the settings) then set a default tune
        if(tune == null)
        {
            prefs.edit().putString("tune","https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%201.mp3?alt=media&token=13d95b0f-ea92-4e88-adac-651c3068df7e").apply();
            tune = "https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%201.mp3?alt=media&token=13d95b0f-ea92-4e88-adac-651c3068df7e";
        }
        //play the alarm tune
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
        mediaPlayer.start();


    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("mylog", ">>> onStart()");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("mylog", ">>> onResume()");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d("mylog", ">>> onRestart()");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mediaPlayer.pause();
        Log.d("mylog", ">>> onPause()");

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mediaPlayer.stop();
        Log.d("mylog", ">>> onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        Log.d("mylog", ">>> onDestroy()");
    }


    public void CloseAlarm(View view) {
        mediaPlayer.stop();
        goToList();
    }


    private void goToList() {
        Intent intent = new Intent(this,RemindersList.class);
        startActivity(intent);
    }
}