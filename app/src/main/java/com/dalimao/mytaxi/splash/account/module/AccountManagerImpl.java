package com.dalimao.mytaxi.splash.account.module;

import android.os.Handler;
import android.util.Log;

import com.dalimao.mytaxi.splash.MyTaxiApplication;
import com.dalimao.mytaxi.splash.common.http.IHttpClient;
import com.dalimao.mytaxi.splash.common.http.IRequest;
import com.dalimao.mytaxi.splash.common.http.IResponse;
import com.dalimao.mytaxi.splash.common.http.api.API;
import com.dalimao.mytaxi.splash.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.splash.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.splash.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.splash.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.splash.common.util.DevUtil;
import com.google.gson.Gson;

/**
 * @Title:AccountManagerImpl
 * @Package:com.dalimao.mytaxi.splash.account.module
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2014:56
 */
public class AccountManagerImpl implements IAccountManager {
    private static final String TAG = "AccountManagerImpl";

    Handler mHandler;
    private IHttpClient mHttpClient;
    private SharedPreferencesDao dao;

    public AccountManagerImpl(IHttpClient httpClient, SharedPreferencesDao dao) {
        this.mHttpClient = httpClient;
        this.dao = dao;
    }

    @Override
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void fetchSMSCode(final String phone) {
        new Thread() {
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = mHttpClient.get(request, false);
                String res = response.getData();
                Log.d(TAG, res);
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        mHandler.sendEmptyMessage(SMS_SEND_SUC);
                    } else {
                        mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                }

            }
        }.start();
    }

    @Override
    public void checkSnmCode(final String phone, final String smsCode) {
        // 网络请求校验验证码
        new Thread() {
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("code", smsCode);
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        mHandler.sendEmptyMessage(SMS_CHECK_SUC);
                    } else {
                        mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                }

            }
        }.start();
    }

    @Override
    public void checkUserExist(final String phone) {
        // 检查用户是否存在
        new Thread() {
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.CHECK_USER_EXIST;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST) {
                        mHandler.sendEmptyMessage(USER_EXIST);
                    } else if (bizRes.getCode() == BaseBizResponse.STATE_USER_NOT_EXIST) {
                        mHandler.sendEmptyMessage(USER_NOT_EXIST);
                    }
                } else {
                    mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                }

            }
        }.start();
    }

    @Override
    public void register(final String phone, final String password) {
        // 请求网络， 提交注册
        new Thread() {
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                request.setBody("uid", DevUtil.UUID(MyTaxiApplication.getInstance()));

                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson().fromJson(response.getData(), BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        mHandler.sendEmptyMessage(REGISTER_SUC);
                    } else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }

            }
        }.start();
    }

    @Override
    public void login(final String phone, final String password) {
        //  网络请求登录
        new Thread() {
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);


                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    LoginResponse bizRes =
                            new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        Account account = bizRes.getData();
                        // todo: 加密存储
                        SharedPreferencesDao dao =
                                new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                                        SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                        // 通知 UI
                        mHandler.sendEmptyMessage(LOGIN_SUC);
                    }
                    if (bizRes.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        mHandler.sendEmptyMessage(PW_ERR);
                    } else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }

            }
        }.start();
    }

    @Override
    public void loginByToken() {
        // 获取本地登录信息
        SharedPreferencesDao dao =
                new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                        SharedPreferencesDao.FILE_ACCOUNT);
        final Account account =
                (Account) dao.get(SharedPreferencesDao.KEY_ACCOUNT, Account.class);
        // 登录是否过期
        boolean tokenValid = false;

        // 检查token是否过期

        if (account != null) {
            if (account.getExpired() > System.currentTimeMillis()) {
                // token 有效
                tokenValid = true;
            }
        }


        if (!tokenValid) {
            mHandler.sendEmptyMessage(TOKEN_INVALID);
        } else {
            // 请求网络，完成自动登录
            new Thread() {
                @Override
                public void run() {
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.getToken());
                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData());
                    if (response.getCode() == BaseResponse.STATE_OK) {
                        LoginResponse bizRes =
                                new Gson().fromJson(response.getData(), LoginResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                            // 保存登录信息
                            Account account = bizRes.getData();
                            // todo: 加密存储
                            SharedPreferencesDao dao =
                                    new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                                            SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);
                            // 通知 UI
                            mHandler.sendEmptyMessage(LOGIN_SUC);
                        }
                        if (bizRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {
                            mHandler.sendEmptyMessage(TOKEN_INVALID);
                        }
                    } else {
                        mHandler.sendEmptyMessage(LOGIN_FAIL);
                    }

                }
            }.start();
        }

    }
}
