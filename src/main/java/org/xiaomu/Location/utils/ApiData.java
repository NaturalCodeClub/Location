package org.xiaomu.Location.utils;

import com.alibaba.fastjson.JSONObject;

public class ApiData {

    String country;
    String province;
    String city;
    String isp;

    public ApiData(JSONObject dataJson) {
        country = dataJson.getString("country");
        city = dataJson.getString("city");
        province = dataJson.getString("province");
        isp = dataJson.getString("isp");
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;

    }

    public String getProvince() {
        return province;
    }

    public String getIsp() {
        return isp;
    }

}
