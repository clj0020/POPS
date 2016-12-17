package com.madmensoftware.www.pops.Models;

/**
 * Created by chandler on 12/16/16.
 */

public class EmergencyContact {

    String popperUid;
    String email;
    String firstName;
    String LastName;
    String phone;
    String parentUid;


    public EmergencyContact()
    {

    }

    public void setParentUid(String uid){
        parentUid = uid;
    }
    public String getParentUid()
    {
        return parentUid;
    }

    public void setPopperUid(String uid){
        popperUid = uid;
    }
    public String getPopperUid(){
        return popperUid;
    }
    public void setEmail(String em){
        email = em;
    }
    public String getEmail(){
        return email;
    }
    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return LastName;
    }
    public String getPhone(){
        return phone;
    }
    public void setFirstName(String fn){
        firstName = fn;
    }
    public void setLastName(String ln){
        LastName = ln;
    }
    public void setPhone(String phoneN){
        phone = phoneN;
    }

}

