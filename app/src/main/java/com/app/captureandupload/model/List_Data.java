package com.app.captureandupload.model;


public class List_Data {

    private String selftext;
    private String title;
    private Integer ups;
    private String thumbnail;
    private Boolean edited;
    private Double created;

    public List_Data(String selftext, String title, Integer ups, String thumbnail, Boolean edited, Double created) {
        this.selftext = selftext;
        this.title = title;
        this.ups = ups;
        this.thumbnail = thumbnail;
        this.edited = edited;
        this.created = created;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUps() {
        return ups;
    }

    public void setUps(Integer ups) {
        this.ups = ups;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    public Double getCreated() {
        return created;
    }

    public void setCreated(Double created) {
        this.created = created;
    }
}