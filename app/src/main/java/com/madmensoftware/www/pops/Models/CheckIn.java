package com.madmensoftware.www.pops.Models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by chandler on 12/18/16.
 */
@Parcel
public class CheckIn {

    String checkInRequest;
    String checkInResponse;
    String checkInStatus;
    String lastCheckIn;
    ArrayList<String> checkInHistory = new ArrayList<>();


    public CheckIn(){
        checkInRequest = "No Request";
        checkInResponse = "No Response";
        checkInStatus = "Request Time";
        lastCheckIn = "No Last check in.";
    }

    public void setLastCheckIn(String last){
        lastCheckIn = last;
        checkInHistory.add(lastCheckIn);
    }
    public String getLastCheckIn(){return lastCheckIn;}

    public void setCheckInStatus(String status){checkInStatus = status;}
    public String getCheckInStatus() {return checkInStatus;}

    public void setCheckInResponse(String chr){
        checkInResponse = chr;
    }
    public String getCheckInResponse()
    {
        return checkInResponse;
    }

    public void setCheckInRequest(String chr){
        checkInRequest = chr;
    }
    public String getCheckInRequest()
    {
        return checkInRequest;
    }

}
