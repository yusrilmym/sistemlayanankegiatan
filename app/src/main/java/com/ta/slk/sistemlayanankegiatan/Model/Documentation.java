package com.ta.slk.sistemlayanankegiatan.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Documentation {
    @SerializedName("id_documentation")
    @Expose
    private String idDocumentation;
    @SerializedName("id_member")
    @Expose
    private String idMember;
    @SerializedName("id_activity")
    @Expose
    private String idActivity;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("picture")
    @Expose
    private String picture;

    public String getIdDocumentation() {
        return idDocumentation;
    }

    public void setIdDocumentation(String idDocumentation) {
        this.idDocumentation = idDocumentation;
    }

    public String getIdMember() {
        return idMember;
    }

    public void setIdMember(String idMember) {
        this.idMember = idMember;
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

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
}
