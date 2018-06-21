package com.dalimao.mytaxi.splash.account.presenter;

import com.dalimao.mytaxi.splash.common.eventbus.IEventBusSubscriber;

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

}
