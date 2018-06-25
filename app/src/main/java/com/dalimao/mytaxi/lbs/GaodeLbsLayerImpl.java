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
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.util.AMapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    //poi搜索
    private String mCityName, mCurrentDirection, mEndDirection;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiSearchListener poiSearchListener;

    //driver路线规划、绘制
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;

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

//        Log.e(TAG, "走入回调方法..");
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
            locationInfo.setName(aMapLocation.getCity());
            mCityName = aMapLocation.getCity();
            mCurrentDirection = aMapLocation.getPoiName();

            LatLng lat = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());// 当前坐标
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(lat, 16, 30, 0));

            if (isFirstLocation) {
                isFirstLocation = false;//说明是第一次
                mLocationChangeListener.onLocation(locationInfo);
                aMap.moveCamera(cameraUpdate);
            } else {
                mLocationChangeListener.onLocationChanged(locationInfo);
            }
        } else {
            String errText = "定位....," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e(TAG, errText);
        }

    }

    public String getCityName() {
        return mCityName;
    }

    public String getCurrentDirection() {
        return mCurrentDirection;
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

    @Override
    public void addStartMarker(final LatLng mStartPoint) {
        aMap.addMarker(new MarkerOptions()
                .position(mStartPoint)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_start)));
    }

    @Override
    public void addEndMarker(final LatLng mEndPoint) {
        aMap.addMarker(new MarkerOptions()
                .position(mEndPoint)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end)));
    }

    @Override
    public void addMarker(LatLng latLng, int res, String key) {
        Marker marker = markerMap.get(key);
        if(null == marker){
            markerMap.put(key, aMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(res))));
        }else {
            marker.setPosition(latLng);
        }

    }

    @Override
    public void drawDriverRoute(LatLonPoint mStartPoint, LatLonPoint mEndPoint,
                                final DriverRouteCompliteListener listener) {
        //执行了驾车模式，这里回调
        mRouteSearch = new RouteSearch(mContext);


        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null,
                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {
                aMap.clear();// 清理地图上的所有覆盖物
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                        if (driveRouteResult.getPaths().size() > 0) {
                            mDriveRouteResult = driveRouteResult;
                            final DrivePath drivePath = mDriveRouteResult.getPaths()
                                    .get(0);
                            DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(
                                    mContext, aMap, drivePath,
                                    mDriveRouteResult.getStartPos(),
                                    mDriveRouteResult.getTargetPos(), null);
                            drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                            drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                            drivingRouteOverlay.removeFromMap();
                            drivingRouteOverlay.addToMap();
                            drivingRouteOverlay.zoomToSpan();

                            int dis = (int) drivePath.getDistance();
                            int dur = (int) drivePath.getDuration();
                            String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                            int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                            RouteInfo routeInfo = new RouteInfo();
                            routeInfo.setDistance(dis);
                            routeInfo.setDuration(dur);
                            routeInfo.setTaxiCost(taxiCost);

                            listener.onDriverRouteComplite(routeInfo);
                        } else if (driveRouteResult != null && driveRouteResult.getPaths() == null) {
                            listener.onDriverError("无返回数据");
                        }

                    } else {
                        listener.onDriverError("无返回数据");
                    }
                } else {
                    listener.onDriverError("无返回数据 code=" + errorCode);
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    }

    /**
     * 功能：移动相机，通过围栏方式把起点和终点展现在视野范围
     *
     * @param mStart
     * @param mEnd
     */
    @Override
    public void moveCamera(LatLng mStart, LatLng mEnd) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder.include(mStart);
        builder.include(mEnd);
        LatLngBounds latLngBounds = builder.build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 300));
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


    /**
     * 功能：poi 地理位置搜索
     *
     * @param key
     */
    @Override
    public void doSearchQuery(String key, final PoiSearchListener listener) {
        // 1 组装关键字
        InputtipsQuery inputQuery = new InputtipsQuery(key, "");
        Inputtips inputTips = new Inputtips(mContext, inputQuery);
        // 2 监听处理搜索结果
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> tipList, int rCode) {
//                Log.e(TAG, "tipList===="+tipList.size()+"---"+tipList.get(0).getName()+"---"+tipList.get(0).getPoint().getLatitude());
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    // 正确返回解析结果
                    List<LocationInfo> locationInfos = new ArrayList<LocationInfo>();

                    for (int i = 0; i < tipList.size(); i++) {
                        Tip tip = tipList.get(i);
                        LocationInfo locationInfo =
                                new LocationInfo(tip.getPoint().getLatitude(),
                                        tip.getPoint().getLongitude());
                        locationInfo.setName(tip.getName());
                        locationInfos.add(locationInfo);
                    }
                    listener.onPoiSearch(locationInfos);
                } else {
                    listener.onError(rCode);
                }
            }
        });

        // 3 开始异步搜索
        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void doPoiSearch(String key, final PoiAsynSearchListener listener) {
        PoiSearch.Query mPoiSearchQuery = new PoiSearch.Query(key, "", getCityName());
        mPoiSearchQuery.requireSubPois(true);   //true 搜索结果包含POI父子关系; false
        mPoiSearchQuery.setPageSize(10);
        mPoiSearchQuery.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(mContext, mPoiSearchQuery);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int rcode) {
                if (rcode == AMapException.CODE_AMAP_SUCCESS) {
                    if (poiResult != null) {
                        List<PoiItem> poiItems = poiResult.getPois();
                        listener.onAsynPoiSearch(poiItems);

//                        mpoiadapter=new PoiListAdapter(mContext, poiItems);
//                        mPoiSearchList.setAdapter(mpoiadapter);
                    }
                } else {
                    listener.onAsynError(rcode);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int rcode) {
                if (rcode == AMapException.CODE_AMAP_SUCCESS) {
                    List<PoiItem> poiItems = new ArrayList<PoiItem>();
                    poiItems.add(poiItem);

                    listener.onAsynPoiItemSearched(poiItems);
//                    mpoiadapter=new PoiListAdapter(mContext, poiItems);
//                    mPoiSearchList.setAdapter(mpoiadapter);
                }
            }
        });
        poiSearch.searchPOIAsyn();
    }

    //清除地图上的所有标记
    @Override
    public void clearAllMarkers() {
        markerMap.clear();
        aMap.clear();// 清理地图上的所有覆盖物
    }

    //恢复视野
    @Override
    public void moveCameraToPoint(LatLng lat) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(lat, 16, 30, 0));
        aMap.moveCamera(cameraUpdate);

    }
}
