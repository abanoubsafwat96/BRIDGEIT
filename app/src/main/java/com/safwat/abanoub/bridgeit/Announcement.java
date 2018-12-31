package com.safwat.abanoub.bridgeit;

public class Announcement {
    public String uid;
    public String title;
    public String date;
    public String announcement;
    public String senderNode;
    public String postedBy;

    public Announcement() {
    }

    public Announcement(String title, String date, String announcement,String senderNode, String postedBy) {
        this.title = title;
        this.date = date;
        this.announcement = announcement;
        this.senderNode = senderNode;
        this.postedBy = postedBy;
    }
}
