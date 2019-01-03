package com.digitalcreativeasia.openprojectlogtime.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusModel {

    @SerializedName("_type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isClosed")
    @Expose
    private Boolean isClosed;
    @SerializedName("isDefault")
    @Expose
    private Boolean isDefault;
    @SerializedName("defaultDoneRatio")
    @Expose
    private Integer defaultDoneRatio;
    @SerializedName("position")
    @Expose
    private Integer position;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getDefaultDoneRatio() {
        return defaultDoneRatio;
    }

    public void setDefaultDoneRatio(Integer defaultDoneRatio) {
        this.defaultDoneRatio = defaultDoneRatio;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}