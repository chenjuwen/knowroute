package com.heasy.knowroute.service;

public interface DataCacheService {
    public static final int millisecondForSecond = 1000; //一秒的毫秒数
    public static final int millisecondForMinute = 1000 * 60; //一分钟的毫秒数
    public static final int millisecondForHour = 1000 * 60 * 60; //一小时的毫秒数
    public static final int millisecondForDay = 1000 * 60 * 60 * 24; //一天的毫秒数

    public static final int secondForMinute = 60; //一分钟的秒数
    public static final int secondForHour = 60 * 60; //一小时的秒数
    public static final int secondForDay = 60 * 60 * 24; //一天的秒数

    boolean set(String key, Object value);

    boolean set(String key, Object value, int expireSeconds);

    boolean add(String key, Object value);

    boolean add(String key, Object value, int expireSeconds);

    boolean delete(String key);

    boolean delete(String... keys);

    Object get(String key);

    boolean exists(String key);

    long incr(String key);

    long decr(String key);

    boolean storeCounter(String key, long counter);

    boolean storeCounter(String key, long counter, int expireSeconds);

    boolean storeCounter(String key, long counter, long expireTimeMillis);

    Long getCounter(String key);

    void clear();
}
