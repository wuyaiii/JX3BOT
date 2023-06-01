package io.github.pigeonmuyz.jx3bot.entity;

/**
 * 普通Http请求的工具类
 * @param <T>
 */
public class Result<T> {
    int code;
    String message;
    T data;
    Long time;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
