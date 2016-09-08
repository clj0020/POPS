package com.madmensoftware.www.pops.Models;

/**
 * Created by carsonjones on 9/8/16.
 */
public class Job {

    private String title;
    private String description;
    private String category;
    private double budget;
    private int duration;
    private String status;
    private String posterUid;
    private String posterName;
    private String popperUid;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPosterUid() {
        return posterUid;
    }

    public void setPosterUid(String posterUid) {
        this.posterUid = posterUid;
    }


    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }


    public String getPopperUid() {
        return popperUid;
    }

    public void setPopperUid(String popperUid) {
        this.popperUid = popperUid;
    }

}
