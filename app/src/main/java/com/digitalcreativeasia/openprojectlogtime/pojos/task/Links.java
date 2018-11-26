
package com.digitalcreativeasia.openprojectlogtime.pojos.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("update")
    @Expose
    private Update update;
    @SerializedName("schema")
    @Expose
    private Schema schema;
    @SerializedName("updateImmediately")
    @Expose
    private UpdateImmediately updateImmediately;
    @SerializedName("logTime")
    @Expose
    private LogTime logTime;
    @SerializedName("activities")
    @Expose
    private Activities activities;
    @SerializedName("attachments")
    @Expose
    private Attachments attachments;
    @SerializedName("addAttachment")
    @Expose
    private AddAttachment addAttachment;
    @SerializedName("revisions")
    @Expose
    private Revisions revisions;
    @SerializedName("addComment")
    @Expose
    private AddComment addComment;
    @SerializedName("timeEntries")
    @Expose
    private TimeEntries timeEntries;
    @SerializedName("project")
    @Expose
    private Project project;
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("author")
    @Expose
    private Author author;
    @SerializedName("responsible")
    @Expose
    private Responsible responsible;
    @SerializedName("assignee")
    @Expose
    private Assignee assignee;
    @SerializedName("parent")
    @Expose
    private Parent parent;

    public Self getSelf() {
        return self;
    }

    public void setSelf(Self self) {
        this.self = self;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public UpdateImmediately getUpdateImmediately() {
        return updateImmediately;
    }

    public void setUpdateImmediately(UpdateImmediately updateImmediately) {
        this.updateImmediately = updateImmediately;
    }

    public LogTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LogTime logTime) {
        this.logTime = logTime;
    }

    public Activities getActivities() {
        return activities;
    }

    public void setActivities(Activities activities) {
        this.activities = activities;
    }

    public Attachments getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments attachments) {
        this.attachments = attachments;
    }

    public AddAttachment getAddAttachment() {
        return addAttachment;
    }

    public void setAddAttachment(AddAttachment addAttachment) {
        this.addAttachment = addAttachment;
    }

    public Revisions getRevisions() {
        return revisions;
    }

    public void setRevisions(Revisions revisions) {
        this.revisions = revisions;
    }

    public AddComment getAddComment() {
        return addComment;
    }

    public void setAddComment(AddComment addComment) {
        this.addComment = addComment;
    }

    public TimeEntries getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(TimeEntries timeEntries) {
        this.timeEntries = timeEntries;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Responsible getResponsible() {
        return responsible;
    }

    public void setResponsible(Responsible responsible) {
        this.responsible = responsible;
    }

    public Assignee getAssignee() {
        return assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

}
