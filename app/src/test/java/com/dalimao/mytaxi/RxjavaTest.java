package com.dalimao.mytaxi;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Title:RxjavaTest
 * @Package:com.dalimao.mytaxi
 * @Description:JUnit测试，使用的启动器，是JUnit；专注测试java程序
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/219:13
 */
public class RxjavaTest {

    @Before
    public void setUp() throws Exception {
        Thread.currentThread().setName("currentThread");
    }


    @Test
    public void testSubscribe() {
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
//                subscriber1.onError(new Exception("error"));
                subscriber1.onNext("hello world");
                subscriber1.onCompleted();
            }
        });


        observable.subscribe(subscriber);


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
//                subscriber1.onError(new Exception("error"));
                subscriber1.onNext("hello world");
                subscriber1.onCompleted();
            }
        });


        observable.subscribeOn(Schedulers.io()) //指生产事件在当前的线程中进行
                .observeOn(Schedulers.newThread()) //指消费事件在新的线程中进行
                .subscribe(subscriber);

    }


    @Test
    public void mapTest() {
        final String name = "ztman-for-army";
        Observable.just(name)
                .subscribeOn(Schedulers.newThread())//指定下一个生产节点在新的线程中处理  并且在下一个节点的输入，就是这个节点的输出
                .map(new Func1<String, User>() {
                    @Override
                    public User call(String result) {

                        User user = new User();
                        user.setName(result);
                        System.out.println("生产>1map-call=" + Thread.currentThread().getName());

                        return user;
                    }
                })

                .subscribeOn(Schedulers.newThread())//指定下一个生产节点在新的线程中处理 并且在下一个节点的输入，就是这个节点的输出
                .map(new Func1<User, User>() {
                    @Override
                    public User call(User user) {
                        System.out.println("生产>2map-call=" + Thread.currentThread().getName());
                        //如果需要，可以在这里对user进行加工处理
                        return user;
                    }
                })

                .observeOn(Schedulers.newThread())//指定消费节点是在新的线程中执行
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {

                        System.out.println("消费>observeOn-call=" + Thread.currentThread().getName());
                        System.out.println("消费>observeOn-call-User=" + user);
                    }
                });

                //指定消费节点是在主线程中执行 gradle已经引入了Rxandroid,即加入了android的api。
                //但是在这里是执行不了的，因为这是对java的测试
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<User>() {
//                    @Override
//                    public void call(User user) {
//
//                        System.out.println("消费>observeOn-call=" + Thread.currentThread().getName());
//                        System.out.println("消费>observeOn-call-User=" + user);
//                    }
//                });

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
