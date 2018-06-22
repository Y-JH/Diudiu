package com.dalimao.mytaxi.lbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title:GaodeLbsLayerImpl
 * @Package:com.dalimao.diudiu.go.lbs
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2117:21
 */
public class GaodeLbsLayerImpl implements ILbsLayer, LocationSource, AMapLocationListener {

    private final String TAG = "GaodeLbsLayerImpl";
    private static String KEY_MY_LOCATION = "0x11";
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private Context mContext;
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private CommoneLocationChangedListener mLocationChangeListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    private Map<String, Marker> markerMap = new HashMap<>();
    private boolean isFirstLocation = true;

    public GaodeLbsLayerImpl(Context context) {
        mContext = context;
        //创建地图对象
        mapView = new MapView(context);
        aMap = mapView.getMap();
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false


    }


    @Override
    public void onCreate(Bundle bundle) {
        mapView.onCreate(bundle);// 此方法必须重写


    }

    void setUpLocationClient() {
        // aMap.setMyLocationType()
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
        }

        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();
    }

    @Override
    public View getMapView() {
        return mapView;
    }

    /**
     * 功能：设置图标
     *
     * @param res
     */
    @Override
    public void setLocationIconRes(int res) {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(res));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    public void addOrUpdataMarker(LocationInfo locationInfo, Bitmap bitmap) {
        Marker marker = markerMap.get(locationInfo.getKey());
        LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());

        if (null != marker) {
            //如果标记marker存在 更新位置
            mCircle.setCenter(latLng);
            mCircle.setRadius(locationInfo.getRotation());
            marker.setPosition(latLng);
            marker.setRotateAngle(locationInfo.getRotation());
        } else {
            //如果标记marker不存在 创建
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            MarkerOptions options = new MarkerOptions();
            options.icon(descriptor);
            options.anchor(0.5f, 0.5f);
            options.position(latLng);
            mLocMarker = aMap.addMarker(options);
            addCircle(latLng, locationInfo.getRotation());//添加定位精度圆
            markerMap.put(locationInfo.getKey(), mLocMarker);
            mLocMarker.setTitle("my-location");
            if (locationInfo.getKey().equals(KEY_MY_LOCATION)) {
                // 传感器控制我的位置标记的旋转角度
                mSensorHelper.setCurrentMarker(marker);
            }
        }

        Log.e(TAG, "走入回调方法..");
        mSensorHelper.setCurrentMarker(marker);

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation && aMapLocation.getErrorCode() == 0) {

            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setKey(KEY_MY_LOCATION);
            locationInfo.setLatitude(aMapLocation.getLatitude());
            locationInfo.setLongitude(aMapLocation.getLongitude());
            locationInfo.setRotation(aMapLocation.getAccuracy());

            LatLng lat = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());// 当前坐标
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(lat, 16, 30, 0));

            if (isFirstLocation) {
                isFirstLocation = false;//说明是第一次
                mLocationChangeListener.onLocation(locationInfo);
            } else {
                mLocationChangeListener.onLocationChanged(locationInfo);
            }
            aMap.moveCamera(cameraUpdate);
        } else {
            String errText = "定位....," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e(TAG, errText);
        }

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void setLocationChangedListener(CommoneLocationChangedListener listener) {
        mLocationChangeListener = listener;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        registerSensorHelper();
        setUpLocationClient();
        isFirstLocation = true;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        mapView.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        unRegisterSensorHelper();

        mapView.onPause();
        deactivate();
    }

    @Override
    public void Destory() {
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    void registerSensorHelper() {
        if (null == mSensorHelper)
            mSensorHelper = new SensorEventHelper(mContext);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    void unRegisterSensorHelper() {
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }
}
