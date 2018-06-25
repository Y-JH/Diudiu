package com.dalimao.mytaxi.account.module;

import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;

/**
 * @Title:OrderStateOptResponse
 * @Package:com.dalimao.mytaxi.account.module
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2514:50
 */
public class OrderStateOptResponse extends BaseBizResponse {
    public final static int ORDER_STATE_CREATE = 0;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
