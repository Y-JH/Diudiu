package com.dalimao.mytaxi.splash.account.module;


import com.dalimao.mytaxi.splash.common.http.biz.BaseBizResponse;

/**
 * @Title:CheckUserResponse
 * @Package:com.dalimao.mytaxi.splash.account.response
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2013:35
 */

public class CheckResponse extends BaseBizResponse {
    Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
