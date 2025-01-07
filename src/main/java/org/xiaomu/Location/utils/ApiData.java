package org.xiaomu.Location.utils;

import org.json.simple.JSONObject;

public class ApiData {

    public ApiData(JSONObject dataJson){
        //TODO finish it
        String country = dataJson.get("country").toString();
        String city = dataJson.get("city").toString();
        String province = dataJson.get("province").toString();
        String isp = dataJson.get("isp").toString();
    }

}
