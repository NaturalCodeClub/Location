package org.ncc.Location.Utils;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;

public class ApiData {

    String country;
    String province;
    String city;
    String isp;
    String districts;

    public ApiData(JSONObject dataJson) {
        country = dataJson.getString("country");
        city = dataJson.getString("city");
        province = dataJson.getString("province");
        isp = dataJson.getString("isp");
        districts = dataJson.getString("districts");
    }

    public ApiData(@NotNull String country_, @NotNull String province_, @NotNull String city_, @NotNull String isp_, @NotNull String district_) {
        country = country_;
        city = city_;
        province = province_;
        isp = isp_;
        districts = district_;
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
        return districts;
    }

}
