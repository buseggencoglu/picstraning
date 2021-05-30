package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class WrongAnswer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answer);

        TimerTask task = new TimerTask(){
            public void run(){
                finish();
            }
        };

        Timer timer = new Timer("Timer");
        long delay = 3000L;
        timer.schedule(task,delay);
    }
}
