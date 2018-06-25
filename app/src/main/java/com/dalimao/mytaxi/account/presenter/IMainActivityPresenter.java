package com.dalimao.mytaxi.account.presenter;

import com.dalimao.mytaxi.common.eventbus.IEventBusSubscriber;
import com.dalimao.mytaxi.lbs.CallDriverBean;
import com.dalimao.mytaxi.lbs.LocationInfo;

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

    /**
     * 功能：更新服务器中客户端位置信息
     * @param locationInfo
     */
    void updateLocationToServer(LocationInfo locationInfo);


    /**
     * 功能：呼叫司机
     * @param callDriverBean
     */
    void callDriver(CallDriverBean callDriverBean);

    //功能：取消呼叫订单
    void cancelCall();
}
