package com.dalimao.mytaxi.splash.account.presenter;


import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.splash.account.module.CheckResponse;
import com.dalimao.mytaxi.splash.account.module.IAccountManager;
import com.dalimao.mytaxi.splash.account.view.ISmsCodeDialogView;
import com.dalimao.mytaxi.splash.common.eventbus.RxbusCallback;

import java.lang.ref.WeakReference;

/**
 * @Title:SmsCodeDialogPresenter
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2015:23
 */
public class SmsCodeDialogPresenter implements ISmsCodeDialogPresenter {
    private IAccountManager iAccountManager;
    private ISmsCodeDialogView iSmsCodeDialogView;

    public SmsCodeDialogPresenter(IAccountManager iAccountManager,
                                  ISmsCodeDialogView iSmsCodeDialogView){
        this.iAccountManager = iAccountManager;
        this.iSmsCodeDialogView = iSmsCodeDialogView;
    }


    @Override
    public void requestSendSmsCode(String phone) {
        iAccountManager.fetchSMSCode(phone);
    }

    @Override
    public void requestCheckSmsCode(String phone, String smsCode) {
        iAccountManager.checkSnmCode(phone, smsCode);
    }

    @Override
    public void requestCheckUserExitst(String phone) {
        iAccountManager.checkUserExist(phone);
    }


    @RxbusCallback
    public void checkCallback(CheckResponse checkResponse) {
        if (null != checkResponse) {
            switch (checkResponse.getCode()){
                case IAccountManager.SMS_SEND_SUC:
                    iSmsCodeDialogView.showCountDownTimer();
                    break;

                case IAccountManager.SMS_SEND_FAIL:
                    iSmsCodeDialogView.showError(IAccountManager.SMS_SEND_FAIL, "");
                    break;

                case IAccountManager.SMS_CHECK_SUC:
                    iSmsCodeDialogView.showSmsCheckCodeState(true);
                    break;

                case IAccountManager.SMS_CHECK_FAIL:
                    iSmsCodeDialogView.showError(IAccountManager.SMS_CHECK_FAIL, "");
                    break;

                case IAccountManager.USER_EXIST:
                    iSmsCodeDialogView.showUserExit(true);
                    break;

                case IAccountManager.USER_NOT_EXIST:
                    iSmsCodeDialogView.showUserExit(false);
                    break;


            }
        }

    }

    //在这里进行消息的接收和处理
    private static class MyHandler extends Handler {

        WeakReference<SmsCodeDialogPresenter> refContext;
        public MyHandler(SmsCodeDialogPresenter presenter){
            refContext = new WeakReference<SmsCodeDialogPresenter>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }
}
