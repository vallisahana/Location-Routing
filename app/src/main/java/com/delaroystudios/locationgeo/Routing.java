package com.delaroystudios.locationgeo;

public class Routing {


    private String name;
    private Double latitude;
    private Double longitude;
    private String distance;

    public Routing() {
    }

    public Routing(String name, Double latitude, Double longitude, String distance) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
