package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private int userId;
    private List<Question> questions;
    Context context;
    ImageView currentImage;
    FlexboxLayout choicesContainer;
    int currentIndex = 0;
    Question currentQuestion;
    TextView timerText;

    private int score = 0;
    private int correctAnswerNumber = 0;
    private int incorrectAnswerNumber = 0;
    private int currentTimer = 53;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = this.getIntent();

        userId = intent.getIntExtra("userId", -1);
        this.context = this.getApplicationContext();

        currentImage = findViewById(R.id.game_current_image);
        choicesContainer = findViewById(R.id.choices_container);

        this.timerText = findViewById(R.id.time_remaining);

        Button finishGameButton = findViewById(R.id.finish_game);
        finishGameButton.setOnClickListener(finishGame);

        new QuestionTask().execute((Object[]) null);
    }

    private void timeout() {
        incorrectAnswerNumber++;
        setNextPhoto();
        startTimer();
    }

    private void startTimer() {
        currentTimer = 53;
        System.out.println(task);
        if (task == null) {
            Handler mainHandler = new Handler();
            task = new TimerTask(){
                public void run() {
                    currentTimer--;
                    if (currentTimer <= 0) {
                        timeout();
                    } else {
                        timerText.setText(String.valueOf(currentTimer));
                    }
                    long delay = 1000L;
                    mainHandler.postDelayed(task, delay);
                }
            };
            mainHandler.post(task);
        }
    }

    private void setQuestions(Cursor result) {
        questions = new ArrayList();
        while (result.moveToNext()) {
            String name = result.getString(result.getColumnIndexOrThrow("names"));
            int id = result.getInt(result.getColumnIndexOrThrow("_id"));
            questions.add(new Question(id, name,
                    result.getBlob(result.getColumnIndexOrThrow("picture"))));
            Button button = new Button(this.context);
            button.setOnClickListener(selectName);
            button.setText(name);
            button.setId(id);
            this.choicesContainer.addView(button);
        }
        Collections.shuffle(questions);
        setNextPhoto();
        startTimer();
    }

    private void gotAnswer(View v){
        if (v.getId() == currentQuestion.getId()) {
            Button selectedButton = findViewById(v.getId());
            ((ViewGroup)selectedButton.getParent()).removeView(selectedButton);
            score +=10;
            correctAnswerNumber++;
            Intent intent = new Intent(this, correct_activity.class);
            startActivity(intent);
        } else {
            incorrectAnswerNumber++;
            Intent intent = new Intent(this, WrongAnswer.class);
            startActivity(intent);
        }
        setNextPhoto();
        startTimer();
    }

    public View.OnClickListener finishGame = new View.OnClickListener() {
        public void onClick(View v) {
            redirectToGameReport();
        }
    };


    public View.OnClickListener selectName = new View.OnClickListener() {
        public void onClick(View v) {
          gotAnswer(v);
        }
    };

    private void redirectToGameReport() {
        Intent intent = new Intent(this, GameReportActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("score", score);
        intent.putExtra("correct", correctAnswerNumber);
        intent.putExtra("incorrect", incorrectAnswerNumber);
        startActivity(intent);
    }

    private void setNextPhoto() {
        if (currentIndex == questions.size()) {
            redirectToGameReport();
            return;
        }
        this.currentQuestion = questions.get(currentIndex);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(this.currentQuestion.getPicture(), 0, this.currentQuestion.getPicture().length, options);
        this.currentImage.setImageBitmap(bmp);
        currentIndex++;
    }

    private class QuestionTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params) {
            database.open();
            return database.getQuestionsByUser(userId);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            setQuestions(result);
            database.close();
        }
    }

}