package com.dalimao.diudiu.go.account.presenter;

import com.dalimao.diudiu.go.common.eventbus.IEventBusSubscriber;

/**
 * @Title:ISmsCodeDialogPresenter
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description:向上接收视图层传过来的指令，再把指令传递到module层；
 *              向下接收module层解析过来的数据，然后由此数据再向上操纵视图层；
 *              从view层的角度，其实是为视图层来提供服务的；
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2014:16
 */
public interface ISmsCodeDialogPresenter extends IEventBusSubscriber {

    //请求后台->发送验证码
    void requestSendSmsCode(String phone);

    //请求后台->校验验证码
    void requestCheckSmsCode(String phone, String smsCode);

    //请求后台->校验用户是否已经注册
    void requestCheckUserExitst(String phone);


}
