package com.dalimao.diudiu.go.common.http.biz;

/**
 * @Title:fsa
 * @Package:com.dalimao.mytaxi.splash.common.http.biz
 * @Description:返回业务数据的公共格式
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2013:15
 */
public class BaseBizResponse {

    public static final int STATE_OK = 200;
    // 密码错误
    public static final int STATE_PW_ERR = 100005;
    // token 无效／过期
    public static final int STATE_TOKEN_INVALID = 100006;
    // 用户已经存在
    public static int STATE_USER_EXIST = 100003;
    // 用户不存在
    public static int STATE_USER_NOT_EXIST = 100002;
    // 状态码
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
