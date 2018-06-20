package com.dalimao.mytaxi.splash.account.presenter;

/**
 * @Title:ICreatePasswordDialogPresenter
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description:向上接收视图层传过来的指令，再把指令传递到module层；
 *              向下接收module层解析过来的数据，然后由此数据再向上操纵视图层；
 *              从view层的角度，其实是为视图层来提供服务的；
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2014:16
 */
public interface ICreatePasswordDialogPresenter {

    //密码输入的合法性
    void checkPw(String pw, String pwr);

    //请求后台->注册
    void requestRegister(String phone, String pw);

    //请求后台->登录
    void requestLogin(String phone, String pw);


}
