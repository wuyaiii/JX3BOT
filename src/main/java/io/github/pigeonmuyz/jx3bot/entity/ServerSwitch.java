package io.github.pigeonmuyz.jx3bot.entity;

public class ServerSwitch {
    int id;
    String zone;
    String server;
    int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getStatus() {
        switch (this.status){
            case 1:
                return "开服了";
            case 0:
                return "维护中";
            default:
                return "未知";
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
