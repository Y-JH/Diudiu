package com.dalimao.mytaxi.splash.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.splash.MyTaxiApplication;
import com.dalimao.mytaxi.splash.account.module.AccountManagerImpl;
import com.dalimao.mytaxi.splash.account.module.IAccountManager;
import com.dalimao.mytaxi.splash.account.presenter.ILoginDialogPresenter;
import com.dalimao.mytaxi.splash.account.presenter.LoginDialogPresenter;
import com.dalimao.mytaxi.splash.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.splash.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.splash.common.util.ToastUtil;


/**
 * @Title:LoginDialog
 * @Package:com.dalimao.mytaxi.splash.account
 * @Description:登录框
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2011:36
 */

public class LoginDialog extends Dialog implements ILoginDialogView{

    private static final String TAG = "LoginDialog";
    private TextView mPhone;
    private EditText mPw;
    private Button mBtnConfirm;
    private View mLoading;
    private TextView mTips;
    private String mPhoneStr;
    private ILoginDialogPresenter iLoginDialogPresenter;

    public LoginDialog(Context context, String phone) {
        this(context, R.style.Dialog);
        mPhoneStr = phone;
        iLoginDialogPresenter = new LoginDialogPresenter(new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT)),
                this);
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_login_input, null);
        setContentView(root);
        initViews();

    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    private void initViews() {
        mPhone = (TextView) findViewById(R.id.phone);
        mPw = (EditText) findViewById(R.id.password);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mLoading = findViewById(R.id.loading);
        mTips = (TextView) findViewById(R.id.tips);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        mPhone.setText(mPhoneStr);

    }

    /**
     * 提交登录
     */
    private void submit() {

        String password = mPw.getText().toString();

        //  网络请求登录
        iLoginDialogPresenter.requestLogin(mPhoneStr, password);
    }


    /**
     * 显示／隐藏 loading
     * @param show
     */

    public void showOrHideLoading(boolean show) {
        if (show) {
            mLoading.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理登录成功 UI
     */
    public void showLoginSuc() {
        mLoading.setVisibility(View.GONE);
        mBtnConfirm.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        mTips.setText(getContext().getString(R.string.login_suc));
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
        dismiss();

    }

    /**
     *  显示服服务器出错
     */

    public void showServerError() {
        hideLoading();
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.error_server));
    }


    /**
     * 密码错误
      */
    public void showPasswordError() {
        hideLoading();
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.password_error));
    }

    @Override
    public void showLoading() {
        showOrHideLoading(true);
    }

    @Override
    public void hideLoading() {
        showOrHideLoading(false);
    }

    @Override
    public void showError(int code, String msg) {
        hideLoading();

        switch (code){
            case IAccountManager.SERVER_FAIL:
                showServerError();
                break;

            case IAccountManager.LOGIN_FAIL:
                ToastUtil.show(getContext(), getContext().getString(R.string.login_fail));
                break;

            case IAccountManager.PW_ERR:
                //提示验证码错误
                showPasswordError();
                break;
        }
    }
}
