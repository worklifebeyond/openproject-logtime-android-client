
package com.digitalcreativeasia.openprojectlogtime.pojos.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskModel {

    @SerializedName("_type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lockVersion")
    @Expose
    private Integer lockVersion;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("description")
    @Expose
    private Description description;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
    @SerializedName("estimatedTime")
    @Expose
    private Double estimatedTime;
    @SerializedName("spentTime")
    @Expose
    private String spentTime;
    @SerializedName("percentageDone")
    @Expose
    private Integer percentageDone;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("laborCosts")
    @Expose
    private String laborCosts;
    @SerializedName("materialCosts")
    @Expose
    private String materialCosts;
    @SerializedName("overallCosts")
    @Expose
    private String overallCosts;
    @SerializedName("remainingTime")
    @Expose
    private Double remainingTime;
    @SerializedName("_links")
    @Expose
    private Links links;

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

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(String spentTime) {
        this.spentTime = spentTime;
    }

    public Integer getPercentageDone() {
        return percentageDone;
    }

    public void setPercentageDone(Integer percentageDone) {
        this.percentageDone = percentageDone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLaborCosts() {
        return laborCosts;
    }

    public void setLaborCosts(String laborCosts) {
        this.laborCosts = laborCosts;
    }

    public String getMaterialCosts() {
        return materialCosts;
    }

    public void setMaterialCosts(String materialCosts) {
        this.materialCosts = materialCosts;
    }

    public String getOverallCosts() {
        return overallCosts;
    }

    public void setOverallCosts(String overallCosts) {
        this.overallCosts = overallCosts;
    }

    public Double getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Double remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

}
