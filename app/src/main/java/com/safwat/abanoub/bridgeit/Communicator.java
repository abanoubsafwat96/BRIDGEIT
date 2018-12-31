package com.safwat.abanoub.bridgeit;

import java.util.Date;

public interface Communicator {
    interface Deadline {
        void onDeadlineChoosed(Date dateClicked);
    }

    interface Gallery {
        void likeBtnClicked();
        void addLike(int position);
        void clearLike(int position);
    }

    interface ChildrensRecyclerView {
        void onChildChoosed(int position);
    }
}