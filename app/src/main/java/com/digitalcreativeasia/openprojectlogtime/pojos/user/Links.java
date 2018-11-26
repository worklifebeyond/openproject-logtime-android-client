
package com.digitalcreativeasia.openprojectlogtime.pojos.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("showUser")
    @Expose
    private ShowUser showUser;
    @SerializedName("updateImmediately")
    @Expose
    private UpdateImmediately updateImmediately;
    @SerializedName("lock")
    @Expose
    private Lock lock;

    public Self getSelf() {
        return self;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public ShowUser getShowUser() {
        return showUser;
    }

    public void setShowUser(ShowUser showUser) {
        this.showUser = showUser;
    }

    public UpdateImmediately getUpdateImmediately() {
        return updateImmediately;
    }

    public void setUpdateImmediately(UpdateImmediately updateImmediately) {
        this.updateImmediately = updateImmediately;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

}
