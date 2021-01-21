package com.heasy.knowroute.event;

import com.heasy.knowroute.core.event.AbstractEvent;

public class TokenEvent extends AbstractEvent {
    private String message;

    public TokenEvent(Object source, String message){
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
