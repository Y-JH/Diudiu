package com.dalimao.diudiu.go.account.presenter;

import android.util.Log;

import com.dalimao.diudiu.go.account.module.IAccountManager;
import com.dalimao.diudiu.go.account.module.LoginResponse;
import com.dalimao.diudiu.go.account.module.NearDriverResponse;
import com.dalimao.diudiu.go.common.eventbus.RxbusCallback;
import com.dalimao.diudiu.go.common.http.biz.BaseBizResponse;
import com.dalimao.diudiu.go.lbs.LocationInfo;
import com.dalimao.diudiu.go.main.IMainActivityView;

import static android.content.ContentValues.TAG;

/**
 * @Title:MainActivity
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2017:20
 */
public class MainActivityPresenter implements IMainActivityPresenter {

    IAccountManager iAccountManager;
    IMainActivityView iMainActivityView;

    public MainActivityPresenter(IAccountManager iAccountManager, IMainActivityView iMainActivityView){
        this.iAccountManager = iAccountManager;
        this.iMainActivityView = iMainActivityView;
    }
    @Override
    public void checkLoginState() {
        iAccountManager.loginByToken();
    }

    /**
     * 功能：获取附近的司机i朋友
     * @param locationInfo
     */
    @Override
    public void fetchNearDrivers(LocationInfo locationInfo) {
        iAccountManager.fetchNearDrivers(locationInfo);
    }
    @RxbusCallback
    public void nearDriversCallback(NearDriverResponse driverResponse){
            Log.d(TAG, "drivers---2--"+driverResponse.getCode());
        if(driverResponse.getCode() == BaseBizResponse.STATE_OK){
            Log.d(TAG, "drivers---3--"+driverResponse.getData().size());
            iMainActivityView.showNearDrivers(driverResponse);
        }
    }
    @RxbusCallback
    public void loginCallback(LoginResponse loginResponse){
        if(null != loginResponse){
            switch (loginResponse.getCode()){
                case IAccountManager.LOGIN_SUC:
                    iMainActivityView.showLoginSucc();
                    break;

                case IAccountManager.TOKEN_INVALID:
                    iMainActivityView.showError(IAccountManager.TOKEN_INVALID, "");
                    break;

                case IAccountManager.LOGIN_FAIL:
                    iMainActivityView.showError(IAccountManager.LOGIN_FAIL, "");
                    break;

                case IAccountManager.PW_ERR:
                    iMainActivityView.showError(IAccountManager.PW_ERR, "");
                    break;
            }
        }
    }

}
