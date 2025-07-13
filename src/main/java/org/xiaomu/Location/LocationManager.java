package org.xiaomu.Location;

import com.alibaba.fastjson.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.ncc.Location.CacheManager;
import org.ncc.Location.ConfigManager;
import org.ncc.Location.QueueManager;
import org.ncc.Location.Utils.ApiData;
import org.ncc.Location.Utils.LocationType;
import org.xiaomu.Location.utils.getRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LocationManager {
    private static final HashMap<String, Boolean> locateState = new HashMap<>();
    //    private static final HashMap<String, JSONObject> Locations = new HashMap<>();
    private static final ConcurrentHashMap<String, ApiData> newLocations = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Player, Integer> retryMap = new ConcurrentHashMap<>();
    public static final HashSet<Player> requestingPlayers = new HashSet<>();

//    // safely delete it --finished
//    public static boolean IsExistence(String playerName, String key) {
//        if (locateState.containsKey(playerName)) {
//            if (locateState.get(playerName)) {
//                return !Locations.get(playerName).getString(key).equals("0");
//            }
//        }
//        return false;
//    }

    public static void Locate(Player player) {
        if (!retryMap.containsKey(player)) retryMap.put(player, 0);
        else {
            if (retryMap.get(player) >= ConfigManager.RETRY_COUNT_DROP) {
                Location.getInstance().getLogger().warning("定位重试次数超过了设置范围，已取消定位。");
                retryMap.remove(player);
                return;
            }
        }
        requestingPlayers.add(player);

        QueueManager.getQueueManager().submit(() -> {
            String playerName = player.getName();
            String playerIP = player.getAddress().getHostString();
            String stringData;

//            if (playerIP.equals("127.0.0.1")) {
//                Location.getInstance().getLogger().warning("定位可能不适合内网映射，请确定您的服务端可获取玩家真实 IP.");
//                playerIP = "";
//            }
            if (CacheManager.requestIsCacheExist(playerIP)) {

                newLocations.put(playerName, replaceValue(CacheManager.getCacheApiData(playerIP)));
                locateState.put(playerName, true);
                requestingPlayers.remove(player);
                retryMap.remove(player);
                Location.getInstance().getLogger().info("对IP " + playerIP + "命中缓存");
                return;
            }

            String stringResult = getRequest.sendGet("https://api.mir6.com/api/ip?ip=" + playerIP + "&type=json");

            if (!stringResult.equals("Error") && !stringResult.equals("定位失败")) {
                JSONObject objectJson = JSONObject.parseObject(stringResult);

                String code = objectJson.getString("code");
                String msg = objectJson.getString("msg");

                if (code.equals("200") && msg.equals("success")) {
                    stringData = objectJson.getString("data");
                    JSONObject dataJson = JSONObject.parseObject(stringData);

                    // Debug
//                    System.out.println(stringData);

                    Location.getInstance().getLogger().info("对玩家 " + playerName + "(IP: " + dataJson.getString("ip") + ") 的定位成功.");
//                            Locations.put(playerName, dataJson);
                    newLocations.put(playerName, replaceValue(new ApiData(dataJson)));
                    locateState.put(playerName, true);
                    CacheManager.addCache(playerIP, new ApiData(dataJson));
                    requestingPlayers.remove(player);
                    retryMap.remove(player);
                } else if (code.equals("202")) {
                    //TODO test is needed
                    Location.getInstance().getLogger().warning("QPS设置过高，无法定位，玩家 " + playerIP + " 的定位将于下一周期进行.");
//                    int i = retryMap.get(player);
//                    i++;
//                    retryMap.put(player, i);

                    retryMap.merge(player, 1, Integer::sum);
                    Bukkit.getGlobalRegionScheduler().runDelayed(Location.getInstance(), scheduledTask -> {
                        Locate(player);
                    }, 20L);
                } else {
                    Location.getInstance().getLogger().warning("对玩家 " + playerName + "(IP: " + playerIP + ") 的定位失败.");
                    Location.getInstance().getLogger().warning("错误信息: 返回码 " + code + " | " + msg);
                    locateState.put(playerName, false);
                    requestingPlayers.remove(player);
                    retryMap.remove(player);
                }
            } else {
                Location.getInstance().getLogger().warning("对 " + playerName + "(IP: " + playerIP + ") 的定位失败.");
                Location.getInstance().getLogger().warning("错误信息: 可能是因为服务器网络状态不佳或未联网.");
                locateState.put(playerName, false);
                requestingPlayers.remove(player);
                retryMap.remove(player);
            }
        });

//        Location.getInstance().getServer().getGlobalRegionScheduler().run(
//                Location.getInstance(),
//                scheduledTask -> {
//                    String playerName = player.getName();
//                    String playerIP = player.getAddress().getHostString();
//                    String stringData;
//
//                    if (playerIP.equals("127.0.0.1")) {
//                        Location.getInstance().getLogger().warning("定位可能不适合内网映射，请确定您的服务端可获取玩家真实 IP.");
//                        playerIP = "";
//                    }
//
//                    String stringResult = getRequest.sendGet("https://api.mir6.com/api/ip?ip=" + playerIP + "&type=json");
//
//                    if (!stringResult.equals("Error")) {
//                        JSONObject objectJson = JSONObject.parseObject(stringResult);
//
//                        String code = objectJson.getString("code");
//                        String msg = objectJson.getString("msg");
//
//                        if (code.equals("200") && msg.equals("success")) {
//                            stringData = objectJson.getString("data");
//                            JSONObject dataJson = JSONObject.parseObject(stringData);
//
//                            Location.getInstance().getLogger().info("对玩家 " + playerName + "(IP: " + dataJson.getString("ip") + ") 的定位成功.");
////                            Locations.put(playerName, dataJson);
//                            newLocations.put(playerName, new ApiData(dataJson));
//                            locateState.put(playerName, true);
//                        } else if (code.equals("202")) {
//
//                        } else {
//                            Location.getInstance().getLogger().warning("对玩家 " + playerName + "(IP: " + playerIP + ") 的定位失败.");
//                            Location.getInstance().getLogger().warning("错误信息: 返回码 " + code + " | " + msg);
//                            locateState.put(playerName, false);
//                        }
//                    } else {
//                        Location.getInstance().getLogger().warning("对 " + playerName + "(IP: " + playerIP + ") 的定位失败.");
//                        Location.getInstance().getLogger().warning("错误信息: 可能是因为服务器网络状态不佳或未联网.");
//                        locateState.put(playerName, false);
//                    }
//                }
//        );
    }

    public static String getIP(Player player) {
//        if (IsExistence(player.getName(), "ip")) {
//            return Locations.get(player.getName()).getString("ip");
        return Objects.requireNonNull(player.getAddress()).getHostString();
//        } else {
//            return "未知";
//        }
    }

    public static String getCountry(Player player) {
//        if (IsExistence(player.getName(), "country")) {
//            return Locations.get(player.getName()).getString("country");
//        } else {
//            return "未知";
//        }

//        if (newLocations.get(player.getName()).getCountry().equals("局域网") || newLocations.get(player.getName()).getCountry().equals("本地局域网") || newLocations.get(player.getName()).getCountry().equals("保留地址")) {
//        if (newLocations.get(player.getName()).getCountry().isEmpty() || !locateState.get(player.getName())) {
//            return "未知";
//        }
        return newLocations.get(player.getName()).getCountry();

    }

    public static String getProvince(Player player) {
//        if (IsExistence(player.getName(), "province")) {
//            return Locations.get(player.getName()).getString("province");
//        } else {
//            return "未知";
//        }
//
//        if (newLocations.get(player.getName()).getProvince().isEmpty() || locateState.get(player.getName())) {
//            return "未知";
//        }
        return newLocations.get(player.getName()).getProvince();
    }

    public static String getCity(Player player) {
//        if (newLocations.get(player.getName()).getCity().isEmpty() || !locateState.get(player.getName())) {
//            return "未知";
//        }
        return newLocations.get(player.getName()).getCity();
    }

    public static String getIsp(Player player) {
//        if (IsExistence(player.getName(), "isp")) {
//            return Locations.get(player.getName()).getString("isp");
//        } else {
//            return "未知";
//        }
//        if (newLocations.get(player.getName()).getIsp().isEmpty() || !locateState.get(player.getName())) {
//            return "未知";
//        }
        return newLocations.get(player.getName()).getIsp();
    }

    public static String getDistrict(Player player) {
//        if (newLocations.get(player.getName()).getDistrict().isEmpty() || !locateState.get(player.getName())) {
//            return "未知";
//        }
        return newLocations.get(player.getName()).getDistrict();
    }

    public static boolean isRequesting(Player player) {
        return requestingPlayers.contains(player);
    }

    public static void removePlayer(String playerName) {
//        Locations.remove(playerName);
        newLocations.remove(playerName);
        locateState.remove(playerName);
    }

    public static void removeAll() {
//        Locations.clear();
        newLocations.clear();
        CacheManager.removeAllCaches();
        locateState.clear();
    }

    public static void LocateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            LocationManager.Locate(player);
        }
    }

    //Code Shit, need further optimization.
    public static ApiData replaceValue(ApiData data) {
        String newCountry;
        String newProvince;
        String newCity;
        String newIsp;
        String newDistrict;

//        debug
//        System.out.println(data.getCountry() + " " + data.getProvince() + " " + data.getCity() + " " + data.getIsp() + " " + data.getDistrict());

        newCountry = performReplace(data.getCountry(), ConfigManager.replacementKey, data, ConfigManager.COUNTRY_REPLACEMENT);
        newProvince = performReplace(data.getProvince(), ConfigManager.replacementKey, data, ConfigManager.PROVINCE_REPLACEMENT);
        newCity = performReplace(data.getCity(), ConfigManager.replacementKey, data, ConfigManager.CITY_REPLACEMENT);
        newIsp = performReplace(data.getIsp(), ConfigManager.replacementKey, data, ConfigManager.ISP_REPLACEMENT);
        newDistrict = performReplace(data.getDistrict(), ConfigManager.replacementKey, data, ConfigManager.DISTRICT_REPLACEMENT);

        // debug
//        System.out.println(newCountry + " " + newProvince + " " + newCity + " " + newIsp + " " + newDistrict);

        return new ApiData(newCountry, newProvince, newCity, newIsp, newDistrict);
//
//        for (String s : ConfigManager.replacementKey) {
//            //Country
//            if (data.getCountry().contains(s)) {
//                switch (ConfigManager.COUNTRY_REPLACEMENT) {
//                    case PROVINCE -> {
//                        if (data.getProvince().contains(s)) {
//                            newCountry = "未知";
//                            break;
//                        }
//                        newCountry = data.getProvince();
//                    }
//                    case CITY -> {
//                        if (data.getCity().contains(s)) {
//                            newCountry = "未知";
//                            break;
//                        }
//                        newCountry = data.getCity();
//                    }
//                    case ISP -> {
//                        if (data.getIsp().contains(s)) {
//                            newCountry = "未知";
//                            break;
//                        }
//                        newCountry = data.getIsp();
//                    }
//                    case DISTRICT -> {
//                        if (data.getDistrict().contains(s)) {
//                            newCountry = "未知";
//                            break;
//                        }
//                        newCountry = data.getDistrict();
//                    }
//                    default -> newCountry = "未知";
//                }
//            }
//            //Province
//            if (data.getProvince().contains(s)) {
//                switch (ConfigManager.PROVINCE_REPLACEMENT) {
//                    case COUNTRY -> {
//                        if (data.getCountry().contains(s)) {
//                            newProvince = "未知";
//                            break;
//                        }
//                        newProvince = data.getCountry();
//                    }
//                    case CITY -> {
//                        if (data.getCity().contains(s)) {
//                            newProvince = "未知";
//                            break;
//                        }
//                        newProvince = data.getCity();
//                    }
//                    case ISP -> {
//                        if (data.getIsp().contains(s)) {
//                            newProvince = "未知";
//                            break;
//                        }
//                        newProvince = data.getIsp();
//                    }
//                    case DISTRICT -> {
//                        if (data.getDistrict().contains(s)) {
//                            newProvince = "未知";
//                            break;
//                        }
//                        newProvince = data.getDistrict();
//                    }
//                    default -> newProvince = "未知";
//                }
//            }
//            //CITY
//            if (data.getCity().contains(s)) {
//                switch (ConfigManager.CITY_REPLACEMENT) {
//                    case COUNTRY -> {
//                        if (data.getCountry().contains(s)) {
//                            newCity = "未知";
//                            break;
//                        }
//                        newCity = data.getCountry();
//                    }
//                    case PROVINCE -> {
//                        if (data.getProvince().contains(s)) {
//                            newCity = "未知";
//                            break;
//                        }
//                        newCity = data.getProvince();
//                    }
////                    case CITY -> {
////                        if(data.getCity().contains(s)) {
////                            newCity = "未知";
////                            break;
////                        }
////                        newCity = data.getCity();
////                    }
//                    case ISP -> {
//                        if (data.getIsp().contains(s)) {
//                            newCity = "未知";
//                            break;
//                        }
//                        newCity = data.getIsp();
//                    }
//                    case DISTRICT -> {
//                        if (data.getDistrict().contains(s)) {
//                            newCity = "未知";
//                            break;
//                        }
//                        newCity = data.getDistrict();
//                    }
//                    default -> newCity = "未知";
//                }
//            }
//            //ISP
//            if (data.getDistrict().contains(s)) {
//                switch (ConfigManager.ISP_REPLACEMENT) {
//                    case COUNTRY -> {
//                        if (data.getCountry().contains(s)) {
//                            newIsp = "未知";
//                            break;
//                        }
//                        newIsp = data.getCountry();
//                    }
//                    case PROVINCE -> {
//                        if (data.getProvince().contains(s)) {
//                            newIsp = "未知";
//                            break;
//                        }
//                        newIsp = data.getProvince();
//                    }
//                    case CITY -> {
//                        if (data.getCity().contains(s)) {
//                            newIsp = "未知";
//                            break;
//                        }
//                        newIsp = data.getCity();
//                    }
////                    case ISP -> {
////                        if(data.getIsp().contains(s)) {
////                            newIsp = "未知";
////                            break;
////                        }
////                        newIsp = data.getIsp();
////                    }
//                    case DISTRICT -> {
//                        if (data.getDistrict().contains(s)) {
//                            newIsp = "未知";
//                            break;
//                        }
//                        newIsp = data.getDistrict();
//                    }
//                    default -> newIsp = "未知";
//                }
//            }
//            //DISTRICT
//            if (data.getDistrict().contains(s)) {
//                switch (ConfigManager.DISTRICT_REPLACEMENT) {
//                    case COUNTRY -> {
//                        if (data.getCountry().contains(s)) {
//                            newDistrict = "未知";
//                            break;
//                        }
//                        newDistrict = data.getCountry();
//                    }
//                    case PROVINCE -> {
//                        if (data.getProvince().contains(s)) {
//                            newDistrict = "未知";
//                            break;
//                        }
//                        newDistrict = data.getProvince();
//                    }
//                    case CITY -> {
//                        if (data.getCity().contains(s)) {
//                            newDistrict = "未知";
//                            break;
//                        }
//                        newDistrict = data.getCity();
//                    }
//                    case ISP -> {
//                        if (data.getIsp().contains(s)) {
//                            newDistrict = "未知";
//                            break;
//                        }
//                        newDistrict = data.getIsp();
//                    }
////                    case DISTRICT -> {
////                        if(data.getDistrict().contains(s)) {
////                            newDistrict = "未知";
////                            break;
////                        }
////                        newDistrict = data.getDistrict();
////                    }
//                    default -> newDistrict = "未知";
//                }
//            }
//
//            if (newCountry != null && newProvince != null && newCity != null & newIsp != null && newDistrict != null) {
//                break;
//            }
//        }
//        if (newCountry == null) newCountry = data.getCountry();
//        if (newProvince == null) newProvince = data.getProvince();
//        if (newCity == null) newCity = data.getCity();
//        if (newDistrict == null) newDistrict = data.getDistrict();
//        if (newIsp == null) newIsp = data.getIsp();
//        return new ApiData(newCountry, newProvince, newCity, newIsp, newDistrict);
    }

    private static String performReplace(@NotNull String str, List<String> keyList, ApiData data, LocationType strategy) {
        for (String key : keyList) {
            //TODO debug
            System.out.println(str + " " + str.contains(key));
            if (str.contains(key) || str.isBlank()) {
                switch (strategy) {
                    case COUNTRY -> {
                        if (data.getCountry().contains(key)) {
                            str = "未知";
                            break;
                        }
                        str = data.getCountry();
                    }
                    case PROVINCE -> {
                        if (data.getProvince().contains(key)) {
                            str = "未知";
                            break;
                        }
                        str = data.getProvince();
                    }
                    case CITY -> {
                        if (data.getCity().contains(key)) {
                            str = "未知";
                            break;
                        }
                        str = data.getCity();
                    }
                    case ISP -> {
                        if (data.getIsp().contains(key)) {
                            str = "未知";
                            break;
                        }
                        str = data.getIsp();
                    }
                    case DISTRICT -> {
                        if (data.getDistrict().contains(key)) {
                            str = "未知";
                            break;
                        }
                        str = data.getDistrict();
                    }
                    default -> str = "未知";
                }
                return str;
            }
        }
        return str;
    }
}
