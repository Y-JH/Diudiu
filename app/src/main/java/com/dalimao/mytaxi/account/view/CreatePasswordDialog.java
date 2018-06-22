package com.dalimao.mytaxi.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.module.AccountManagerImpl;
import com.dalimao.mytaxi.account.module.IAccountManager;
import com.dalimao.mytaxi.account.presenter.CreatePasswordDialogPresenter;
import com.dalimao.mytaxi.account.presenter.ICreatePasswordDialogPresenter;
import com.dalimao.mytaxi.common.eventbus.RxBus;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.ToastUtil;


/**
 * @Title:CreatePasswordDialog
 * @Package:com.dalimao.mytaxi.splash.account
 * @Description:密码创建/修改
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2011:33
 */

public class CreatePasswordDialog extends Dialog implements ICreatePasswordDialogView {

    private  static final String TAG = "CreatePasswordDialog";
    private TextView mTitle;
    private TextView mPhone;
    private EditText mPw;
    private EditText mPw1;
    private Button mBtnConfirm;
    private View mLoading;
    private TextView mTips;
    private String mPhoneStr;
    private ICreatePasswordDialogPresenter iCreatePasswordDialogPresenter;

    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public void showError(int code, String msg) {
        hideLoading();
        switch (code){
            case IAccountManager.LOGIN_FAIL:
                ToastUtil.show(getContext(), getContext().getString(R.string.login_fail));
                break;

            case IAccountManager.PW_ERR:
                ToastUtil.show(getContext(), getContext().getString(R.string.password_error));
                break;

            case IAccountManager.SERVER_FAIL:
                mTips.setTextColor(getContext()
                        .getResources().getColor(R.color.error_red));
                mTips.setText(getContext().getString(R.string.error_server));
                break;
        }
    }

    public CreatePasswordDialog(Context context, String phone) {
        this(context, R.style.Dialog);
        // 上一个页面传来的手机号
        mPhoneStr = phone;
        iCreatePasswordDialogPresenter = new CreatePasswordDialogPresenter(new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT)),
                this);

        RxBus.getInstance().register(iCreatePasswordDialogPresenter);//注册 Presenter
    }
    @Override
    public void dismiss() {
        super.dismiss();
        RxBus.getInstance().unRegister(iCreatePasswordDialogPresenter);//解注册 Presenter
    }

    public CreatePasswordDialog(Context context, int theme) {
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
        View root = inflater.inflate(R.layout.dialog_create_pw, null);
        setContentView(root);
        initViews();
    }

    private void initViews() {
        mPhone = (TextView) findViewById(R.id.phone);
        mPw = (EditText) findViewById(R.id.pw);
        mPw1 = (EditText) findViewById(R.id.pw1);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mLoading = findViewById(R.id.loading);
        mTips = (TextView) findViewById(R.id.tips);
        mTitle = (TextView) findViewById(R.id.dialog_title);
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
     * 提交注册
     */
    private void submit() {
        if (checkPassword()) {
            final String password = mPw.getText().toString();
            final String phonePhone = mPhoneStr;
            // 请求网络， 提交注册
            iCreatePasswordDialogPresenter.requestRegister(phonePhone, password);
        }
    }

    /**
     * 检查密码输入
     * @return
     */
    private boolean checkPassword() {
        String password = mPw.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mTips.setVisibility(View.VISIBLE);
            mTips.setText(getContext().getString(R.string.password_is_null));
            mTips.setTextColor(getContext().
                    getResources().getColor(R.color.error_red));
            return false;
        }
        if (!password.equals(mPw1.getText().toString())) {
            mTips.setVisibility(View.VISIBLE);
            mTips.setText(getContext()
                    .getString(R.string.password_is_not_equal));
            mTips.setTextColor(getContext()
                    .getResources().getColor(R.color.error_red));
            return false;
        }
        return true;
    }




    /**
     *  处理注册成功
     */
    public void showRegisterSuc() {
        showLoading();
        mBtnConfirm.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext()
                .getResources()
                .getColor(R.color.color_text_normal));
        mTips.setText(getContext()
                .getString(R.string.register_suc_and_loging));
        // 请求网络，完成自动登录
        String password = mPw.getText().toString();
        iCreatePasswordDialogPresenter.requestLogin(mPhoneStr, password);
    }

    public void showLoginSuc() {
        dismiss();
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
    }

    @Override
    public void checkPw(String pw, String pwr) {
        this.checkPassword();
    }

}
