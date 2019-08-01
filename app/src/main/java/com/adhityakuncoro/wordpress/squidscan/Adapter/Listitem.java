package com.adhityakuncoro.wordpress.squidscan.Adapter;

public class Listitem {
    private long _id;
    private String code;
    private String type;

    public Listitem(long _id, String code, String type) {
        this._id = _id;
        this.code = code;
        this.type = type;
    }

    public long get_Id() {
        return _id;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
