package com.firstapp.firebasechat.Model;

public class Users {

    private String id;
    private String username;
    private String imageURL;
    private String status;


    private Boolean selected = false;

    //Constructors;
    public Users(){

    }



    public Users(String id, String username, String imageURL, String status){
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
    }




    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
