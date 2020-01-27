package com.ta.slk.sistemlayanankegiatan.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDocumentation {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<Documentation> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Documentation> getResult() {
        return result;
    }

    public void setResult(List<Documentation> result) {
        this.result = result;
    }

}