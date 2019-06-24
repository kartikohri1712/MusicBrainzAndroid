package org.metabrainz.mobile.data.sources.api.entities.mbentity;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.metabrainz.mobile.data.sources.api.entities.LifeSpan;

import java.util.ArrayList;

public class Label extends MBEntity {
    private String name;
    private String type;
    @SerializedName("label-code")
    private String code;
    @SerializedName("life-span")
    private LifeSpan lifeSpan;
    private String country;
    private Area area;
    private ArrayList<Release> releases = new ArrayList<>();

    public ArrayList<Release> getReleases() {
        return releases;
    }

    public void setReleases(ArrayList<Release> releases) {
        this.releases = releases;
    }

    public String getCode() {
        if (code != null) return "LC " + code;
        return "";
    }

    public void setCode(String code) {
        this.code = code;
    }

    @NonNull
    @Override
    public String toString() {
        return "Label{" +
                "mbid='" + mbid + '\'' +
                ", name='" + name + '\'' +
                ", disambiguation='" + disambiguation + '\'' +
                ", lifeSpan=" + lifeSpan +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", area=" + area +
                '}';
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LifeSpan getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(LifeSpan lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
