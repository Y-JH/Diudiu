package com.dalimao.mytaxi.splash.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.splash.account.module.IAccountManager;
import com.dalimao.mytaxi.splash.account.view.ILoginDialogView;

import java.lang.ref.WeakReference;

/**
 * @Title:LoginDialogPresenter
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2015:25
 */
public class LoginDialogPresenter implements ILoginDialogPresenter {
    private IAccountManager iAccountManager;
    private ILoginDialogView iLoginDialogView;

    public LoginDialogPresenter(IAccountManager iAccountManager,
                                ILoginDialogView iLoginDialogView){
        this.iAccountManager = iAccountManager;
        this.iLoginDialogView = iLoginDialogView;
        iAccountManager.setHandler(new MyHandler(this));
    }
    @Override
    public void requestLogin(String phone, String pw) {
        iAccountManager.login(phone, pw);
    }



    //在这里进行消息的接收和处理
    private static class MyHandler extends Handler {

        WeakReference<LoginDialogPresenter> refContext;
        public MyHandler(LoginDialogPresenter presenter){
            refContext = new WeakReference<LoginDialogPresenter>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IAccountManager.LOGIN_SUC:
                    refContext.get().iLoginDialogView.showLoginSuc();
                    break;

                case IAccountManager.LOGIN_FAIL:
                    refContext.get().iLoginDialogView.showError(IAccountManager.LOGIN_FAIL, "");
                    break;

                case IAccountManager.PW_ERR:
                    refContext.get().iLoginDialogView.showError(IAccountManager.PW_ERR, "");
                    break;

                case IAccountManager.SERVER_FAIL:
                    refContext.get().iLoginDialogView.showError(IAccountManager.SERVER_FAIL, "");
                    break;
            }
        }
    }
}
