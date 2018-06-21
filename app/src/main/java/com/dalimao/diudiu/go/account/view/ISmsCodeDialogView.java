package com.dalimao.diudiu.go.account.view;

/**
 * @Title:ISmsCodeDialogView
 * @Package:com.dalimao.mytaxi.splash.account.view
 * @Description:发送验证码页面
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2014:29
 */
public interface ISmsCodeDialogView extends IView{


    //显示倒计时
    void showCountDownTimer();
    void showLoading();

    //显示错误
    void showError(int code, String msg);

    //验证码的状态更新UI
    void showSmsCheckCodeState(boolean bol);

    void showUserExit(boolean bol);

    //关闭视图
    void close();
}
