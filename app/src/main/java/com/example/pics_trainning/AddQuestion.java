package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class AddQuestion extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    ImageView selectImage;
    Context context;
    int userId;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);

        selectImage = findViewById(R.id.question_select_image);
        selectImage.setOnClickListener(selectPicture);
        Button addQuestion = findViewById(R.id.question_add);
        addQuestion.setOnClickListener(addQuestionEvent);
        this.context = getApplicationContext();

        name = findViewById(R.id.add_question_name);
    }

    public View.OnClickListener selectPicture = new View.OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Uri image = data.getData();
            try {
                selectImage.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(image)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void returnToNewGame() {
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    public View.OnClickListener addQuestionEvent = new View.OnClickListener() {
        public void onClick(View v){
            new AddQuestionTask().execute((Object[]) null);
        }
    };

    private class AddQuestionTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            Bitmap bitmap = ((BitmapDrawable) selectImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();

            database.addQuestion(userId, imageInByte, name.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Cursor result){
            returnToNewGame();
            database.close();
        }
    }

}