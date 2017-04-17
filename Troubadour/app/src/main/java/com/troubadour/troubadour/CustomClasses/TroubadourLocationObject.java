package com.troubadour.troubadour.CustomClasses;

/**
 * Created by James on 4/15/2017.
 */

public class TroubadourLocationObject {

    private Double latitude;
    private Double longitude;

    public TroubadourLocationObject(Double mLattitude, Double mLongitude){
        setLatitude(mLattitude);
        setLongitude(mLongitude);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
