package io.github.pigeonmuyz.jx3bot.entity;

import java.util.List;

public class Daily {
    String date;
    String week;
    String war;
    String battle;

    String huatu;

    public String getHuatu() {
        return huatu;
    }

    public String getDate() {
        return date;
    }

    public String getWeek() {
        return week;
    }

    public String getWar() {
        return war;
    }

    public String getBattle() {
        return battle;
    }

    public String getSchool() {
        return school;
    }

    public List<String> getTeam() {
        return team;
    }

    String school;
    List<String> luck;
    List<String> team;

    public String getLuck() {
        String lucky = String.join(";",luck);
        return lucky;
    }
}
