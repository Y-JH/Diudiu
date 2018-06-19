package com.dalimao.mytaxi.splash.http.impl;

import com.dalimao.mytaxi.splash.http.IHttpClient;
import com.dalimao.mytaxi.splash.http.IRequest;
import com.dalimao.mytaxi.splash.http.IResponse;
import com.dalimao.mytaxi.splash.http.api.API;

import org.junit.Before;
import org.junit.Test;

/**
 * @Title:OkHttpClientImplTest
 * @Package:com.dalimao.mytaxi.splash.http.impl
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1915:22
 */
public class OkHttpClientImplTest {

    IHttpClient httpClient;

    /**
     * 功能：在testRun方法执行之前优先执行
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        httpClient = new OkHttpClientImpl();
        API.Config.setDebug(false);//设置为发布环境的域名测试
    }

    @Test
    public void get() throws Exception {

        IRequest request = new BaseRequest(API.Config.getDomain()+API.TEST_GET);

        request.setBody("uid", "123456");
        request.setHeader("testHeader", "test header");
        IResponse response = httpClient.get(request, false);
        System.out.print("执行测试getCode方法>>"+response.getCode());
        System.out.print("执行测试getData方法>>"+response.getData().toString());

    }

    @Test
    public void post() throws Exception {
        IRequest request = new BaseRequest(API.Config.getDomain()+API.TEST_POST);

        request.setBody("uid", "123456");
        request.setHeader("testHeader", "test header");
        IResponse response = httpClient.post(request, false);
        System.out.print("执行测试getCode方法>>"+response.getCode());
        System.out.print("执行测试getData方法>>"+response.getData().toString());
    }

}