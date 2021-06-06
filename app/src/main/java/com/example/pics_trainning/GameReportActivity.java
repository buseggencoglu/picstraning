package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameReportActivity extends AppCompatActivity {

    int userId;
    int score;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_report);
        this.context = this.getApplicationContext();
        Intent intent = this.getIntent();

        userId = intent.getIntExtra("userId", -1);
        score = intent.getIntExtra("score", -1);
        int correct = intent.getIntExtra("correct", -1);
        int incorrect = intent.getIntExtra("incorrect", -1);

        TextView scoreView = findViewById(R.id.summary_score);
        scoreView.setText(String.valueOf(score));

        TextView correctView = findViewById(R.id.correct_answer);
        correctView.setText(String.valueOf(correct));

        TextView incorrectView = findViewById(R.id.incorrect_answer);
        incorrectView.setText(String.valueOf(incorrect));

        Button returnButton = findViewById(R.id.return_main);
        returnButton.setOnClickListener(returnToMain);

        new UserRetreiveTask().execute((Object[]) null);
    }

    private void redirectToGameReport() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public View.OnClickListener returnToMain = new View.OnClickListener() {
        public void onClick(View v) {
            redirectToGameReport();
        }
    };

    private class UserUpdateTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            database.updateUserScore(userId, score);
            return null;
        }

        @Override
        protected void onPostExecute(Cursor result){
            database.close();
        }
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
            if (result.moveToFirst()) {
                int highScore = result.getInt(result.getColumnIndexOrThrow("high_score"));
                if (score > highScore) {
                    new UserUpdateTask().execute((Object[]) null);
                }
            }
            database.close();
        }
    }


}