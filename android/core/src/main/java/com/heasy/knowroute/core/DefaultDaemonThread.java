package com.heasy.knowroute.core;

/**
 * Created by Administrator on 2020/11/8.
 */
public class DefaultDaemonThread extends Thread {
    public DefaultDaemonThread(){
        setDaemon(true);
    }

    @Override
    public void run() {

    }
}
