package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class login extends AppCompatActivity {
    private Context context;

    EditText username;
    EditText passw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = (Button) findViewById(R.id.login);
        TextView redirectRegister = (TextView) findViewById(R.id.redirectRegister);
        login.setOnClickListener(buttonLogin);
        redirectRegister.setOnClickListener(registerRedirectButton);
        this.username = findViewById(R.id.username);
        this.passw = findViewById(R.id.password);
        this.context = this.getApplicationContext();
    }

    public View.OnClickListener buttonLogin = new View.OnClickListener() {
        public void onClick(View v){
            boolean error = false;
            if (username.getText().toString().isEmpty()) {
                error = true;
                username.setError("This field can not be blank");
            }
            if (passw.getText().toString().isEmpty()) {
                error = true;
                passw.setError("This field can not be blank");
            }
            if (error) {
                return;
            }
            new LoginTask().execute((Object[]) null);
        }
    };

    public void redirectHome(Cursor result) {
        if(result.moveToNext()){
            int id = Integer.parseInt(result.getString(result.getColumnIndexOrThrow("_id")));
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", id);
            startActivity(intent);
        }
        else {
            username.setError("Invalid credential!");
            passw.setError("Invalid credential!");
        }
    }

    public void redirectRegister() {
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }

    public View.OnClickListener registerRedirectButton = new View.OnClickListener() {
        public void onClick(View v){
            redirectRegister();
        }
    };

    private class LoginTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            return database.getUser(username.getText().toString(), passw.getText().toString());
        }

        @Override
        protected void onPostExecute(Cursor result){
            redirectHome(result);
            database.close();
        }
    }

}