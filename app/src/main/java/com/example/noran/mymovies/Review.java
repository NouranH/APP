package com.example.noran.mymovies;

import java.io.Serializable;

public class Review implements Serializable {
    String author;
    String content;
    private String id;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }



    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }

    public void setContent(String content) {
        this.content= content;
    }
    public String getContent() {
        return content;
    }

}