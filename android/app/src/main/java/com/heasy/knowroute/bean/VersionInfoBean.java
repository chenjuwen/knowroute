package com.heasy.knowroute.bean;

public class VersionInfoBean {
    private String currentVersion = "";
    private String lastedVersion = "";
    private String lastedVersionURL = "";

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getLastedVersion() {
        return lastedVersion;
    }

    public void setLastedVersion(String lastedVersion) {
        this.lastedVersion = lastedVersion;
    }

    public String getLastedVersionURL() {
        return lastedVersionURL;
    }

    public void setLastedVersionURL(String lastedVersionURL) {
        this.lastedVersionURL = lastedVersionURL;
    }
}
