package com.dalimao.mytaxi.splash.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.splash.account.module.IAccountManager;
import com.dalimao.mytaxi.splash.main.IMainActivityView;

import java.lang.ref.WeakReference;

/**
 * @Title:MainActivity
 * @Package:com.dalimao.mytaxi.splash.account.presenter
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2017:20
 */
public class MainActivityPresenter implements IMainActivityPresenter {

    IAccountManager iAccountManager;
    IMainActivityView iMainActivityView;

    public MainActivityPresenter(IAccountManager iAccountManager, IMainActivityView iMainActivityView){
        this.iAccountManager = iAccountManager;
        this.iMainActivityView = iMainActivityView;
        iAccountManager.setHandler(new MyHandler(this));
    }
    @Override
    public void checkLoginState() {
        iAccountManager.loginByToken();
    }



    //在这里进行消息的接收和处理
    private static class MyHandler extends Handler {

        WeakReference<MainActivityPresenter> refContext;
        public MyHandler(MainActivityPresenter presenter){
            refContext = new WeakReference<MainActivityPresenter>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IAccountManager.LOGIN_SUC:
                    refContext.get().iMainActivityView.showLoginSucc();
                    break;

                case IAccountManager.TOKEN_INVALID:
                    refContext.get().iMainActivityView.showError(IAccountManager.TOKEN_INVALID, "");
                    break;

                case IAccountManager.LOGIN_FAIL:
                    refContext.get().iMainActivityView.showError(IAccountManager.LOGIN_FAIL, "");
                    break;
            }
        }
    }
}
