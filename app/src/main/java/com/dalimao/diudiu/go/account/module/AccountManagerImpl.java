package com.dalimao.diudiu.go.account.module;

import android.os.Handler;
import android.util.Log;

import com.dalimao.diudiu.go.common.eventbus.RxBus;
import com.dalimao.diudiu.go.common.http.IRequest;
import com.dalimao.diudiu.go.common.http.biz.BaseBizResponse;
import com.dalimao.diudiu.go.common.storage.SharedPreferencesDao;
import com.dalimao.diudiu.go.MyTaxiApplication;
import com.dalimao.diudiu.go.common.http.IHttpClient;
import com.dalimao.diudiu.go.common.http.IResponse;
import com.dalimao.diudiu.go.common.http.api.API;
import com.dalimao.diudiu.go.common.http.impl.BaseRequest;
import com.dalimao.diudiu.go.common.http.impl.BaseResponse;
import com.dalimao.diudiu.go.common.util.DevUtil;
import com.dalimao.diudiu.go.lbs.LocationInfo;
import com.google.gson.Gson;

import rx.functions.Func1;

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

    /**
     * 功能：检测验证码是否发送成功
     *
     * @param phone
     */
    @Override
    public void fetchSMSCode(final String phone) {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {

                String url = API.Config.getDomain() + API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = mHttpClient.get(request, false);
                String res = response.getData();
                Log.d(TAG, res);

                CheckResponse checkResponse = new CheckResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    checkResponse =
                            new Gson().fromJson(response.getData(), CheckResponse.class);
                    if (checkResponse.getCode() == BaseBizResponse.STATE_OK) {
                        checkResponse.setCode(SMS_SEND_SUC);
                    } else {
                        checkResponse.setCode(SMS_SEND_FAIL);
                    }
                } else {
                    checkResponse.setCode(SMS_SEND_FAIL);
                }

                return checkResponse;
            }
        });
    }

    /**
     * 功能：检测网络请求校验验证码
     *
     * @param phone
     * @param smsCode
     */
    @Override
    public void checkSnmCode(final String phone, final String smsCode) {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("code", smsCode);
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, response.getData());

                CheckResponse checkResponse = new CheckResponse();

                if (response.getCode() == BaseResponse.STATE_OK) {
                    checkResponse =
                            new Gson().fromJson(response.getData(), CheckResponse.class);
                    if (checkResponse.getCode() == BaseBizResponse.STATE_OK) {
                        checkResponse.setCode(SMS_CHECK_SUC);
                    } else {
                        checkResponse.setCode(SMS_CHECK_FAIL);
                    }
                } else {
                    checkResponse.setCode(SMS_CHECK_FAIL);
                }

                return checkResponse;
            }
        });
    }

    /**
     * 功能：检测检查用户是否存在
     *
     * @param phone
     */
    @Override
    public void checkUserExist(final String phone) {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.CHECK_USER_EXIST;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, response.getData());

                CheckResponse checkResponse = new CheckResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    checkResponse =
                            new Gson().fromJson(response.getData(), CheckResponse.class);
                    if (checkResponse.getCode() == BaseBizResponse.STATE_USER_EXIST) {
                        checkResponse.setCode(USER_EXIST);
                    } else if (checkResponse.getCode() == BaseBizResponse.STATE_USER_NOT_EXIST) {
                        checkResponse.setCode(USER_NOT_EXIST);
                    }
                } else {
                    checkResponse.setCode(SMS_SERVER_FAIL);
                }


                return checkResponse;
            }
        });
    }

    /**
     * 功能：请求网络， 提交注册
     *
     * @param phone
     * @param password
     */
    @Override
    public void register(final String phone, final String password) {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                request.setBody("uid", DevUtil.UUID(MyTaxiApplication.getInstance()));

                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());

                RegisterResponse registerResponse = new RegisterResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    registerResponse =
                            new Gson().fromJson(response.getData(), RegisterResponse.class);
                    if (registerResponse.getCode() == BaseBizResponse.STATE_OK) {
                        registerResponse.setCode(REGISTER_SUC);
                    } else {
                        registerResponse.setCode(SERVER_FAIL);
                    }
                } else {
                    registerResponse.setCode(SERVER_FAIL);
                }

                return registerResponse;
            }
        });
    }

    /**
     * 功能：直接登录-网络登录模块
     *
     * @param phone
     * @param password
     */
    @Override
    public void login(final String phone, final String password) {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());

                LoginResponse loginResponse = new LoginResponse();

                if (response.getCode() == BaseResponse.STATE_OK) {
                    loginResponse = new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (loginResponse.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        Account account = loginResponse.getData();
                        // todo: 加密存储
                        SharedPreferencesDao dao =
                                new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                                        SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                        loginResponse.setCode(LOGIN_SUC);
                    } else if (loginResponse.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        loginResponse.setCode(PW_ERR);
                    } else {
                        loginResponse.setCode(SERVER_FAIL);
                    }
                } else {
                    loginResponse.setCode(SERVER_FAIL);
                }


                return loginResponse;
            }
        });
    }


    /**
     * 功能：注册之后请求的登录方法
     *
     * @param phone
     * @param password
     */
    @Override
    public void registerToLogin(final String phone, final String password) {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", phone);
                request.setBody("password", password);
                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());

                RegisterResponse registerResponse = new RegisterResponse();

                if (response.getCode() == BaseResponse.STATE_OK) {
                    registerResponse = new Gson().fromJson(response.getData(), RegisterResponse.class);
                    if (registerResponse.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        Account account = registerResponse.getData();
                        // todo: 加密存储
                        SharedPreferencesDao dao =
                                new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                                        SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                        registerResponse.setCode(LOGIN_SUC);
                    } else if (registerResponse.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        registerResponse.setCode(PW_ERR);
                    } else {
                        registerResponse.setCode(SERVER_FAIL);
                    }
                } else {
                    registerResponse.setCode(SERVER_FAIL);
                }


                return registerResponse;
            }
        });
    }

    @Override
    public void loginByToken() {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {
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

                LoginResponse loginResponse = new LoginResponse();

                if (!tokenValid) {
                    loginResponse.setCode(TOKEN_INVALID);
                } else {
                    // 请求网络，完成自动登录
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.getToken());
                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData());

                    if (response.getCode() == BaseResponse.STATE_OK) {
                        loginResponse =
                                new Gson().fromJson(response.getData(), LoginResponse.class);
                        if (loginResponse.getCode() == BaseBizResponse.STATE_OK) {
                            // 保存登录信息
                            Account account2 = loginResponse.getData();
                            // todo: 加密存储
                            SharedPreferencesDao dao2 =
                                    new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                                            SharedPreferencesDao.FILE_ACCOUNT);
                            dao2.save(SharedPreferencesDao.KEY_ACCOUNT, account2);
                            // 通知 UI
                            loginResponse.setCode(LOGIN_SUC);
                        }
                        if (loginResponse.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {
                            loginResponse.setCode(TOKEN_INVALID);
                        }
                    } else {
                        loginResponse.setCode(LOGIN_FAIL);
                    }
                }


                return loginResponse;
            }
        });


    }

    /**
     * 功能：获取附近的司机朋友
     *
     * @param locationInfo
     */
    @Override
    public void fetchNearDrivers(final LocationInfo locationInfo) {
        RxBus.getInstance().chainProcess("", new Func1() {
            @Override
            public Object call(Object o) {

                String url = API.Config.getDomain() + API.GET_NEAR_DRIVERS;
                IRequest request = new BaseRequest(url);
                request.setBody("latitude", String.valueOf(locationInfo.getLatitude()));
                request.setBody("longitude", String.valueOf(locationInfo.getLongitude()));
                IResponse response = mHttpClient.get(request, false);
                Log.d(TAG, "drivers--"+response.getData());

                NearDriverResponse nearDriverResponse = new NearDriverResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    nearDriverResponse = new Gson().fromJson(response.getData(), NearDriverResponse.class);

                }
                return nearDriverResponse;
            }
        });
    }

}
