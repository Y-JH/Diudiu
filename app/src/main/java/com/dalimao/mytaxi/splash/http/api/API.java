package com.dalimao.mytaxi.splash.http.api;

/**
 * @Title:API
 * @Package:com.dalimao.mytaxi.splash.http.api
 * @Description:API的约定
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1914:43
 */
public final class API {
    public static final String TEST_GET = "/get?uid=${uid}";
    public static final String TEST_POST = "/post";

    /**
     * 功能：用来配置域名信息
     */
    public static class Config{
        public static final String TEST_DOMAIN = "http://httpbin.org";//测试环境的域名
        public static final String REPLASE_DOMAIN = "http://httpbin.org";//发布环境的域名
        private static String domain = TEST_DOMAIN;


        public static void setDebug(boolean debug){
            domain = debug ? TEST_DOMAIN : REPLASE_DOMAIN;

        }

        public static String getDomain(){
            return domain;
        }
    }
}
