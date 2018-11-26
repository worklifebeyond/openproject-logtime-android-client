
package com.digitalcreativeasia.openprojectlogtime.pojos.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddComment {

    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("title")
    @Expose
    private String title;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
