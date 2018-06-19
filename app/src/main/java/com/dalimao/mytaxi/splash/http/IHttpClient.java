package com.dalimao.mytaxi.splash.http;

/**
 * @Title:HttpClient
 * @Package:com.dalimao.mytaxi.splash.http
 * @Description:定义抽象接口
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1914:25
 */
public interface IHttpClient {
    IResponse get(IRequest request, boolean forceCache);
    IResponse post(IRequest request, boolean forceCache);
}
