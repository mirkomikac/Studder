package com.studder.model;

import java.util.Date;

public class UserMatch {

    private Long id;
    private Date matchTime;
    private User participant1;
    private User participant2;
    private String lastMessage;
    private Date lastMessageDate;
    private Boolean mutedParticipant1;
    private Boolean getMutedParticipant2;
    private Boolean lastMessageSeen;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }

    public User getParticipant1() {
        return participant1;
    }

    public void setParticipant1(User participant1) {
        this.participant1 = participant1;
    }

    public User getParticipant2() {
        return participant2;
    }

    public void setParticipant2(User participant2) {
        this.participant2 = participant2;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public Boolean getMutedParticipant1() {
        return mutedParticipant1;
    }

    public void setMutedParticipant1(Boolean mutedParticipant1) {
        this.mutedParticipant1 = mutedParticipant1;
    }

    public Boolean getGetMutedParticipant2() {
        return getMutedParticipant2;
    }

    public void setGetMutedParticipant2(Boolean getMutedParticipant2) {
        this.getMutedParticipant2 = getMutedParticipant2;
    }

    public Boolean getLastMessageSeen() {
        return lastMessageSeen;
    }

    public void setLastMessageSeen(Boolean lastMessageSeen) {
        this.lastMessageSeen = lastMessageSeen;
    }

}
