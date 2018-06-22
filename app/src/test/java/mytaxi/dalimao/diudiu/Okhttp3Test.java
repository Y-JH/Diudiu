package mytaxi.dalimao.diudiu;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Title:Okhttp3Text
 * @Package:com.dalimao.mytaxi
 * @Description:使用junit书写测试类
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/1912:36
 */
public class Okhttp3Test {

    /**
     * 功能：使用get方式进行http请求的测试
     *
     * @return String
     */
    @Test
    public void httpGet() {

        String url = "https://httpbin.org/get";
//        String url = "http://gank.io/api/xiandu/categories";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        String result = null;
        try {
            response = okHttpClient.newCall(request).execute();
            result = response.body().string();
            System.out.print("输出get方式请求的结果：==>>" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 功能：使用post方式进行http请求的测试
     */
    @Test
    public void httpPost() {
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String url = "http://httpbin.org/post";
        RequestBody body = RequestBody.create(JSON, "{\"name\":\"dalimao\"}");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            System.out.print("输出get方式请求的结果：==>>" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 功能：测试计算请求时间
     */
    @Test
    public void interceptTest() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                long start = System.currentTimeMillis();
                Request request = chain.request();
                Response response = chain.proceed(request);
                long end = System.currentTimeMillis();
                System.out.print("输出请求的时间：==>>" + (end - start));
                return response;
            }
        };
        String url = "http://gank.io/api/xiandu/categories";
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        String result = null;
        try {
            response = okHttpClient.newCall(request).execute();
            result = response.body().string();
//            System.out.print("输出get方式请求的结果：==>>" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 功能：测试缓存
     */
    @Test
    public void cacheTest() {
        Cache cache = new Cache(new File("cache.cache"), 5 * 1024 * 1024);
        String url = "http://gank.io/api/xiandu/categories";
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .cache(cache)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_CACHE)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            System.out.print("输出结果cache：==>>" + response.cacheResponse());
            System.out.print("----->输出结果net：==>>" + response.networkResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
