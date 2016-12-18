package com.madmensoftware.www.pops.Models;

/**
 * Created by carson on 12/17/2016.
 */

public class EmergencyContact {
    String popperUid;
    String lastName;
    String firstName;
    String email;

    public EmergencyContact() {

    }

    public String getPopperUid() {
        return popperUid;
    }

    public void setPopperUid(String popperUid) {
        this.popperUid = popperUid;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
