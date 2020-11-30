package com.hb712.gleak_android.message.net;

import com.hb712.gleak_android.base.BaseMessage;

/**
 * 新增漏点
 */
public class NewLeak extends BaseMessage {

    private String type;
    private String name;
    private Location location;
    private String cycle;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

}
