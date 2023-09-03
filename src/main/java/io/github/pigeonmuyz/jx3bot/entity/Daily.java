package io.github.pigeonmuyz.jx3bot.entity;

import java.util.List;

public class Daily {
    String date;
    String week;
    String war;
    String battle;
    String orecar;
    String rescue;
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

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setWar(String war) {
        this.war = war;
    }

    public void setBattle(String battle) {
        this.battle = battle;
    }

    public String getOrecar() {
        return orecar;
    }

    public void setOrecar(String orecar) {
        this.orecar = orecar;
    }

    public String getRescue() {
        return rescue;
    }

    public void setRescue(String rescue) {
        this.rescue = rescue;
    }

    public void setHuatu(String huatu) {
        this.huatu = huatu;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public List<String> getCard() {
        return card;
    }

    public void setCard(List<String> card) {
        this.card = card;
    }

    public void setLuck(List<String> luck) {
        this.luck = luck;
    }

    public void setTeam(List<String> team) {
        this.team = team;
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
    List<String> card;
    List<String> luck;
    List<String> team;

    public String getLuck() {
        String lucky = String.join(";",luck);
        return lucky;
    }
}
