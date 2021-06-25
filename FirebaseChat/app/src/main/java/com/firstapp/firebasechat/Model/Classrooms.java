package com.firstapp.firebasechat.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Classrooms {

    private String id;
    private String classRoomName;
    private String classImageURL;
    private ArrayList<String> listofUsers;

    public Classrooms(ArrayList<String> users){
        listofUsers = users;
    }

    public Classrooms(String id, String classRoomName, String classImageURL)
    {
        this.id = id;
        this.classRoomName = classRoomName;
        this.classImageURL = classImageURL;
    }

    public ArrayList<String> getListofUsers() {
        return listofUsers;
    }

    public void setListofUsers(ArrayList<String> listofUsers) {
        this.listofUsers = listofUsers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassRoomName() {
        return classRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        this.classRoomName = classRoomName;
    }

    public String getClassImageURL() {
        return classImageURL;
    }

    public void setClassImageURL(String classImageURL) {
        this.classImageURL = classImageURL;
    }
}
