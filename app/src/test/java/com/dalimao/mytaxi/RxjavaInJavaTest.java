package com.dalimao.mytaxi;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Title:RxjavaTestInAndroid
 * @Package:com.dalimao.mytaxi
 * @Description:专注测试Android程序
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/219:13
 */
public class RxjavaInJavaTest {

    @Before
    public void setUp() throws Exception {
        Thread.currentThread().setName("currentThread");
    }


    @Test
    public void schedulerTest() {
        //观察者（订阅者）
        final Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

                System.out.println("onCompleted=" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError=" + Thread.currentThread().getName());
                e.printStackTrace();
            }

            @Override
            public void onNext(String result) {
                System.out.println("onNext=" + Thread.currentThread().getName());
                System.out.println("onNext=" + result);

            }
        };

        //被观察者
        final Observable observable = Observable.create(new Observable.OnSubscribe<Subscriber>() {

            @Override
            public void call(Subscriber subscriber1) {

                System.out.println("Observable-call=" + Thread.currentThread().getName());
                subscriber1.onStart();
                subscriber1.onNext("hello world");
                subscriber1.onCompleted();
            }
        });


        observable.subscribeOn(Schedulers.io()) //指生产事件在当前的线程中进行
                .observeOn(AndroidSchedulers.mainThread()) //指消费事件在新的线程中进行
                .subscribe(subscriber);


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
