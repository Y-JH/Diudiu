package com.dalimao.mytaxi.splash.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.splash.MyTaxiApplication;
import com.dalimao.mytaxi.splash.account.module.AccountManagerImpl;
import com.dalimao.mytaxi.splash.account.module.IAccountManager;
import com.dalimao.mytaxi.splash.account.presenter.ISmsCodeDialogPresenter;
import com.dalimao.mytaxi.splash.account.presenter.SmsCodeDialogPresenter;
import com.dalimao.mytaxi.splash.common.eventbus.RxBus;
import com.dalimao.mytaxi.splash.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.splash.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.splash.common.util.ToastUtil;


/**
 * @Title:SmsCodeDialog
 * @Package:com.dalimao.mytaxi.splash.account
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2011:13
 */
public class SmsCodeDialog extends Dialog implements ISmsCodeDialogView {
    private static final String TAG = "SmsCodeDialog";
    private String mPhone;
    private Button mResentBtn;
    private VerificationCodeInput mVerificationInput;
    private View mLoading;
    private View mErrorView;
    private TextView mPhoneTv;
    private ISmsCodeDialogPresenter iSmsCodeDialogPresenter;
    /**
     *  验证码倒计时
     */
    private CountDownTimer mCountDownTimer = new CountDownTimer(10000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            mResentBtn.setEnabled(false);
            mResentBtn.setText(String.format(getContext()
                    .getString(R.string.after_time_resend,
                            millisUntilFinished/1000)));
        }

        @Override
        public void onFinish() {
            mResentBtn.setEnabled(true);
            mResentBtn.setText(getContext().getString(R.string.resend));
            cancel();
        }
    };

    public SmsCodeDialog(Context context, String phone) {
        this(context, R.style.Dialog);
        // 上一个界面传来的手机号
        this.mPhone = phone;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_smscode_input, null);
        setContentView(root);
        mPhoneTv = (TextView) findViewById(R.id.phone);
        String template = getContext().getString(R.string.sending);
        mPhoneTv.setText(String.format(template, mPhone));
        mResentBtn = (Button) findViewById(R.id.btn_resend);
        mVerificationInput = (VerificationCodeInput) findViewById(R.id.verificationCodeInput);
        mLoading = findViewById(R.id.loading);
        mErrorView = findViewById(R.id.error);
        mErrorView.setVisibility(View.GONE);
        initListeners();
        requestSendSmsCode();
    }

    /**
     * 请求下发验证码
     */
    private void requestSendSmsCode() {
        iSmsCodeDialogPresenter.requestSendSmsCode(mPhone);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCountDownTimer.cancel();

    }


    public SmsCodeDialog(Context context, int themeResId) {
        super(context, themeResId);
        iSmsCodeDialogPresenter = new SmsCodeDialogPresenter(new AccountManagerImpl(new OkHttpClientImpl(),
                new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT)),
                this);
        RxBus.getInstance().register(iSmsCodeDialogPresenter);//注册 Presenter
    }

    @Override
    public void dismiss() {
        super.dismiss();
        RxBus.getInstance().unRegister(iSmsCodeDialogPresenter);//解注册 Presenter
    }

    protected SmsCodeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    private void initListeners() {

        //  关闭按钮组册监听器
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 重发验证码按钮注册监听器
        mResentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend();
            }
        });

        // 验证码输入完成监听器
        mVerificationInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String code) {

                commit(code);
            }
        });
    }

    /**
     * 提交验证码
     * @param code
     */
    private void commit(final String code) {
        showLoading();
        iSmsCodeDialogPresenter.requestCheckSmsCode(mPhone, code);
    }

    private void resend() {
        String template = getContext().getString(R.string.sending);
        mPhoneTv.setText(String.format(template, mPhone));

    }

    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideLoading() {
        mLoading.setVisibility(View.GONE);
    }


    @Override
    public void showCountDownTimer() {
        mPhoneTv.setText(String.format(getContext().getString(R.string.sms_code_send_phone), mPhone));
        mCountDownTimer.start();
        mResentBtn.setEnabled(false);
    }

    @Override
    public void showError(int code, String msg) {
        hideLoading();

        switch (code){
            case IAccountManager.SMS_SEND_FAIL:
                ToastUtil.show(getContext(), getContext().getString(R.string.sms_send_fail));
                break;

            case IAccountManager.SMS_CHECK_FAIL:
                //提示验证码错误
                mErrorView.setVisibility(View.VISIBLE);
                mVerificationInput.setEnabled(true);
                break;

            case IAccountManager.SMS_SERVER_FAIL:
                ToastUtil.show(getContext(), getContext().getString(R.string.error_server));
                break;
        }
    }

    @Override
    public void showSmsCheckCodeState(boolean bol) {
        if (!bol) {
            //提示验证码错误
            mErrorView.setVisibility(View.VISIBLE);
            mVerificationInput.setEnabled(true);
            hideLoading();
        } else {
            mErrorView.setVisibility(View.GONE);
            showLoading();
            // 检查用户是否存在
            iSmsCodeDialogPresenter.requestCheckUserExitst(mPhone);
        }
    }

    @Override
    public void showUserExit(boolean bol) {
        hideLoading();
        mErrorView.setVisibility(View.GONE);
        dismiss();
        if (!bol) {
            // 用户不存在,进入注册
            CreatePasswordDialog dialog =
                    new CreatePasswordDialog(getContext(), mPhone);
            dialog.show();

        } else {
            // 用户存在 ，进入登录
            LoginDialog dialog = new LoginDialog(getContext(), mPhone);
            dialog.show();

        }
    }

    @Override
    public void close() {

    }
}
