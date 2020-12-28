package com.heasy.knowroute.event;

import com.heasy.knowroute.core.event.AbstractEvent;

public class FixedPointNavigationEvent extends AbstractEvent {
    private String message;

    public FixedPointNavigationEvent(Object source){
        super(source);
    }

    public FixedPointNavigationEvent(Object source, String message){
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
