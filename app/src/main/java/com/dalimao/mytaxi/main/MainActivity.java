package com.dalimao.mytaxi.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

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
import com.dalimao.mytaxi.common.util.ToastUtil;
import com.dalimao.mytaxi.lbs.GaodeLbsLayerImpl;
import com.dalimao.mytaxi.lbs.ILbsLayer;
import com.dalimao.mytaxi.lbs.LocationInfo;

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
    private IMainActivityPresenter iMainActivityPresenter;
    private GaodeLbsLayerImpl gaodeLbsLayer;
    private Bitmap bitmap;
    private String mPushKey;

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
        init();

        initPush();
    }

    //初始化推送服务
    void initPush(){
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
                //获取附近的司机i朋友
                //获取司机位置信息，应该是进行一次
                getNearDrivers(locationInfo);
            }
        });

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_main);
        mapViewContainer.addView(gaodeLbsLayer.getMapView());

    }

    /**
     * 功能：更新服务器中客户端位置信息
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
        Log.e(TAG, "drivers num="+driverResponse.getData().size());
        List<LocationInfo> list = driverResponse.getData();
        for (int i = 0; i < list.size(); i++) {
            int index = i;
            if (index > 2) index -= 2;
            gaodeLbsLayer.addOrUpdataMarker(list.get(i), bitmap);
        }
    }

    /**
     * 功能：司机的位置发生了变化，重新绘制
     * @param info
     */
    @Override
    public void showDriverLocationChanged(LocationInfo info) {
        gaodeLbsLayer.addOrUpdataMarker(info, bitmap);
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
