package com.dalimao.diudiu.go.account.presenter;

import com.dalimao.diudiu.go.account.module.IAccountManager;
import com.dalimao.diudiu.go.common.eventbus.RxbusCallback;
import com.dalimao.diudiu.go.account.module.RegisterResponse;
import com.dalimao.diudiu.go.account.view.ICreatePasswordDialogView;

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
        iAccountManager.registerToLogin(phone, pw);
    }


    @RxbusCallback
    public void registerCallback(RegisterResponse registerResponse){
        if(null != registerResponse){
            switch (registerResponse.getCode()){
                case IAccountManager.LOGIN_SUC:
                    iCreatePasswordDialogView.showLoginSuc();
                    break;

                case IAccountManager.REGISTER_SUC:
                    iCreatePasswordDialogView.showRegisterSuc();
                    break;

                case IAccountManager.LOGIN_FAIL:
                    iCreatePasswordDialogView.showError(IAccountManager.LOGIN_FAIL, "");
                    break;

                case IAccountManager.PW_ERR:
                    iCreatePasswordDialogView.showError(IAccountManager.PW_ERR, "");
                    break;

                case IAccountManager.SERVER_FAIL:
                    iCreatePasswordDialogView.showError(IAccountManager.SERVER_FAIL, "");
                    break;
            }
        }
    }

}
