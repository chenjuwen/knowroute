package com.heasy.knowroute.event;

import com.heasy.knowroute.core.event.AbstractEvent;

public class FixedPointInfoEvent extends AbstractEvent {
    /**
     * save, delete
     */
    private String actionName;
    private String message;

    public FixedPointInfoEvent(Object source){
        super(source);
    }

    public FixedPointInfoEvent(Object source, String actionName, String message){
        super(source);
        this.actionName = actionName;
        this.message = message;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum ACTION_NAME{
        SAVE,
        DELETE
    }
}
