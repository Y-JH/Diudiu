package com.dalimao.mytaxi.splash.http.impl;

import com.dalimao.mytaxi.splash.http.IHttpClient;
import com.dalimao.mytaxi.splash.http.IRequest;
import com.dalimao.mytaxi.splash.http.IResponse;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Title:OkHttpClientImpl
 * @Package:com.dalimao.mytaxi.splash.http.impl
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1914:58
 */
public class OkHttpClientImpl implements IHttpClient {

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();

    @Override
    public IResponse get(IRequest request, boolean forceCache) {
        request.setMethod(IRequest.GET);
        Map<String, String> header = request.getHeader();
        Request.Builder builder = new Request.Builder();
        for(String key : header.keySet()){
            builder.header(key, header.get(key));
        }

        builder.url(request.getUrl()).get();
        Request request1 = builder.build();

        return execte(request1);
    }



    @Override
    public IResponse post(IRequest request, boolean forceCache) {
        request.setMethod(IRequest.POST);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, request.getBody().toString());
        Map<String, String> header = request.getHeader();
        Request.Builder builder = new Request.Builder();
        for(String key : header.keySet()){
            builder.header(key, header.get(key));

        }

        builder.url(request.getUrl()).post(body);
        Request request1 = builder.build();
        return execte(request1);
    }


    /**
     * 功能：网络请求的执行过程+封装到BaseResponse类中缓存
     * @param request1
     * @return
     */
    private IResponse execte(Request request1) {

        BaseResponse baseResponse = new BaseResponse();

        try{

            Response respose = okHttpClient.newCall(request1).execute();
            baseResponse.setCode(respose.code());
            String body = respose.body().string();
            baseResponse.setData(body);

        }catch (IOException e){
            e.printStackTrace();

            baseResponse.setCode(BaseResponse.STATE_UNKNOWN_ERROR);
            baseResponse.setData("error="+e.getMessage());
        }



        return baseResponse;
    }
}
