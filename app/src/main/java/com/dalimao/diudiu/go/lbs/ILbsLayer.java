package com.dalimao.diudiu.go.lbs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

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


    interface CommoneLocationChangedListener {

        void onLocationChanged(LocationInfo locationInfo);
        void onLocation(LocationInfo locationInfo);
    }
}
