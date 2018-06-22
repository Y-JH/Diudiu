package com.dalimao.diudiu.go.lbs;

/**
 * @Title:LocationInfo
 * @Package:com.dalimao.diudiu.go.main
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2117:09
 */
public class LocationInfo {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private float rotation;

    public LocationInfo() {
    }

    public LocationInfo(int id, String name, double latitude, double longitude, float rotation) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rotation=" + rotation +
                '}';
    }
}
