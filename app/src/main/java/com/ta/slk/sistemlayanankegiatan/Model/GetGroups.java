
package com.ta.slk.sistemlayanankegiatan.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetGroups {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private List<Groups> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Groups> getResult() {
        return result;
    }

    public void setResult(List<Groups> result) {
        this.result = result;
    }

}