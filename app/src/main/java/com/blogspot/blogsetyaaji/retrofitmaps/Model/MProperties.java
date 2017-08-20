
package com.blogspot.blogsetyaaji.retrofitmaps.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MProperties {

    @SerializedName("properti")
    @Expose
    private List<Propertus> properti = null;
    @SerializedName("pesan")
    @Expose
    private String pesan;
    @SerializedName("sukses")
    @Expose
    private Boolean sukses;

    public List<Propertus> getProperti() {
        return properti;
    }

    public void setProperti(List<Propertus> properti) {
        this.properti = properti;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public Boolean getSukses() {
        return sukses;
    }

    public void setSukses(Boolean sukses) {
        this.sukses = sukses;
    }

}
