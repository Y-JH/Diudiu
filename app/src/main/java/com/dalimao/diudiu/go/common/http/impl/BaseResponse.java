package com.dalimao.diudiu.go.common.http.impl;

import com.dalimao.diudiu.go.common.http.IResponse;

/**
 * @Title:BaseResponse
 * @Package:com.dalimao.mytaxi.splash.http.impl
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1915:06
 */
public class BaseResponse implements IResponse {

    public static final int STATE_UNKNOWN_ERROR = 10001;
    public static final int STATE_OK = 200;
    private int code;
    private String data;

    @Override
    public String getData() {


        return this.data;

    }

    @Override
    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public void setData(String data) {
        this.data = data;
    }
}
