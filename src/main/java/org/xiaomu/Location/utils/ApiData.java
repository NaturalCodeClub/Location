package org.xiaomu.Location.utils;

import com.alibaba.fastjson.JSONObject;

public class ApiData {

    public ApiData(JSONObject dataJson){
        //TODO finish it
        String country = dataJson.getString("country");
        String city = dataJson.getString("city");
        String province = dataJson.getString("province");
        String isp = dataJson.getString("isp");
    }

}
