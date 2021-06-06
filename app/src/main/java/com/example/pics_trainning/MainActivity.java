package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int userId;
    Context context;
    TextView username;
    TextView mail;
    TextView highScore;
    TextView goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        context=this.getApplicationContext();
        this.username=findViewById(R.id.home_username);
        this.mail=findViewById(R.id.home_mail);
        this.highScore=findViewById(R.id.home_score);
        this.goal=findViewById(R.id.home_goal);
        ImageView edit = findViewById(R.id.home_edit);
        edit.setOnClickListener(editButtonAction);
        new UserRetreiveTask().execute((Object[]) null);

        Button newGame = findViewById(R.id.home_new_game);
        newGame.setOnClickListener(newGameButtonAction);
    }

    public View.OnClickListener editButtonAction = new View.OnClickListener() {
        public void onClick(View v){
            boolean isEnabled = goal.isEnabled();
            if (isEnabled) {
                goal.setEnabled(false);
                if (!goal.getText().toString().isEmpty()) {
                    new UserUpdateTask().execute((Object[]) null);
                }
            } else {
                goal.setEnabled(true);
            }
        }
    };

    public View.OnClickListener newGameButtonAction = new View.OnClickListener() {
        public void onClick(View v){
            redirectNewGame();
        }
    };

    private void fillUserInfo(Cursor result){
        if (result.moveToFirst()) {
            this.username.setText(result.getString(result.getColumnIndexOrThrow("username")));
            this.mail.setText(result.getString(result.getColumnIndexOrThrow("email")));
            this.highScore.setText(result.getString(result.getColumnIndexOrThrow("high_score")));
            this.goal.setText(result.getString(result.getColumnIndexOrThrow("goal")));
         }
    }

    public void redirectNewGame() {
        Intent intent = new Intent(this, NewGame.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }


    private class UserRetreiveTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            return database.getUserById(userId);
        }

        @Override
        protected void onPostExecute(Cursor result){
            fillUserInfo(result);
            database.close();
        }
    }

    private class UserUpdateTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            database.updateUser(userId, Integer.parseInt(goal.getText().toString()));
            return null;
        }

        @Override
        protected void onPostExecute(Cursor result){
            database.close();
        }
    }

}