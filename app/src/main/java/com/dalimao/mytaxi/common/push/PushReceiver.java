package com.dalimao.mytaxi.common.push;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dalimao.mytaxi.account.module.OrderStateOptResponse;
import com.dalimao.mytaxi.common.eventbus.RxBus;
import com.dalimao.mytaxi.lbs.LocationInfo;
import com.dalimao.mytaxi.lbs.Order;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by yuanjunhua on 2018/6/22.
 */

public class PushReceiver extends BroadcastReceiver {
    private static final int MSG_TYPE_LOCATION = 1;//位置变化
    private static final int MSG_TYPE_ORDER = 2;//订单状态变化
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            String msg = intent.getStringExtra("msg");
            Log.e("bmob", "客户端收到推送内容：" + msg);
            try {
                JSONObject jsonObject = new JSONObject(msg);
                int type = jsonObject.optInt("type");
                if (type == MSG_TYPE_LOCATION) {
                    // 位置变化
                    LocationInfo locationInfo =
                            new Gson().fromJson(jsonObject.optString("data"), LocationInfo.class);
                    RxBus.getInstance().send(locationInfo);

                } else if(type == MSG_TYPE_ORDER){
                    Order order = new Gson().fromJson(jsonObject.optString("data"), Order.class);

                    OrderStateOptResponse response = new OrderStateOptResponse();
                    response.setState(OrderStateOptResponse.ORDER_STATE_ACCEPT);
                    response.setData(order);
                    response.setCode(response.STATE_OK);

                    RxBus.getInstance().send(response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
