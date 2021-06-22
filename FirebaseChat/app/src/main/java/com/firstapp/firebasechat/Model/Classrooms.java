package com.firstapp.firebasechat.Model;

import java.io.Serializable;
import java.util.List;

public class Classrooms {

    private String id;
    private String classImageURL;


    public Classrooms(){

    }

    public Classrooms(String user, String classImageURL)
    {
        this.id = id;
        this.classImageURL = classImageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassImageURL() {
        return classImageURL;
    }

    public void setClassImageURL(String classImageURL) {
        this.classImageURL = classImageURL;
    }
}
