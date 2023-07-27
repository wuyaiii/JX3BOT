package io.github.pigeonmuyz.jx3bot.entity.request;

import io.github.pigeonmuyz.jx3bot.entity.Chivalrous;

import java.util.List;
import java.util.Map;

public class ChivalrousRequest {
    int code;
    String msg;
    List<Chivalrous> data;
    Long time;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Chivalrous> getData() {
        return data;
    }

    public void setData(List<Chivalrous> data) {
        this.data = data;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
