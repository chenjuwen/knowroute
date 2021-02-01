package com.heasy.knowroute.event;

import com.heasy.knowroute.core.event.AbstractEvent;

public class UpdateVersionEvent extends AbstractEvent {
    private String versionName;
    private String downloadURL;

    public UpdateVersionEvent(Object source, String versionName, String downloadURL){
        super(source);
        this.versionName = versionName;
        this.downloadURL = downloadURL;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
}
