package com.safwat.abanoub.bridgeit;

class StudentFeedback {
    public String uid;
    public String note;
    public String fullname;
    public String date;

    public StudentFeedback() {
    }

    public StudentFeedback(String note,String fullname, String date) {
        this.note = note;
        this.fullname=fullname;
        this.date = date;
    }
}
