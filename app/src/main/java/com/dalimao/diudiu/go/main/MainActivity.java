package com.dalimao.diudiu.go.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.dalimao.diudiu.go.MyTaxiApplication;
import com.dalimao.diudiu.go.R;
import com.dalimao.diudiu.go.account.module.AccountManagerImpl;
import com.dalimao.diudiu.go.account.module.IAccountManager;
import com.dalimao.diudiu.go.account.module.NearDriverResponse;
import com.dalimao.diudiu.go.account.presenter.IMainActivityPresenter;
import com.dalimao.diudiu.go.account.presenter.MainActivityPresenter;
import com.dalimao.diudiu.go.account.view.PhoneInputDialog;
import com.dalimao.diudiu.go.common.eventbus.RxBus;
import com.dalimao.diudiu.go.common.http.impl.OkHttpClientImpl;
import com.dalimao.diudiu.go.common.storage.SharedPreferencesDao;
import com.dalimao.diudiu.go.common.util.ToastUtil;
import com.dalimao.diudiu.go.lbs.GaodeLbsLayerImpl;
import com.dalimao.diudiu.go.lbs.ILbsLayer;
import com.dalimao.diudiu.go.lbs.LocationInfo;

import java.util.List;

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
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        gaodeLbsLayer.setLocationIconRes(R.drawable.location_marker);
        gaodeLbsLayer.setLocationChangedListener(new ILbsLayer.CommoneLocationChangedListener() {
            @Override
            public void onLocationChanged(LocationInfo locationInfo) {
                gaodeLbsLayer.addOrUpdataMarker(locationInfo,
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.navi_map_gps_locked));

                Log.e(TAG, "drivers num=>>>>.....................");
                //获取附近的司机i朋友
                getNearDrivers(locationInfo);
            }

            @Override
            public void onLocation(LocationInfo locationInfo) {

            }
        });

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_main);
        mapViewContainer.addView(gaodeLbsLayer.getMapView());

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
