package com.najeebi.reminders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import java.io.IOException;

public class Settings extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune = prefs.getString("tune",null);
        if(tune == null)
        {
            prefs.edit().putString("tune","https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%201.mp3?alt=media&token=13d95b0f-ea92-4e88-adac-651c3068df7e").apply();
            RadioButton rb = (RadioButton) findViewById(R.id.btn1);
            rb.setChecked(true);
        }
        else
        {
            if(tune.equals("https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%201.mp3?alt=media&token=13d95b0f-ea92-4e88-adac-651c3068df7e"))
            {
                RadioButton rb = (RadioButton) findViewById(R.id.btn1);
                rb.setChecked(true);
            }
            else if(tune.equals("https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%202.mp3?alt=media&token=00f354dd-02d4-4314-bf38-137687ae95cf"))
            {
                RadioButton rb = (RadioButton) findViewById(R.id.btn2);
                rb.setChecked(true);
            }
            else if(tune.equals("https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%203.mp3?alt=media&token=63d6e13f-66c9-4afa-8ff9-3a3b1ca164f6"))
            {
                RadioButton rb = (RadioButton) findViewById(R.id.btn3);
                rb.setChecked(true);
            }
            else if(tune.equals("https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%204.mp3?alt=media&token=5b3bbd52-c3a0-4c59-8ae2-331cf9774e4e"))
            {
                RadioButton rb = (RadioButton) findViewById(R.id.btn4);
                rb.setChecked(true);
            }
            else if(tune.equals("https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%205.mp3?alt=media&token=1921a30f-4cf3-4ac6-9661-938bf73a0bc6"))
            {
                RadioButton rb = (RadioButton) findViewById(R.id.btn5);
                rb.setChecked(true);
            }
            else if(tune.equals("https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%206.mp3?alt=media&token=33310d14-7ead-4370-aaa1-9d00e3c4e66b"))
            {
                RadioButton rb = (RadioButton) findViewById(R.id.btn6);
                rb.setChecked(true);
            }
            else
            {
                RadioButton rb = (RadioButton) findViewById(R.id.btn7);
                rb.setChecked(true);
            }
        }
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




    public void onClickbtn1(View view) {
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune = "https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%201.mp3?alt=media&token=13d95b0f-ea92-4e88-adac-651c3068df7e";
        prefs.edit().putString("tune",tune).apply();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                    mediaPlayer.stop();
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
                    mediaPlayer.start();
            }
        }).start();
    }

    public void onClickbtn2(View view) {
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune = "https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%202.mp3?alt=media&token=00f354dd-02d4-4314-bf38-137687ae95cf";
        prefs.edit().putString("tune",tune).apply();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
                mediaPlayer.start();
            }
        }).start();
    }

    public void onClickbtn3(View view) {
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune = "https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%203.mp3?alt=media&token=63d6e13f-66c9-4afa-8ff9-3a3b1ca164f6";
        prefs.edit().putString("tune",tune).apply();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
                mediaPlayer.start();
            }
        }).start();
    }

    public void onClickbtn4(View view) {
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune = "https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%204.mp3?alt=media&token=5b3bbd52-c3a0-4c59-8ae2-331cf9774e4e";
        prefs.edit().putString("tune",tune).apply();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
                mediaPlayer.start();
            }
        }).start();
    }

    public void onClickbtn5(View view) {
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune ="https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%205.mp3?alt=media&token=1921a30f-4cf3-4ac6-9661-938bf73a0bc6";
        prefs.edit().putString("tune",tune).apply();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
                mediaPlayer.start();
            }
        }).start();
    }

    public void onClickbtn6(View view) {
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune ="https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%206.mp3?alt=media&token=33310d14-7ead-4370-aaa1-9d00e3c4e66b";
        prefs.edit().putString("tune",tune).apply();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
                mediaPlayer.start();
            }
        }).start();
    }

    public void onClickbtn7(View view) {
        SharedPreferences prefs = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String tune ="https://firebasestorage.googleapis.com/v0/b/reminders-9580a.appspot.com/o/tune%207.mp3?alt=media&token=7b822b29-3f18-439c-b3b1-b947834fb416";
        prefs.edit().putString("tune",tune).apply();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer != null)
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tune));
                mediaPlayer.start();
            }
        }).start();
    }

    public void goToList(View view) {
        mediaPlayer.stop();
        Intent intent = new Intent(this,RemindersList.class);
        startActivity(intent);
    }
}