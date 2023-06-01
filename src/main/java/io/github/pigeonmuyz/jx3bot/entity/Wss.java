package io.github.pigeonmuyz.jx3bot.entity;

/**
 * WSS的工具类
 * @param <T>
 */
public class Wss<T> {
    int code;
    T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
