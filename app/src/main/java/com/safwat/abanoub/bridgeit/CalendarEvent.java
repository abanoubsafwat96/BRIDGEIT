package com.safwat.abanoub.bridgeit;

public class CalendarEvent {
    public String timeInMillis;
    public String message;
    public String uid;

    public CalendarEvent() {
    }

    public CalendarEvent(String timeInMillis, String message) {
        this.timeInMillis = timeInMillis;
        this.message = message;
    }
}
