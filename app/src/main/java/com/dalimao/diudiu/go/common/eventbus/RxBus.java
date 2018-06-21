package com.dalimao.diudiu.go.common.eventbus;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Title:RxBus
 * @Package:com.dalimao.mytaxi.splash.common.eventbus
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2110:53
 */
public class RxBus {
    private static final String TAG = "RxBus";

    private static volatile RxBus instance;
    //订阅者<观察者>集合
    private Set<IEventBusSubscriber> subscribers;

    /**
     * 功能：注册 IEventBusSubscriber
     *
     * @param subscriber
     */
    public synchronized void register(IEventBusSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * 功能解除注册 IEventBusSubscriber
     *
     * @param subscriber
     */
    public synchronized void unRegister(IEventBusSubscriber subscriber) {
        if (null != subscriber && subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
        }
    }


    private RxBus() {
        subscribers = new CopyOnWriteArraySet<>();
    }

    /**
     * 功能：使用单列模式实现rxbus
     * @return
     */
    public static synchronized RxBus getInstance() {
        if (null == instance) {
            synchronized (RxBus.class) {
                if (null == instance) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }


    /**
     * 功能：包装处理后发送通知
     * @param func1 对数据进行封装处理的方法
     */
    public void chainProcess(String msg, Func1 func1){
        Observable.just(msg)
                .subscribeOn(Schedulers.io())//指定数据包装在IO线程
                .map(func1)
                .observeOn(AndroidSchedulers.mainThread())//指定消费在主线程
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        Log.e(TAG, "chainProcess is starting ...");
                        for(IEventBusSubscriber subscriber : subscribers){
                            //通知：将数据发送到已注册的subscriber
//                            subscriber.onEvent(o);

                            //扫描注解，将数据发送到使用注解的方法中去
                            callMethodByAnnotation(subscriber, o);
                        }

                    }
                });
    }


    /**
     * 功能：通过反射获取对象方法的列表，判断
     * 1，是否被注解修饰
     * 2，参数类型是否和data类型一致
     * @param target
     * @param data
     */
    private void callMethodByAnnotation(IEventBusSubscriber target, final Object data) {
        Method[] methods = target.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            try{
                if(methods[i].isAnnotationPresent(RxbusCallback.class)){
                    //如果是被 RxbusCallback 修饰过的方法
                    Class paramType = methods[i].getParameterTypes()[0];

                    if(null != data && data.getClass().getName().equals(paramType.getName())){
                        //已加注解方法参数的类型若与data的一致，
                        methods[i].invoke(target, new Object[]{data});
                    }
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }catch (InvocationTargetException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
