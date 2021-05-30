package com.example.pics_trainning;

import java.io.Serializable;
import java.util.Arrays;

public class Question implements Serializable {
    private int id;
    private String name;
    private byte[] picture;

    public Question(int id, String name, byte[] picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", picture=" + Arrays.toString(picture) +
                '}';
    }
}
