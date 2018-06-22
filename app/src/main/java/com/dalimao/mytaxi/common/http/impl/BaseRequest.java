package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.api.API;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title:BaseRequest
 * @Package:com.dalimao.mytaxi.splash.http.impl
 * @Description:请求的封装具体实现
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1914:32
 */
public class BaseRequest implements IRequest {
    private String method = POST;
    private String url;
    private Map<String, String> header;
    private Map<String, Object> body;

    /**
     * 功能：公共设置的头部信息
     *
     * @param url
     */
    public BaseRequest(String url) {
        this.url = url;
        header = new HashMap<>();
        body = new HashMap<>();
        header.put("X-Bmob-Application-Id", API.Config.getAppId());
        header.put("X-Bmob-REST-API-Key", API.Config.getAppKey());
    }


    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setHeader(String key, String value) {
        this.header.put(key, value);
    }

    @Override
    public void setBody(String key, String value) {
        this.body.put(key, value);
    }

    @Override
    public String getUrl() {
        if (GET.equals(method)) {
            for (String key : this.body.keySet()) {
                url = url.replace("${" + key + "}", this.body.get(key).toString());
            }
        }
        return url;
    }

    @Override
    public Map<String, String> getHeader() {
        return this.header;
    }

    @Override
    public Object getBody() {
        if (body != null) {
            return new Gson().toJson(this.body, HashMap.class);
        } else {
            return "{}";
        }
    }
}
