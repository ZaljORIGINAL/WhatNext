package com.zalj.schedule.VersionControl;

import com.zalj.schedule.BuildConfig;

import java.sql.Time;

public class Version {
    public String versionName;
    public int versionCode;
    public long lastUpdate;
    public long memory;
    public String description;

    public Version(){
        this.versionName = BuildConfig.VERSION_NAME;
        this.versionCode = BuildConfig.VERSION_CODE;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Time getLastUpdate(){
        return new Time(lastUpdate);
    }

    public long getMemory(){
        return memory;
    }
}
