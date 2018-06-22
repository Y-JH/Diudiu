package com.dalimao.diudiu.go.account.presenter;

import com.dalimao.diudiu.go.common.eventbus.IEventBusSubscriber;
import com.dalimao.diudiu.go.lbs.LocationInfo;

/**
 * @Title:IMainActivity
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2017:19
 */
public interface IMainActivityPresenter extends IEventBusSubscriber{

    //检测用户的登录状态
    void checkLoginState();

    //获取附近的司机i朋友
    void fetchNearDrivers(LocationInfo locationInfo);
}
