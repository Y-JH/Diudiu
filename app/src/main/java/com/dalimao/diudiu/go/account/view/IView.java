package com.dalimao.diudiu.go.account.view;

/**
 * @Title:IView
 * @Package:com.dalimao.mytaxi.splash.account.view
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2014:50
 */
public interface IView {
    //加载动画
    void showLoading();

    void hideLoading();

    //显示错误
    void showError(int code, String msg);
}
