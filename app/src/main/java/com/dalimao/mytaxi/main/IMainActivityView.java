package com.dalimao.mytaxi.main;

import com.dalimao.mytaxi.account.module.NearDriverResponse;
import com.dalimao.mytaxi.lbs.LocationInfo;
import com.dalimao.mytaxi.lbs.Order;

/**
 * @Title:IMainActivityView
 * @Package:com.dalimao.mytaxi.splash.main
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2017:22
 */
public interface IMainActivityView {

    //显示手机的输入对话框
    void showPhoneInputDialog();

    void showError(int code, String msg);

    void showLoginSucc();

    //显示司机列表
    void showNearDrivers(NearDriverResponse driverResponse);

    /**
     * 功能：司机的位置发生了变化
     * @param info
     */
    void showDriverLocationChanged(LocationInfo info);

    void callDriverSuc();
    void callDriverFail();

    //取消订单状态，即隐藏那写UI
    void hideDriverCallShowing();

    //取消成功
    void cancellSuc();
    //取消失败
    void cancellFail();

    //显示司机接单
    void showDriverAcceptOrder(Order order);

    //司机到达上车地点
    void showDriverArriveStart(Order mOrder);
    //开始进行行车
    void showDriverStartDrive(Order mOrder);
    //司机到达目标终点
    void showDriverArriveEnd(Order mOrder);
    //司机接到订单之后，开往乘客的位置变化更新
    void showDriverLocationUpdate(LocationInfo infoCar, Order mOrder);
    //司机接到订单之后，开往目的地的位置变化更新
    void showDriverLocation2Update(LocationInfo infoCar, Order mOrder);
}
