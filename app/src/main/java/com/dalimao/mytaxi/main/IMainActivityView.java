package com.dalimao.mytaxi.main;

import com.dalimao.mytaxi.account.module.NearDriverResponse;
import com.dalimao.mytaxi.lbs.LocationInfo;

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
}
