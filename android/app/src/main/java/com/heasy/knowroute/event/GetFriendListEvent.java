package com.heasy.knowroute.event;

import com.heasy.knowroute.core.event.AbstractEvent;

public class GetFriendListEvent extends AbstractEvent {
    private String message;

    public GetFriendListEvent(Object source){
        super(source);
    }

    public GetFriendListEvent(Object source, String message){
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
