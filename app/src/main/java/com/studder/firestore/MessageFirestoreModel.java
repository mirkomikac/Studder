package com.studder.firestore;

import java.util.Date;

public class MessageFirestoreModel {

    private Long id;
    private String participant1;
    private String participant2;
    private String sender;
    private String text;
    private Date date;
    private String status;
    private Long matchId;

    public MessageFirestoreModel() { }

    public MessageFirestoreModel(Long id, String participant1, String participant2, String text, Date date, String status, Long matchId, String sender) {
        this.id = id;
        this.participant1 = participant1;
        this.participant2 = participant2;
        this.text = text;
        this.date = date;
        this.status = status;
        this.matchId = matchId;
        this.sender = sender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParticipant1() {
        return participant1;
    }

    public void setParticipant1(String participant1) {
        this.participant1 = participant1;
    }

    public String getParticipant2() {
        return participant2;
    }

    public void setParticipant2(String participant2) {
        this.participant2 = participant2;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
