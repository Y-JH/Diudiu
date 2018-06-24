package com.dalimao.mytaxi;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Title:RxjavaTestInAndroid
 * @Package:com.dalimao.mytaxi
 * @Description:专注测试Android程序
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/219:13
 */

@RunWith(AndroidJUnit4.class)
public class RxjavaTestInAndroid {

    @Before
    public void setUp() throws Exception {
        Thread.currentThread().setName("currentThread");
    }


    @Test
    public void mapTest() {
        final String TAG = "mapTest";
        final String name = "ztman-for-army";
        Observable.just(name)
                .subscribeOn(Schedulers.newThread())//指定下一个生产节点在新的线程中处理  并且在下一个节点的输入，就是这个节点的输出
                .map(new Func1<String, User>() {
                    @Override
                    public User call(String result) {

                        User user = new User();
                        user.setName(result);
                        Log.e(TAG, "生产>1map-call=" + Thread.currentThread().getName());
                        return user;
                    }
                })

                .subscribeOn(Schedulers.newThread())//指定下一个生产节点在新的线程中处理 并且在下一个节点的输入，就是这个节点的输出
                .map(new Func1<User, User>() {
                    @Override
                    public User call(User user) {
                        Log.e(TAG, "生产>2map-call=" + Thread.currentThread().getName());
                        //如果需要，可以在这里对user进行加工处理
                        return user;
                    }
                })

                //指定消费节点是在主线程中执行 gradle已经引入了Rxandroid,即加入了android的api。
                //但是在这里是执行不了的，因为这是对java的测试
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {

                        Log.e(TAG, "消费>observeOn-call=" + Thread.currentThread().getName());
                        Log.e(TAG, "消费>observeOn-call-User=" + user);
                    }
                });

    }

    public class User {
        private String name;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

}
