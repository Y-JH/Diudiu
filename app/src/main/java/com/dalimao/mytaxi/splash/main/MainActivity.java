package com.dalimao.mytaxi.splash.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.splash.MyTaxiApplication;
import com.dalimao.mytaxi.splash.account.module.AccountManagerImpl;
import com.dalimao.mytaxi.splash.account.module.IAccountManager;
import com.dalimao.mytaxi.splash.account.presenter.IMainActivityPresenter;
import com.dalimao.mytaxi.splash.account.presenter.MainActivityPresenter;
import com.dalimao.mytaxi.splash.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.splash.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.splash.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.splash.common.util.ToastUtil;

/**
 *
 * 1 检查本地纪录(登录态检查)
 * 2 若用户没登录则登录
 * 3 登录之前先校验手机号码
 * 4 token 有效使用 token 自动登录
 * todo : 地图初始化
 */

public class MainActivity extends AppCompatActivity implements IMainActivityView{
    private final static String TAG = "MainActivityPresenter";
    private IMainActivityPresenter iMainActivityPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iMainActivityPresenter = new MainActivityPresenter(new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT)),
                this);

        //检查用户是否登录
        iMainActivityPresenter.checkLoginState();
    }
    /**
     * 显示手机输入框
     */
    @Override
    public void showPhoneInputDialog() {
        PhoneInputDialog dialog = new PhoneInputDialog(this);
        dialog.show();
    }

    @Override
    public void showError(int code, String msg) {
        switch (code){
            case IAccountManager.TOKEN_INVALID:
                showPhoneInputDialog();
                break;

            case IAccountManager.LOGIN_FAIL:
                ToastUtil.show(MainActivity.this,
                        getString(R.string.error_server));
                break;
        }
    }

    @Override
    public void showLoginSucc() {
        ToastUtil.show(MainActivity.this,
                getString(R.string.login_suc));
    }

}
