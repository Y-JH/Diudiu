package com.dalimao.mytaxi.common.eventbus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title:RegisterBus
 * @Package:com.dalimao.mytaxi.splash.common.eventbus
 * @Description:自定义注解，标记观察者方法的注解
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2112:21
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RxbusCallback {
}
