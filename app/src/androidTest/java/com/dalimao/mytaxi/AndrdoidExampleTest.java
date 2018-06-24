package com.dalimao.mytaxi;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yuanjunhua on 2018/6/24.
 */

@RunWith(AndroidJUnit4.class)
public class AndrdoidExampleTest {

    @Before
    public void setUp() throws Exception {
        Thread.currentThread().setName("currentThread");
    }


    @Test
    public void schedulerTest() {
        final String tag = "test";
        //观察者（订阅者）
        final Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

                System.out.println("onCompleted=" + Thread.currentThread().getName());
                Log.e(tag, "onCompleted=" + Thread.currentThread().getName());
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
                Log.e(tag, "onNext=" + result);

            }
        };

        //被观察者
        final Observable observable = Observable.create(new Observable.OnSubscribe<Subscriber>() {

            @Override
            public void call(Subscriber subscriber1) {

                System.out.println("Observable-call=" + Thread.currentThread().getName());
                Log.e(tag, "Observable-call=" + Thread.currentThread().getName());
                subscriber1.onStart();
                subscriber1.onNext("hello world");
                Log.e(tag, "hello world");
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
