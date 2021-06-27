package com.firstapp.firebasechat.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Classrooms {

    private String id;
    private String classRoomName;
    private String classImageURL;
    private ArrayList<String> listofUsers;
    private String roomName;

    public Classrooms(ArrayList<String> users){
        listofUsers = users;
    }

    public Classrooms(String id, String classRoomName, String classImageURL)
    {
        this.id = id;
        this.classRoomName = classRoomName;
        this.classImageURL = classImageURL;
    }

    public Classrooms(String roomName, String roomID){
        this.roomName = roomName;
        this.id = roomID;
    }

    public String getRoomName(){return roomName;}

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

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }*/
}
