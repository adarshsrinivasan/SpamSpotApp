package com.example.adarsh.smstest;

/**
 * Created by ADARSH on 15-03-2017.
 */
public class Data_SmsInbox {
    private String id;
    private String msg;
    private String contact;
    private String type;

    public String getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public String getContact() {
        return contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Data_SmsInbox(String id, String msg, String contact, String type){
        this.id = id;
        this.msg = msg;
        this.contact = contact;
        this.type = type;

    }
}
