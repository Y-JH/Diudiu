package com.dalimao.mytaxi.lbs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

import java.util.List;

/**
 * @Title:ILbsLayer
 * @Package:com.dalimao.diudiu.go.lbs
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2117:12
 */
public interface ILbsLayer {

    //获取地图view
    View getMapView();

    //设置定位图标
    void setLocationIconRes(int res);


    //添加或者更新标记点
    //包括位置、角度（通过id进行识别）
    void addOrUpdataMarker(LocationInfo locationInfo, Bitmap bitmap);



    void setLocationChangedListener(CommoneLocationChangedListener listener);


    //生命周期函数
    void onCreate(Bundle bundle);
    void onResume();
    void onSaveInstanceState(Bundle bundle);
    void onPause();
    void Destory();

    void addStartMarker(LatLng mStartPoint);//添加起点位置标记
    void addEndMarker(LatLng mEndPoint);//添加终点位置标记
    void addMarker(LatLng latLng, int res, String key);
    void drawDriverRoute(LatLonPoint mStartPoint,
                         LatLonPoint mEndPoint,
                         DriverRouteCompliteListener driverRouteCompliteListener,
            int resStartMarker, int resEndMarker);//绘制驾车路线

    //移动相机，通过围栏方式把起点和终点展现在视野范围
    void moveCamera(LatLng mStart, LatLng mEnd);

    /**
     * 功能：poi 地理位置搜索
     * @param key
     */
    void doSearchQuery(String key, PoiSearchListener listener);
    void doPoiSearch(String key, PoiAsynSearchListener listener);

    void clearAllMarkers();//清除地图上的所有标记

    void moveCameraToPoint(LatLng lat);//恢复视野区


    //目的：是把定位之后的结果暴露在MainActivity中
    interface CommoneLocationChangedListener {

        void onLocationChanged(LocationInfo locationInfo);
        void onLocation(LocationInfo locationInfo);
    }

    //目的：是把搜索之后的结果暴露在MainActivity中
    interface PoiSearchListener {

        void onPoiSearch(List<LocationInfo> infoList);
        void onError(int errorCode);
    }

    //目的：是把搜索之后的结果暴露在MainActivity中
    interface PoiAsynSearchListener {

        void onAsynPoiSearch(List<PoiItem> poiItemList);
        void onAsynPoiItemSearched(List<PoiItem> poiItemList);
        void onAsynError(int errorCode);
    }

    //目的：是把司机路线绘制后的结果暴露在MainActivity中
    interface DriverRouteCompliteListener {

        void onDriverRouteComplite(RouteInfo routeInfo);
        void onDriverError(String err);

    }
}
