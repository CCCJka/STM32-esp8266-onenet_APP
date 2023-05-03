package com.example.example;

public class DataBean {
    String at;
    String value;

    public DataBean(String at, String value) {
        this.at = at;
        this.value = value;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "AT:" + this.at + " | VALUE:" + this.value;
    }
}
