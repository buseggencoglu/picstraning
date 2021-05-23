package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);
        TextView label = (TextView) findViewById(R.id.userId);
        label.setText("--> --> " + userId);
    }


}