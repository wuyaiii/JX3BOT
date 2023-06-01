package io.github.pigeonmuyz.jx3bot.entity;

import java.util.List;

public class ServerTeam {
    String zone;
    String server;
    Long time;
    List<TeamActivity> data;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<TeamActivity> getData() {
        return data;
    }

    public void setData(List<TeamActivity> data) {
        this.data = data;
    }
}
