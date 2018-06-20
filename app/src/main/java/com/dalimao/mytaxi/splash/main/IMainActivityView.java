package com.dalimao.mytaxi.splash.main;

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
}
