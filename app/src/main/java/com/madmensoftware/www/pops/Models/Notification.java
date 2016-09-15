package com.madmensoftware.www.pops.Models;

import org.parceler.Parcel;

/**
 * Created by carson on 9/14/2016.
 */
@Parcel

public class Notification {

    public Notification(){
        //required
    }

    String name;
    String popperUid;
    String parentUid;
    String neighborUid;
    String jobUid;
    String Uid;
    String description;

    public String getUid(){
        return Uid;
    }
    public void setUid(String uid){
        Uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPopperUid() {
        return popperUid;
    }

    public void setPopperUid(String popperUid) {
        this.popperUid = popperUid;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getNeighborUid() {
        return neighborUid;
    }

    public void setNeighborUid(String neighborUid) {
        this.neighborUid = neighborUid;
    }

    public String getJobUid() {
        return jobUid;
    }

    public void setJobUid(String jobUid) {
        this.jobUid = jobUid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }




}
