package com.digitalcreativeasia.openprojectlogtime.pojos.activity;

import com.digitalcreativeasia.openprojectlogtime.pojos.task.Self;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("workPackage")
    @Expose
    private Self workPackage;
    @SerializedName("user")
    @Expose
    private Self user;
    @SerializedName("update")
    @Expose
    private Self update;

    public Self getSelf() {
        return self;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public Self getWorkPackage() {
        return workPackage;
    }

    public void setWorkPackage(Self workPackage) {
        this.workPackage = workPackage;
    }

    public Self getUser() {
        return user;
    }

    public void setUser(Self user) {
        this.user = user;
    }

    public Self getUpdate() {
        return update;
    }

    public void setUpdate(Self update) {
        this.update = update;
    }

}