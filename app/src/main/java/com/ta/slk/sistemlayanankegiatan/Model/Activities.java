package com.ta.slk.sistemlayanankegiatan.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activities {

    @SerializedName("id_activity")
    @Expose
    private String idActivity;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("name_activities")
    @Expose
    private String nameActivities;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("file")
    @Expose
    private String file;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("id_member")
    @Expose
    private String idMember;
    @SerializedName("id_employee")
    @Expose
    private String idEmployee;
    @SerializedName("id_user")
    @Expose
    private String idUser;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("birth")
    @Expose
    private String birth;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("photo_profile")
    @Expose
    private String photoProfile;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("comment_key")
    @Expose
    private String commentKey;

    public String getIdActivity() {
        return idActivity;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getNameActivities() {
        return nameActivities;
    }

    public String getPlace() {
        return place;
    }

    public String getFile() { return file; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getIdMember() {
        return idMember;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}