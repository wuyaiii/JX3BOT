package io.github.pigeonmuyz.jx3bot.entity;

import java.util.List;

public class TeamActivity {
    int activityId;
    /**
     * 副本名称
     */
    String activity;
    /**
     * 等级要求
     */
    int level;
    /**
     * 团长名字
     */
    String leader;
    int pushId;
    int roleId;
    Long createTime;
    /**
     * 当前队伍人数
     */
    int number;
    /**
     * 当前副本最大人数
     */
    int maxNumber;
    List<Object> label;
    /**
     * 团招内容
     */
    String content;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public int getPushId() {
        return pushId;
    }

    public void setPushId(int pushId) {
        this.pushId = pushId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public List<Object> getLabel() {
        return label;
    }

    public void setLabel(List<Object> label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
