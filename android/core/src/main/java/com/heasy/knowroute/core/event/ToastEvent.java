package com.heasy.knowroute.core.event;

/**
 * Created by Administrator on 2017/12/28.
 */
public class ToastEvent extends AbstractEvent {
    private String message;

    public ToastEvent(Object source){
        super(source);
    }

    public ToastEvent(Object source, String message){
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
