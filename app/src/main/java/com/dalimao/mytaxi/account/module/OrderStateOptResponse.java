package com.dalimao.mytaxi.account.module;

import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.lbs.Order;

/**
 * @Title:OrderStateOptResponse
 * @Package:com.dalimao.mytaxi.account.module
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2514:50
 */
public class OrderStateOptResponse extends BaseBizResponse {
    //创建订单
    public final static int ORDER_STATE_CREATE = 0;
    //取消订单
    public final static int ORDER_STATE_CANCEL = -1;
    //司机接单
    public final static int ORDER_STATE_ACCEPT = 1;
    //   司机到达
    public static final int ORDER_STATE_ARRIVE_START = 2;
    //  司机开始行程
    public static final int ORDER_STATE_START_DRIVE = 3;
    //  到达目的地
    public static final int ORDER_STATE_ARRIVE_END = 4;
    private int state;

    private Order data;

    public void setData(Order data){
        this.data = data;
    }
    public Order getData(){
        return data;
    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
