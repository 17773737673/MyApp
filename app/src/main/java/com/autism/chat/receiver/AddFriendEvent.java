package com.autism.chat.receiver;

import android.content.Intent;

/**
 * Created by AutismPerson on 4/7 0007.
 */
public class AddFriendEvent {

    public Intent intent ;
    public AddFriendEvent(Intent intent) {
        this.intent = intent;
    }
}
