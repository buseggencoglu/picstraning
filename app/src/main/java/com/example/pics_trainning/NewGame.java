package com.example.pics_trainning;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NewGame extends AppCompatActivity implements QuestionsAdaptor.ItemClickListener {
    int userId;
    Context context;

    QuestionsAdaptor adaptor;
    ArrayList<Question> questions;

    String deletedName;

    private static final int ADD_QUESTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectQuestionAdd();
            }
        });
        FloatingActionButton startGame = findViewById(R.id.new_game_start);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
        this.context = this.getApplicationContext();

        new QuestionTask().execute((Object[]) null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_QUESTION) {
            new QuestionTask().execute((Object[]) null);
        }
    }


    private void redirectQuestionAdd() {
        Intent intent = new Intent(this, AddQuestion.class);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, ADD_QUESTION);
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void setQuestions(Cursor result) {
        questions = new ArrayList();
        while (result.moveToNext()) {
            questions.add(new Question(result.getInt(result.getColumnIndexOrThrow("_id")),
                    result.getString(result.getColumnIndexOrThrow("names")),
                    result.getBlob(result.getColumnIndexOrThrow("picture"))));
        }

        RecyclerView recyclerView = findViewById(R.id.questions_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);
        this.adaptor = new QuestionsAdaptor(this, questions.stream()
                                                                   .map(Question::getName)
                                                                   .collect(Collectors.toList()));
        this.adaptor.setClickListener(this);
        recyclerView.setAdapter(adaptor);
    }

    private class QuestionTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            return database.getQuestionsByUser(userId);
        }

        @Override
        protected void onPostExecute(Cursor result){
            setQuestions(result);
            database.close();
        }
    }

    private class DeleteQuestionTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            database.deleteQuestionByName(deletedName);
            return null;
        }

        @Override
        protected void onPostExecute(Cursor result){
            new QuestionTask().execute( (Object[]) null);
            database.close();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        this.deletedName = this.questions.get(position).getName();
        Toast.makeText(this, "Deleted " + deletedName, Toast.LENGTH_SHORT).show();
        new DeleteQuestionTask().execute((Object[]) null);
    }

}