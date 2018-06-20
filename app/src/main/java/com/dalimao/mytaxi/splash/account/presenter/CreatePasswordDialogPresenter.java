package com.dalimao.mytaxi.splash.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.splash.account.module.IAccountManager;
import com.dalimao.mytaxi.splash.account.view.ICreatePasswordDialogView;

import java.lang.ref.WeakReference;

/**
 * @Title:CreatePasswordDialogPresenter
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description: CreatePasswordDialogPresenter
 *                  向上依赖视图层的抽象(link@ICreatePasswordDialogView)，
 *                  向下依赖module层的抽象(link@ICreatePasswordDialogView)；
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2015:24
 */
public class CreatePasswordDialogPresenter implements ICreatePasswordDialogPresenter {
    private IAccountManager iAccountManager;
    private ICreatePasswordDialogView iCreatePasswordDialogView;

    public CreatePasswordDialogPresenter(IAccountManager iAccountManager,
                                         ICreatePasswordDialogView iCreatePasswordDialogView){
        this.iAccountManager = iAccountManager;
        this.iCreatePasswordDialogView = iCreatePasswordDialogView;
        iAccountManager.setHandler(new MyHandler(this));
    }

    @Override
    public void checkPw(String pw, String pwr) {
        iCreatePasswordDialogView.checkPw(pw, pwr);
    }

    @Override
    public void requestRegister(String phone, String pw) {
        iAccountManager.register(phone, pw);
    }

    @Override
    public void requestLogin(String phone, String pw) {
        iAccountManager.login(phone, pw);
    }



    //在这里进行消息的接收和处理
    private static class MyHandler extends Handler {

        WeakReference<CreatePasswordDialogPresenter> refContext;
        public MyHandler(CreatePasswordDialogPresenter presenter){
            refContext = new WeakReference<CreatePasswordDialogPresenter>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IAccountManager.LOGIN_SUC:
                    refContext.get().iCreatePasswordDialogView.showLoginSuc();
                    break;

                case IAccountManager.REGISTER_SUC:
                    refContext.get().iCreatePasswordDialogView.showRegisterSuc();
                    break;

                case IAccountManager.LOGIN_FAIL:
                    refContext.get().iCreatePasswordDialogView.showError(IAccountManager.LOGIN_FAIL, "");
                    break;

                case IAccountManager.PW_ERR:
                    refContext.get().iCreatePasswordDialogView.showError(IAccountManager.PW_ERR, "");
                    break;

                case IAccountManager.SERVER_FAIL:
                    refContext.get().iCreatePasswordDialogView.showError(IAccountManager.SERVER_FAIL, "");
                    break;
            }
        }
    }
}
