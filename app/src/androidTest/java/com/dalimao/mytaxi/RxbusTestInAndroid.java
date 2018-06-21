package com.dalimao.mytaxi;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.dalimao.mytaxi.splash.common.eventbus.IEventBusSubscriber;
import com.dalimao.mytaxi.splash.common.eventbus.RxBus;
import com.dalimao.mytaxi.splash.common.eventbus.RxbusCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.functions.Func1;

/**
 * @Title:RxbusTest
 * @Package:com.dalimao.mytaxi
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2111:09
 */
@RunWith(AndroidJUnit4.class)
public class RxbusTestInAndroid {
    private static final String TAG = "RxbusTestInAndroid";
    Presenter presenter;

    @Before
    public void setUp() {
        Log.e(TAG, "--->setUp--第一个执行");
        presenter = new Presenter(new Manager());
        RxBus.getInstance().register(presenter);
    }

    @Test
    public void sendMessage() {
        Log.e(TAG, "--->Test--第三个执行");
        presenter.sendUsermsg();
    }

    @After
    public void tearDown() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "--->After--第二个执行");
        RxBus.getInstance().unRegister(presenter);
    }


    class Presenter implements IEventBusSubscriber {
        Manager manager;

        public Presenter(Manager manager) {
            this.manager = manager;
        }

        public void sendUsermsg() {
            manager.getUser();
        }


        public void sendOrdermsg() {
            manager.getOrder();
        }

//        @Override
//        public void onEvent(Object event) {
//            if (null != event) {
//                if (event instanceof User) {
//                    User user = (User) event;
//                    Log.e(TAG, Thread.currentThread().getName() + "-->>name=" + user.getName());
//                } else if (event instanceof Order) {
//                    Order order = (Order) event;
//                    Log.e(TAG, Thread.currentThread().getName() + "-->>name=" + order.getOrder());
//                }
//            }
//        }

        @RxbusCallback
        public void getUser(User user){
            Log.e(TAG, Thread.currentThread().getName() + "-->>name=" + user.getName());
        }

        @RxbusCallback
        public void getUser(Order order){
            Log.e(TAG, Thread.currentThread().getName() + "-->>name=" + order.getOrder());
        }

    }


    class Manager {
        public void getUser() {
            RxBus.getInstance().chainProcess("getUser", new Func1<String, User>() {
                @Override
                public User call(String o) {
                    Log.e(TAG, "getUser-chainProcess-->>>" + Thread.currentThread().getName());
                    User user = new User();
                    user.setName(o);
                    return user;
                }
            });
        }

        public void getOrder() {
            RxBus.getInstance().chainProcess("getOrder", new Func1<String, Order>() {
                @Override
                public Order call(String o) {
                    Log.e(TAG, "getOrder-chainProcess-->>>" + Thread.currentThread().getName());
                    Order order = new Order();
                    order.setOrder(o);
                    return order;
                }
            });
        }
    }

    class User {
        String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    class Order {
        String order;

        public void setOrder(String order) {
            this.order = order;
        }

        public String getOrder() {
            return order;
        }
    }
}
