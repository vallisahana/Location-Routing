package com.delaroystudios.locationgeo.provider;

import java.util.List;

public class PlaceDetails {

    int placeId,placeIsDelete;
    String placeName,placeLatitude,placeLongitude;

    public PlaceDetails(List<PlaceDetails> placeDetails) {
    }

    public PlaceDetails(int placeId, String placeName, String placeLatitude, String placeLongitude, int placeIsDelete) {
        this.placeId = placeId;
        this.placeIsDelete = placeIsDelete;
        this.placeName = placeName;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public int getPlaceIsDelete() {
        return placeIsDelete;
    }

    public void setPlaceIsDelete(int placeIsDelete) {
        this.placeIsDelete = placeIsDelete;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(String placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public String getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(String placeLongitude) {
        this.placeLongitude = placeLongitude;
    }
}
