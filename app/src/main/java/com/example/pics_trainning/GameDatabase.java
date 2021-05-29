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

    public Cursor getUserById(int id){
        return database.query("user", null, "_id=" + id, null, null, null, null);
    }

    public void updateUser(int id, int goal){
        ContentValues userUpdate = new ContentValues();
        userUpdate.put("goal", goal);
        open();
        database.update("user", userUpdate, "_id=" + id, null);
        close();
    }

    public void addQuestion(int userId, byte[] picture, String names){
        ContentValues newQuestion = new ContentValues();
        newQuestion.put("userId", userId);
        newQuestion.put("picture", picture);
        newQuestion.put("names", names);
        open();
        database.insert("game", null, newQuestion);
        close();
    }

    public Cursor getQuestionsByUser(int id){
        return database.query("question",null, "user="+id,
                null, null, null, null);
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
            String sqlQuestion = "CREATE TABLE question" +
                    "(_id integer primary key autoincrement, game integer," +
                    "picture blob, names TEXT, foreign key(user) references user(_id));";
            db.execSQL(sqlQuestion);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        }
    }
}

