package com.dalimao.diudiu.go.common.http;

import java.util.Map;

/**
 * @Title:HttpClient
 * @Package:com.dalimao.mytaxi.splash.http
 * @Description:定义请求数据的请求方式
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1914:25
 */
public interface IRequest {
    public static final String POST = "POST";
    public static final String GET = "GET";


    /**
     * 功能：制定请求方式
     * @param method
     */
    void setMethod(String method);

    /**
     * 功能：制定请求头
     * @param key
     * @param value
     */
    void setHeader(String key, String value);

    /**
     * 功能：制定请求体（请求参数）
     * @param key
     * @param value
     */
    void setBody(String key, String value);

    /**
     * 功能：提供请求地址
     * @return
     */
    String getUrl();

    /**
     * 功能：提供请求头
     * @return
     */
    Map<String, String> getHeader();

    /**
     * 功能：提供请求参数
     * @return
     */
    Object getBody();
}
