package com.duowan.mobile.main.kinds;

public interface KindStorage {
    int getInt(String key, int defaultValue);

    String getString(String key, String defaultValue);

    boolean getBoolean(String key, boolean defaultValue);

    void putInt(String key, int value);

    void putString(String key, String value);

    void putBoolean(String key, boolean value);

    boolean isDebug();
}