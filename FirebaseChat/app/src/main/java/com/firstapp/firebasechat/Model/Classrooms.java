package com.firstapp.firebasechat.Model;

public class Classrooms {

    private String id;
    private String chatImageURL;

    public Classrooms(){

    }

    public Classrooms(String id, String chatmageURL)
    {
        this.id = id;
        this.chatImageURL = chatImageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return chatImageURL;
    }

    public void setImageURL(String imageURL) {
        this.chatImageURL = chatImageURL;
    }
}
