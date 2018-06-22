package com.dalimao.diudiu.go.main;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.dalimao.diudiu.go.MyTaxiApplication;
import com.dalimao.diudiu.go.R;
import com.dalimao.diudiu.go.account.module.AccountManagerImpl;
import com.dalimao.diudiu.go.account.module.IAccountManager;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked));
            }

            @Override
            public void onLocation(LocationInfo locationInfo) {

            }
        });

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_main);
        mapViewContainer.addView(gaodeLbsLayer.getMapView());

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
