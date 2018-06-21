package com.dalimao.diudiu.go.account.view;

/**
 * @Title:ICreatePasswordDialogView
 * @Package:com.dalimao.mytaxi.splash.account.view
 * @Description:注册页面
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2014:29
 */
public interface ICreatePasswordDialogView extends IView {

    //显示注册成功
    void showRegisterSuc();

    //显示登录成功
    void showLoginSuc();

    void checkPw(String pw, String pwr);
}
