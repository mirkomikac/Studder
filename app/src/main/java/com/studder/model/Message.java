package com.studder.model;

import java.util.Date;

public class Message {

    private Long id;
    private String text;
    private Date timeRecieved;
    private String status;
    private UserMatch match;
    private User sender;

    public Message(){

    }

    public Message(String text, Date timeRecieved, String status, UserMatch match, User sender) {
        this.text = text;
        this.timeRecieved = timeRecieved;
        this.status = status;
        this.match = match;
        this.sender = sender;
    }

    public Date getTimeRecieved() {
        return timeRecieved;
    }

    public void setTimeRecieved(Date timeRecieved) {
        this.timeRecieved = timeRecieved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserMatch getMatch() {
        return match;
    }

    public void setMatch(UserMatch match) {
        this.match = match;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
