package org.chengbing.util;

public class Result <T> {
    private int code = 200;
    private String message = "Success!";
    private T t;


    public Result() {
    }

    public Result(T t) {
        this.t = t;
    }

    public Result(int code, String message, T t) {
        this.code = code;
        this.message = message;
        this.t = t;
    }

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

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
