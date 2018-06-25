package com.dalimao.mytaxi.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.module.AccountManagerImpl;
import com.dalimao.mytaxi.account.module.IAccountManager;
import com.dalimao.mytaxi.account.module.NearDriverResponse;
import com.dalimao.mytaxi.account.presenter.IMainActivityPresenter;
import com.dalimao.mytaxi.account.presenter.MainActivityPresenter;
import com.dalimao.mytaxi.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.common.eventbus.RxBus;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.AMapUtil;
import com.dalimao.mytaxi.common.util.DevUtil;
import com.dalimao.mytaxi.common.util.ToastUtil;
import com.dalimao.mytaxi.lbs.CallDriverBean;
import com.dalimao.mytaxi.lbs.GaodeLbsLayerImpl;
import com.dalimao.mytaxi.lbs.ILbsLayer;
import com.dalimao.mytaxi.lbs.ITextWatcher;
import com.dalimao.mytaxi.lbs.LocationInfo;
import com.dalimao.mytaxi.lbs.PoiAdapter;
import com.dalimao.mytaxi.lbs.PoiListAdapter;
import com.dalimao.mytaxi.lbs.RouteInfo;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * 1 检查本地纪录(登录态检查)
 * 2 若用户没登录则登录
 * 3 登录之前先校验手机号码
 * 4 token 有效使用 token 自动登录
 * 地图初始化
 */

public class MainActivity extends AppCompatActivity
        implements IMainActivityView {
    private final static String TAG = "MainActivityPresenter";
    private FrameLayout mapContainer;//地图mapview容器
    private AutoCompleteTextView mStartEdit;//起点
    private AutoCompleteTextView mEndEdit;//终点
    private PoiAdapter mEndAdapter;
    private TextView mCity;//标题显示城市名称

    private IMainActivityPresenter iMainActivityPresenter;
    private GaodeLbsLayerImpl gaodeLbsLayer;
    private ListView mListView;
    private Bitmap bitmap;
    private String mPushKey;
    private PoiListAdapter mpoiadapter;
    private LatLng mStart, mEnd;

    //  操作状态相关元素
    private LinearLayout optArea;
    private LinearLayout loadingArea;
    private TextView mTips;
    private TextView mLoadingText;
    private Button mBtnCall;
    private Button mBtnCancel;
    private Button mBtnPay;
    private float mCost;
    private boolean mIsLogin = true;
    private String mEndDirection;
    private LocationInfo locationInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.y7);
        iMainActivityPresenter = new MainActivityPresenter(new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferencesDao(MyTaxiApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT)),
                this);

        RxBus.getInstance().register(iMainActivityPresenter);
        //检查用户是否登录
        iMainActivityPresenter.checkLoginState();

        gaodeLbsLayer = new GaodeLbsLayerImpl(this);
        gaodeLbsLayer.onCreate(savedInstanceState);
        initViews();
        initViews2();
        init();
        initPush();
    }

    private void initViews2() {
        optArea = (LinearLayout) findViewById(R.id.optArea);
        loadingArea = (LinearLayout) findViewById(R.id.loading_area);
        mLoadingText = (TextView) findViewById(R.id.loading_text);
        mBtnCall = (Button) findViewById(R.id.btn_call_driver);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnPay = (Button) findViewById(R.id.btn_pay);
        mTips = (TextView) findViewById(R.id.tips_info);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_call_driver:
                        // 呼叫司机
                        callDriver();
                        break;

                    case R.id.btn_cancel:
                        //取消呼叫
                        cancelCall();
                        break;
                }
            }


        };
        mBtnCall.setOnClickListener(listener);
        mBtnCancel.setOnClickListener(listener);
        mBtnPay.setOnClickListener(listener);
    }

    private void callDriver() {
        if (mIsLogin) {
            // 已登录，直接呼叫
            mTips.setVisibility(View.GONE);
            loadingArea.setVisibility(View.VISIBLE);
            mLoadingText.setText(getString(R.string.calling_driver));
            mBtnCancel.setEnabled(true);
            mBtnCall.setEnabled(false);

            CallDriverBean callDriverBean = new CallDriverBean();
            callDriverBean.setKey(mPushKey);
            callDriverBean.setCost(String.valueOf(mCost));
            callDriverBean.setPhone("");
            callDriverBean.setStartLatitude(String.valueOf(mStart.latitude));
            callDriverBean.setStartLongitude(String.valueOf(mStart.longitude));
            callDriverBean.setStartAddr(gaodeLbsLayer.getCurrentDirection());
            callDriverBean.setEndAddr(mEndDirection);
            callDriverBean.setEndLongitude(String.valueOf(mEnd.longitude));
            callDriverBean.setEndLatitude(String.valueOf(mEnd.latitude));
            //   请求呼叫
            iMainActivityPresenter.callDriver(callDriverBean);
        } else {
            // 未登录，先登录
//            iMainActivityPresenter.loginByToken();
            ToastUtil.show(this, "用户未登录");
        }
    }


    /**
     * 功能：取消呼叫快车
     */
    private void cancelCall() {
        if (!mBtnCall.isEnabled()) {
            //说明已经点击了呼叫
            mTips.setVisibility(View.GONE);
            loadingArea.setVisibility(View.VISIBLE);
            mLoadingText.setText("订单取消中...");
            mBtnCancel.setEnabled(false);

            iMainActivityPresenter.cancelCall();

        } else {
            optArea.setVisibility(View.GONE);
        }
    }


    private void initViews() {
        mapContainer = (FrameLayout) findViewById(R.id.map_container);
        mCity = (TextView) findViewById(R.id.city);
        mStartEdit = (AutoCompleteTextView) findViewById(R.id.start);
        mEndEdit = (AutoCompleteTextView) findViewById(R.id.end);
        mListView = (ListView) findViewById(R.id.listView);


        mStartEdit.addTextChangedListener(new ITextWatcher(new ITextWatcher.TextWatcherListener() {
            @Override
            public void onTextWatcher(Editable s) {
                searchPOI(s);
            }
        }));

        mEndEdit.addTextChangedListener(new ITextWatcher(new ITextWatcher.TextWatcherListener() {
            @Override
            public void onTextWatcher(Editable s) {
                searchPOI(s);

            }
        }));

        //显示一个相关地名的列表
//        final String key = mStartEdit.getText().toString();
//        gaodeLbsLayer.doPoiSearch(key, new ILbsLayer.PoiAsynSearchListener() {
//            @Override
//            public void onAsynPoiSearch(List<PoiItem> poiItemList) {
//                mpoiadapter=new PoiListAdapter(MainActivity.this, poiItemList);
//                mListView.setAdapter(mpoiadapter);
//            }
//
//            @Override
//            public void onAsynPoiItemSearched(List<PoiItem> poiItemList) {
//                mpoiadapter =new PoiListAdapter(MainActivity.this, poiItemList);
//                mListView.setAdapter(mpoiadapter);
//            }
//
//            @Override
//            public void onAsynError(int errorCode) {
//
//            }
//        });
    }


    /**
     * 功能：根据输入edt输入内容搜索
     *
     * @param s
     */
    private void searchPOI(Editable s) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            gaodeLbsLayer.doSearchQuery(newText, new ILbsLayer.PoiSearchListener() {
                @Override
                public void onPoiSearch(List<LocationInfo> infoList) {
                    updatePoiList(infoList);
                }

                @Override
                public void onError(int errorCode) {

                }
            });
        }
    }


    /**
     * 功能：根据输入edt输入内容搜索
     *
     * @param results
     */
    private void updatePoiList(final List<LocationInfo> results) {
        List<String> listString = new ArrayList<String>();
        for (int i = 0; i < results.size(); i++) {
            listString.add(results.get(i).getName());
        }

        Log.e(TAG, "listString==" + listString.size() + listString.get(0));
        if (mEndAdapter == null) {
            mEndAdapter = new PoiAdapter(getApplicationContext(), listString);
            mEndEdit.setAdapter(mEndAdapter);

        } else {
            mEndAdapter.setData(listString);
        }
        mEndEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationInfo info = results.get(position);
                mEndDirection = info.getName();
                DevUtil.closeInputMethod(MainActivity.this);
                //标记终点
                mEnd = new LatLng(info.getLatitude(), info.getLongitude());
                gaodeLbsLayer.addEndMarker(mEnd);
                gaodeLbsLayer.drawDriverRoute(AMapUtil.convertToLatLonPoint(mStart), AMapUtil.convertToLatLonPoint(mEnd), new ILbsLayer.DriverRouteCompliteListener() {
                    @Override
                    public void onDriverRouteComplite(RouteInfo routeInfo) {
                        ToastUtil.show(MainActivity.this, routeInfo.getTaxiCost() + "<-");
                        mCost = routeInfo.getTaxiCost();
                        gaodeLbsLayer.moveCamera(mStart, mEnd);


                        //显示操作区域
                        optArea.setVisibility(View.VISIBLE);
                        String infoString = getString(R.string.route_info);
                        infoString = String.format(infoString,
                                new Float(routeInfo.getDistance()).intValue(),
                                mCost,
                                routeInfo.getDuration());
                        mTips.setVisibility(View.VISIBLE);
                        mTips.setText(infoString);

                    }

                    @Override
                    public void onDriverError(String err) {

                    }
                });
            }
        });
        mEndAdapter.notifyDataSetChanged();
    }

    //初始化推送服务
    void initPush() {
        // 推送服务
        // 初始化BmobSDK
        Bmob.initialize(this, API.Config.getAppId());
        // 使用推送服务时的初始化操作
        BmobInstallation installation = BmobInstallation.getCurrentInstallation(this);
        installation.save();
        mPushKey = installation.getInstallationId();
        // 启动推送服务
        BmobPush.startWork(this);
    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        gaodeLbsLayer.setLocationIconRes(R.drawable.location_marker);
        gaodeLbsLayer.setLocationChangedListener(new ILbsLayer.CommoneLocationChangedListener() {
            @Override
            public void onLocationChanged(LocationInfo locationInfo) {

                //客户端的位置、描点和上传个人位置信息，应该是一直进行的
                //更新服务器中客户端位置信息
                gaodeLbsLayer.addOrUpdataMarker(locationInfo,
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.navi_map_gps_locked));
                updateLocationToServer(locationInfo);

            }

            @Override
            public void onLocation(LocationInfo locationInfo) {
                mCity.setText(locationInfo.getName());
                mStartEdit.setText(gaodeLbsLayer.getCurrentDirection());
                MainActivity.this.locationInfo = locationInfo;
                mStart = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
                //标记起点
                gaodeLbsLayer.addStartMarker(mStart);
                //获取附近的司机i朋友
                //获取司机位置信息，应该是进行一次
                getNearDrivers(locationInfo);
            }
        });

        mapContainer.addView(gaodeLbsLayer.getMapView());

    }

    /**
     * 功能：更新服务器中客户端位置信息
     *
     * @param locationInfo
     */
    private void updateLocationToServer(LocationInfo locationInfo) {
        iMainActivityPresenter.updateLocationToServer(locationInfo);
    }

    /**
     * 功能：获取附近的司机i朋友
     *
     * @param locationInfo
     */
    private void getNearDrivers(LocationInfo locationInfo) {
        iMainActivityPresenter.fetchNearDrivers(locationInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gaodeLbsLayer.Destory();
        RxBus.getInstance().unRegister(iMainActivityPresenter);
    }

    /**
     * 显示手机输入框
     */
    @Override
    public void showPhoneInputDialog() {
        PhoneInputDialog dialog = new PhoneInputDialog(this);
        dialog.show();
    }

    @Override
    public void showError(int code, String msg) {
        switch (code) {
            case IAccountManager.TOKEN_INVALID:
                showPhoneInputDialog();
                break;

            case IAccountManager.LOGIN_FAIL:
                ToastUtil.show(MainActivity.this,
                        getString(R.string.error_server));
                break;

            case IAccountManager.PW_ERR:
                ToastUtil.show(MainActivity.this,
                        getString(R.string.password_error));
                break;
        }
    }

    @Override
    public void showLoginSucc() {
        ToastUtil.show(MainActivity.this,
                getString(R.string.login_suc));
    }

    /**
     * 功能：显示司机列表
     *
     * @param driverResponse
     */
    @Override
    public void showNearDrivers(NearDriverResponse driverResponse) {
        List<LocationInfo> list = driverResponse.getData();
        for (int i = 0; i < list.size(); i++) {
            int index = i;
            if (index > 2) index -= 2;

            LocationInfo info = list.get(i);

            Log.e(TAG, "info.getKey====>>>" + info.getKey());
            LatLng latLng = new LatLng(info.getLatitude(), info.getLongitude());
            gaodeLbsLayer.addMarker(latLng, R.drawable.amap_car, info.getKey());
        }
    }

    /**
     * 功能：司机的位置发生了变化，重新绘制
     *
     * @param info
     */
    @Override
    public void showDriverLocationChanged(LocationInfo info) {
        LatLng latLng = new LatLng(info.getLatitude(), info.getLongitude());
        gaodeLbsLayer.addMarker(latLng, R.drawable.amap_car, info.getKey());
    }

    @Override
    public void callDriverSuc() {
        loadingArea.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setText(getString(R.string.show_call_suc));
    }

    @Override
    public void callDriverFail() {
        loadingArea.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setText(getString(R.string.show_call_fail));
    }

    /**
     * 功能：取消订单状态，即隐藏那写UI
     */
    @Override
    public void hideDriverCallShowing() {
        //清除地图上的所有标记
        gaodeLbsLayer.clearAllMarkers();

        //添加定位标记
        gaodeLbsLayer.addOrUpdataMarker(locationInfo, BitmapFactory.decodeResource(getResources(),
                R.drawable.navi_map_gps_locked));

        //恢复地图的视野
        gaodeLbsLayer.moveCameraToPoint(mStart);

        //获取附近司机
        getNearDrivers(locationInfo);

        //隐藏操作栏
        optArea.setVisibility(View.GONE);
    }

    @Override
    public void cancellSuc() {
        ToastUtil.show(this, "取消订单成功");
        this.hideDriverCallShowing();
    }

    @Override
    public void cancellFail() {
        ToastUtil.show(this, "取消订单失败");
        mBtnCancel.setEnabled(true);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        gaodeLbsLayer.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        gaodeLbsLayer.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gaodeLbsLayer.onSaveInstanceState(outState);
    }

}
