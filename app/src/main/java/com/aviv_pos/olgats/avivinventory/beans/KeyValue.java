package com.aviv_pos.olgats.avivinventory.beans;

/**
 * Created by olgats on 21/12/2015.
 */
public class KeyValue {
    private int key;
    private String value;

    public KeyValue() {
        super();
    }

    public KeyValue(int key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyValue{" +
                "key=" + key +
                ", value='" + value + '\'' +
                '}';
    }
}
