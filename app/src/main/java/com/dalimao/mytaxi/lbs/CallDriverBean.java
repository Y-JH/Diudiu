package com.dalimao.mytaxi.lbs;

/**
 * @Title:CallDriverBean
 * @Package:com.dalimao.mytaxi.lbs
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2514:43
 */
public class CallDriverBean {
    String key;
    String startLatitude;
    String startLongitude;
    String endLatitude;
    String endLongitude;
    String startAddr;
    String endAddr;
    String phone;
    String cost;

    public CallDriverBean() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(String startLatitude) {
        this.startLatitude = startLatitude;
    }

    public String getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(String startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        this.endLatitude = endLatitude;
    }

    public String getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(String startAddr) {
        this.startAddr = startAddr;
    }

    public String getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(String endAddr) {
        this.endAddr = endAddr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "CallDriverBean{" +
                "key='" + key + '\'' +
                ", startLatitude='" + startLatitude + '\'' +
                ", startLongitude='" + startLongitude + '\'' +
                ", endLatitude='" + endLatitude + '\'' +
                ", endLongitude='" + endLongitude + '\'' +
                ", startAddr='" + startAddr + '\'' +
                ", endAddr='" + endAddr + '\'' +
                ", phone='" + phone + '\'' +
                ", cost='" + cost + '\'' +
                '}';
    }
}
