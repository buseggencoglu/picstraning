package com.example.pics_trainning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GameDatabase {
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseHelper;

    public GameDatabase(Context context){
        databaseHelper = new DatabaseOpenHelper(context, "Game", null, 1);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        if (database != null)
            database.close();
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void addUser(String username, String email, String passw){
        ContentValues newUser = new ContentValues();
        newUser.put("username", username);
        newUser.put("email", email);
        newUser.put("passw",md5(passw));
        open();
        database.insert("user", null, newUser);
        close();
    }

    public Cursor getUser(String username, String passw){
        return database.query("user", null, "username='" + username + "' and passw='" + md5(passw) + "'", null, null, null, null);
    }

    public void addGame(int score, int correct_answer, int false_answer, int user){
        ContentValues newGame = new ContentValues();
        newGame.put("score", score);
        newGame.put("correct_answer", correct_answer);
        newGame.put("false_answer", false_answer);
        newGame.put("user",user);
        open();
        database.insert("game", null, newGame);
        close();
    }

    public Cursor getGame(long id){
        return database.query("game",null, "_id="+id, null, null, null, null);
    }

    public void addQuestion(byte[] picture, String names, int game){
        ContentValues newQuestion = new ContentValues();
        newQuestion.put("picture", picture);
        newQuestion.put("names", names);
        newQuestion.put("game",game);
        open();
        database.insert("game", null, newQuestion);
        close();
    }

    public Cursor getQuestion(long id){
        return database.query("question",null, "_id="+id, null, null, null, null);
    }


    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db){
            String sqlUser = "CREATE TABLE user" +
                    "(_id integer primary key autoincrement," +
                    "username TEXT, email TEXT, passw TEXT, high_score integer DEFAULT 0, goal integer DEFAULT 0)";
            db.execSQL(sqlUser);
            String sqlGame = "CREATE TABLE game" +
                    "(_id integer primary key autoincrement, user integer," +
                    "score integer, correct_answer integer, false_answer integer, foreign key(user) references user(_id));";
            db.execSQL(sqlGame);
            String sqlQuestion = "CREATE TABLE question" +
                    "(_id integer primary key autoincrement, game integer," +
                    "picture blob, names TEXT, foreign key(game) references game(_id));";
            db.execSQL(sqlQuestion);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        }
    }
}

