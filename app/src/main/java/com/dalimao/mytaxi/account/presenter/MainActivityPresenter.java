package com.dalimao.mytaxi.account.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.dalimao.mytaxi.account.module.IAccountManager;
import com.dalimao.mytaxi.account.module.LoginResponse;
import com.dalimao.mytaxi.account.module.NearDriverResponse;
import com.dalimao.mytaxi.account.module.OrderStateOptResponse;
import com.dalimao.mytaxi.common.eventbus.RxbusCallback;
import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.lbs.CallDriverBean;
import com.dalimao.mytaxi.lbs.LocationInfo;
import com.dalimao.mytaxi.lbs.Order;
import com.dalimao.mytaxi.main.IMainActivityView;

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
    private Order mOrder;//缓存起来


    public MainActivityPresenter(IAccountManager iAccountManager, IMainActivityView iMainActivityView) {
        this.iAccountManager = iAccountManager;
        this.iMainActivityView = iMainActivityView;
    }

    @Override
    public void checkLoginState() {
        iAccountManager.loginByToken();
    }

    /**
     * 功能：获取附近的司机i朋友
     *
     * @param locationInfo
     */
    @Override
    public void fetchNearDrivers(LocationInfo locationInfo) {
        iAccountManager.fetchNearDrivers(locationInfo);
    }


    /**
     * 功能：位置状态变化
     * 1，未在订单状态下的司机位置变化
     * 2，在订单状态下-已接单-前往乘客位置下的司机位置变化
     * 3，在订单状态下-已到乘客位置并开始行车的位置变化
     *
     * @param info
     */
    @RxbusCallback
    public void driverLocationChanged(LocationInfo info) {
        if (null != mOrder && mOrder.getState() == OrderStateOptResponse.ORDER_STATE_ACCEPT) {
            iMainActivityView.showDriverLocationUpdate(info, mOrder);
        } else if (null != mOrder && mOrder.getState() == OrderStateOptResponse.ORDER_STATE_START_DRIVE) {
            iMainActivityView.showDriverLocation2Update(info, mOrder);
        } else {
            iMainActivityView.showDriverLocationChanged(info);
        }
    }

    /**
     * 功能：订单状态变化
     *
     * @param response
     */
    @RxbusCallback
    public void callDriverResponse(OrderStateOptResponse response) {
        if (response.getState() == OrderStateOptResponse.ORDER_STATE_CREATE) {
            // 呼叫司机
            mOrder = response.getData();
            if (response.getCode() == BaseBizResponse.STATE_OK) {
                iMainActivityView.callDriverSuc();
            } else {
                iMainActivityView.callDriverFail();
            }
        } else if (response.getState() == OrderStateOptResponse.ORDER_STATE_CANCEL) {
            //取消订单
            mOrder = response.getData();
            if (response.getCode() == BaseBizResponse.STATE_OK) {
                iMainActivityView.cancellSuc();
            } else {
                iMainActivityView.cancellFail();
            }
        } else if (response.getState() == OrderStateOptResponse.ORDER_STATE_ACCEPT) {
            //司机接单
            mOrder = response.getData();
            iMainActivityView.showDriverAcceptOrder(mOrder);
        } else if (response.getState() == OrderStateOptResponse.ORDER_STATE_ARRIVE_START) {
            //司机到达上车地点
            mOrder = response.getData();
            iMainActivityView.showDriverArriveStart(mOrder);
        } else if (response.getState() == OrderStateOptResponse.ORDER_STATE_START_DRIVE) {
            //开始进行行车
            mOrder = response.getData();
            iMainActivityView.showDriverStartDrive(mOrder);
        } else if (response.getState() == OrderStateOptResponse.ORDER_STATE_ARRIVE_END) {
            //司机到达目标终点
            mOrder = response.getData();
            iMainActivityView.showDriverArriveEnd(mOrder);

        }
    }

    /**
     * 功能：更新服务器中客户端位置信息
     *
     * @param locationInfo
     */
    @Override
    public void updateLocationToServer(LocationInfo locationInfo) {
        iAccountManager.updateLocationToServer(locationInfo);
    }

    @Override
    public void callDriver(CallDriverBean callDriverBean) {
        iAccountManager.callDriver(callDriverBean);
    }

    /**
     * 功能：取消呼叫订单
     */
    @Override
    public void cancelCall() {
        String orderId = String.valueOf(mOrder.getOrderId());
        if (!TextUtils.isEmpty(orderId)) {
            iAccountManager.cancelCall(orderId);

        } else {
            iMainActivityView.hideDriverCallShowing();
        }
    }

    @RxbusCallback
    public void nearDriversCallback(NearDriverResponse driverResponse) {
        Log.d(TAG, "drivers---2--" + driverResponse.getCode());
        if (driverResponse.getCode() == BaseBizResponse.STATE_OK) {
            Log.d(TAG, "drivers---3--" + driverResponse.getData().size());
            iMainActivityView.showNearDrivers(driverResponse);
        }
    }

    @RxbusCallback
    public void loginCallback(LoginResponse loginResponse) {
        if (null != loginResponse) {
            switch (loginResponse.getCode()) {
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
