package com.dalimao.mytaxi.account.module;

import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.lbs.LocationInfo;

import java.util.List;

/**
 * @Title:NearDriverResponse
 * @Package:com.dalimao.diudiu.go.account.module
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2216:54
 */
public class NearDriverResponse extends BaseBizResponse {

    /**
     * msg : ok
     * code : 200
     * data : [{"latitude":1.001,"rotation":0,"key":"key0","longitude":0.999}]
     */

    private List<LocationInfo> data;

    public List<LocationInfo> getData() {
        return data;
    }

    public void setData(List<LocationInfo> data) {
        this.data = data;
    }

}
