package com.dalimao.mytaxi.account.presenter;

import com.dalimao.mytaxi.account.module.IAccountManager;
import com.dalimao.mytaxi.account.module.LoginResponse;
import com.dalimao.mytaxi.account.view.ILoginDialogView;
import com.dalimao.mytaxi.common.eventbus.RxbusCallback;

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
    }
    @Override
    public void requestLogin(String phone, String pw) {
        iAccountManager.login(phone, pw);
    }


    @RxbusCallback
    public void loginCallback(LoginResponse loginResponse) {
        if (null != loginResponse) {
            switch (loginResponse.getCode()){
                case IAccountManager.LOGIN_SUC:
                    iLoginDialogView.showLoginSuc();
                    break;

                case IAccountManager.LOGIN_FAIL:
                    iLoginDialogView.showError(IAccountManager.LOGIN_FAIL, "");
                    break;

                case IAccountManager.PW_ERR:
                    iLoginDialogView.showError(IAccountManager.PW_ERR, "");
                    break;

                case IAccountManager.SERVER_FAIL:
                    iLoginDialogView.showError(IAccountManager.SERVER_FAIL, "");
                    break;
            }
        }

    }

}
