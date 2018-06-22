package com.dalimao.mytaxi.account.module;


import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;

/**
 * @Title:LoginResponse
 * @Package:com.dalimao.mytaxi.splash.account.response
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2013:35
 */

public class LoginResponse extends BaseBizResponse {
    Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
