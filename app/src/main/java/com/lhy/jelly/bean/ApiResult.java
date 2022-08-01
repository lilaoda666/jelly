package com.lhy.jelly.bean;

/**
 * Created by Lihy on 2018/6/28 14:53
 * E-Mail ：liheyu999@163.com
 */
public class ApiResult<T> {

    private int status;
    private String message;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
}
