package com.malak.contactaura;

import com.google.gson.annotations.SerializedName;

public class PhonePerson {

    @SerializedName("id")
    private int remoteId;

    @SerializedName("name")
    private String displayName;

    @SerializedName("phone")
    private String phoneNumber;

    @SerializedName("source")
    private String origin;

    @SerializedName("created_at")
    private String createdAt;

    public PhonePerson() {
    }

    public PhonePerson(String displayName, String phoneNumber) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.origin = "mobile";
    }

    public int getRemoteId() {
        return remoteId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}