package io.github.pigeonmuyz.jx3bot.entity;

public class Chivalrous {
    int id;
    /**
     * 地图名
     */
    String map_name;
    /**
     * 任务所属地图具体位置
     */
    String event;
    /**
     * 获取结算方式
     */
    String site;
    /**
     * 任务描述
     */
    String desc;
    String icon;
    /**
     * 开始时间
     */
    String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMap_name() {
        return map_name;
    }

    public void setMap_name(String map_name) {
        this.map_name = map_name;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
