package com.digitalcreativeasia.openprojectlogtime.pojos.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("raw")
    @Expose
    private String raw;
    @SerializedName("html")
    @Expose
    private String html;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}