package io.github.pigeonmuyz.jx3bot.entity;

public class Chivalrous {
    int id;
    /**
     * 地图名
     */
    String map;
    /**
     * 任务名称
     */
    String name;
    /**
     * 任务所属地图具体位置
     */
    String site;
    /**
     * 获取结算方式
     */
    String tasks;
    /**
     * 任务描述
     */
    String desc;
    /**
     * 任务备注
     */
    String note;
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

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks) {
        this.tasks = tasks;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
