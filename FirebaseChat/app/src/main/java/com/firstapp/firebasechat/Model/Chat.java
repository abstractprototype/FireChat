package com.firstapp.firebasechat.Model;

import android.net.Uri;

public class Chat {
    private String sender;
    private String receiver;
    private String message, messageType, DateandTime;
    private Uri image;
    private boolean isseen;

     /*messageMap.put("sender", sender);
        messageMap.put("receiver", receiver);
        messageMap.put("message", message);
        messageMap.put("isseen",false);
        messageMap.put("messageType", "message");
        messageMap.put("Date and Time", formattedDate);*/
    public Chat(String sender, String receiver, String message, boolean isseen, String messageType, String DateandTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.messageType = messageType;
        this.DateandTime = DateandTime;
    }




    public Chat() {

    }

    public void setMessageType(String e){
        this.messageType = e;
    }

    public String getMessageType(){
        return this.messageType;
    }

    public void setDateandTime(String e){
        this.DateandTime = e;
    }

    public String getDateandTime(){
        return this.DateandTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }


}
