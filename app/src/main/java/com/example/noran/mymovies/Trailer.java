package com.example.noran.mymovies;

import java.io.Serializable;


public class Trailer implements Serializable {
    String key;
    String name;

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }


    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}