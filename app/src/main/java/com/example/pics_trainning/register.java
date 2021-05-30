package com.example.pics_trainning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class register extends AppCompatActivity {
    private Context context;

    EditText username;
    EditText email;
    EditText passw;
    EditText confirmPassw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(buttonRegister);
        TextView redirectLogin = (TextView) findViewById(R.id.redirectHome);
        redirectLogin.setOnClickListener(loginRedirectButton);
        this.username = (EditText) findViewById(R.id.newUsername);
        this.email = (EditText) findViewById(R.id.email);
        this.passw = (EditText) findViewById(R.id.newPassword);
        this.confirmPassw = (EditText) findViewById(R.id.confirmPassw);
        this.context = this.getApplicationContext();
    }

    public void redirectLogin() {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }

    public View.OnClickListener buttonRegister = new View.OnClickListener() {
        public void onClick(View v){
            System.out.println(passw);
            System.out.println(confirmPassw);
            boolean error = false;
            if (username.getText().toString().isEmpty()) {
                error = true;
                username.setError("This field can not be blank");
            }

            if (email.getText().toString().isEmpty()) {
                error = true;
                email.setError("This field can not be blank");
            }

            if (passw.getText().toString().isEmpty()) {
                error = true;
                passw.setError("This field can not be blank");
            }
            if (!passw.getText().toString().equals(confirmPassw.getText().toString())) {
                error = true;
                passw.setError("password has to be same!");
            }
            if (error) {
                return;
            }
            new RegisterTask().execute((Object[]) null);
        }
    };

    public View.OnClickListener loginRedirectButton = new View.OnClickListener() {
        public void onClick(View v){
            redirectLogin();
        }
    };

    private class RegisterTask extends AsyncTask<Object, Object, Cursor> {
        GameDatabase database = new GameDatabase(context);

        @Override
        protected Cursor doInBackground(Object... params){
            database.open();
            database.addUser(username.getText().toString(), email.getText().toString(), passw.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Cursor result){
            redirectLogin();
            database.close();
        }
    }

}