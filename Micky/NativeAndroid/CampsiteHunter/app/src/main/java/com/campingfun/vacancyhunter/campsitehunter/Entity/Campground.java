package com.campingfun.vacancyhunter.campsitehunter.Entity;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by wayliu on 8/31/2014.
 */
public class Campground implements Serializable {
    @DatabaseField(generatedId = true)
    private int autoId;

    @DatabaseField(index = true)
    private String name;

    private transient LatLng location;

    @DatabaseField(index = true)
    private String contractId;

    @DatabaseField(index = true)
    private String contractType;

    @DatabaseField
    private String photoUrl;
    @DatabaseField(index = true)
    private String id;

    @DatabaseField
    private String state;

    @DatabaseField
    private String description;
    @DatabaseField
    private String drivingDirection;
    @DatabaseField
    private String fullReserveUrl;
    @DatabaseField
    private String importantInfo;
    @DatabaseField
    private String address;
    @DatabaseField
    private String contact_DirectLine;
    @DatabaseField
    private String contact_RangerStation;
    @DatabaseField
    private String attention;
    @DatabaseField
    private String imgUrls_Photo;
    private String[] imgUrls_PhotoArray;
    @DatabaseField
    private String imgUrls_Map;

    private boolean isAvailable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDrivingDirection() {
        return drivingDirection;
    }

    public void setDrivingDirection(String drivingDirection) {
        this.drivingDirection = drivingDirection;
    }

    public String getFullReserveUrl() {
        return fullReserveUrl;
    }

    public void setFullReserveUrl(String fullReserveUrl) {
        this.fullReserveUrl = fullReserveUrl;
    }

    public String getImportantInfo() {
        return importantInfo;
    }

    public void setImportantInfo(String importantInfo) {
        this.importantInfo = importantInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact_DirectLine() {
        return contact_DirectLine;
    }

    public void setContact_DirectLine(String contact_DirectLine) {
        this.contact_DirectLine = contact_DirectLine;
    }

    public String getContact_RangerStation() {
        return contact_RangerStation;
    }

    public void setContact_RangerStation(String contact_RangerStation) {
        this.contact_RangerStation = contact_RangerStation;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getImgUrls_Photo() {
        return imgUrls_Photo;
    }

    public void setImgUrls_Photo(String imgUrls_Photo) {
        this.imgUrls_Photo = imgUrls_Photo;
    }

    public String getImgUrls_Map() {
        return imgUrls_Map;
    }

    public void setImgUrls_Map(String imgUrls_Map) {
        this.imgUrls_Map = imgUrls_Map;
    }

    public String[] getImgUrls_PhotoArray() {
        return imgUrls_PhotoArray;
    }

    public void setImgUrls_PhotoArray(String[] imgUrls_PhotoArray) {
        this.imgUrls_PhotoArray = imgUrls_PhotoArray;
    }
}
