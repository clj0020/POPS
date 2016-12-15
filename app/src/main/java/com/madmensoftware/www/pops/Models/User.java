package com.madmensoftware.www.pops.Models;

import android.util.Log;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by carsonjones on 8/29/16.
 */
@Parcel
public class User {

    String uid;
    String firstName;
    String lastName;
    String name;
    long birthDay;
    String address;
    int age;
    int zipCode;
    String type;
    String transportationType;
    long goalDate;
    double goal;
    String email;
    int radius;
    double earned;
    int organizationCode;
    int phone;
    int accessCode;
    String parentUid;
    String childUid;
    String organizationName;
    List<String> jobs;
    Notification notification;
    String safeWord;
    String stripeCustomerId;
    String stripeAccountId;
    String stripeApiSecretKey;
    String stripeApiPublishableKey;
    boolean paymentAdded;


    public User() {
        //goalDate = new Date();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(long goalDate) {
        this.goalDate = goalDate;
    }

    public int getDaysUntilGoalDate() {
        Calendar eDate = toCalendar(this.goalDate);
        Calendar sDate = toCalendar(System.currentTimeMillis());

        // Get the represented date in milliseconds
        long milis1 = sDate.getTimeInMillis();
        long milis2 = eDate.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(milis2 - milis1);

        return (int)(diff / (24 * 60 * 60 * 1000));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getEarned() {
        return earned;
    }

    public void setEarned(double earned) {
        this.earned = earned;
    }

    public void addToEarned(double profit) {
        this.earned = (this.earned + profit);
    }

    public int getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(int organizationCode) {
        this.organizationCode = organizationCode;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(int accessCode) {
        this.accessCode = accessCode;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getChildUid() {
        return childUid;
    }

    public void setChildUid(String childUid) {
        this.childUid = childUid;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getSafeWord() {
        return safeWord;
    }

    public void setSafeWord(String safeWord) {
        this.safeWord = safeWord;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }

    public String getStripeApiSecretKey() {
        return stripeApiSecretKey;
    }

    public void setStripeApiSecretKey(String stripeApiSecretKey) {
        this.stripeApiSecretKey = stripeApiSecretKey;
    }

    public String getStripeApiPublishableKey() {
        return stripeApiPublishableKey;
    }

    public void setStripeApiPublishableKey(String stripeApiPublishableKey) {
        this.stripeApiPublishableKey = stripeApiPublishableKey;
    }



    public boolean getPaymentAdded() {
        return paymentAdded;
    }

    public void setPaymentAdded(boolean paymentAdded) {
        this.paymentAdded = paymentAdded;
    }



    // Helper methods
    private Calendar toCalendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
