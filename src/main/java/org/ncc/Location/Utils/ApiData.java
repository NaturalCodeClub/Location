package org.ncc.Location.Utils;

import com.alibaba.fastjson.JSONObject;

public class ApiData {

    String country;
    String province;
    String city;
    String isp;
    String district;

    public ApiData(JSONObject dataJson) {
        country = dataJson.getString("country");
        city = dataJson.getString("city");
        province = dataJson.getString("province");
        isp = dataJson.getString("isp");
        district = dataJson.getString("district");
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

    public String getDistrict() {
        return district;
    }

}
