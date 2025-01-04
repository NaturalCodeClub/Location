package org.xiaomu.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.xiaomu.Location.utils.getRequest;
import java.util.HashMap;
import java.util.Locale;

import com.alibaba.fastjson.JSONObject;

public class LocationManager {
    private static final HashMap<String, Boolean> locateState = new HashMap<String, Boolean>();
    private static final HashMap<String, JSONObject> Locations = new HashMap<String, JSONObject>();

    public static boolean IsExistence(String playerName, String key) {
        if (locateState.containsKey(playerName)) {
            if (locateState.get(playerName)) {
                return !Locations.get(playerName).getString(key).equals("0");
            }
        }
        return false;
    }

    public static void Locate(Player player) {

        Location.getInstance().getServer().getGlobalRegionScheduler().run(
                Location.getInstance(),
                scheduledTask -> {
                    String playerName = player.getName();
                    String playerIP = player.getAddress().getHostString();
                    String stringData;

                    if (playerIP.equals("127.0.0.1")) {
                        Location.getInstance().getLogger().warning("定位可能不适合内网映射，请确定您的服务端可获取玩家真实 IP.");
                        playerIP = "";
                    }

                    String stringResult = getRequest.sendGet("https://api.mir6.com/api/ip?ip=" + playerIP + "&type=json");

                    if (!stringResult.equals("Error")) {
                        JSONObject objectJson = JSONObject.parseObject(stringResult);

                        String code = objectJson.getString("code");
                        String msg = objectJson.getString("msg");

                        if (code.equals("200") && msg.equals("success")) {
                            stringData = objectJson.getString("data");
                            JSONObject dataJson = JSONObject.parseObject(stringData);

                            Location.getInstance().getLogger().info("对玩家 " + playerName + "(IP: " + dataJson.getString("ip") + ") 的定位成功.");
                            Locations.put(playerName, dataJson);
                            locateState.put(playerName, true);
                        } else {
                            Location.getInstance().getLogger().warning("对玩家 " + playerName + "(IP: " + playerIP + ") 的定位失败.");
                            Location.getInstance().getLogger().warning("错误信息: 返回码 " + code + " | " + msg);
                            locateState.put(playerName, false);
                        }
                    } else {
                        Location.getInstance().getLogger().warning("对 " + playerName + "(IP: " + playerIP + ") 的定位失败.");
                        Location.getInstance().getLogger().warning("错误信息: 可能是因为服务器网络状态不佳或未联网.");
                        locateState.put(playerName, false);
                    }
                }
        );
    }

    public static String getIP(Player player) {
        if (IsExistence(player.getName(), "ip")) {
            return Locations.get(player.getName()).getString("ip");
        } else {
            return "未知";
        }
    }

    public static String getCountry(Player player) {
        if (IsExistence(player.getName(), "country")) {
            return Locations.get(player.getName()).getString("country");
        } else {
            return "未知";
        }
    }
    public static String getProvince(Player player) {
        if (IsExistence(player.getName(), "province")) {
            return Locations.get(player.getName()).getString("province");
        } else {
            return "未知";
        }
    }
    public static String getCity(Player player) {
        if (IsExistence(player.getName(), "city")) {
            return Locations.get(player.getName()).getString("city");
        } else {
            return "未知";
        }
    }
    public static String getIsp(Player player) {
        if (IsExistence(player.getName(), "isp")) {
            return Locations.get(player.getName()).getString("isp");
        } else {
            return "未知";
        }
    }

    public static void removePlayer(String playerName) {
        Locations.remove(playerName);
        locateState.remove(playerName);
    }

    public static void removeAll(){
        Locations.clear();
        locateState.clear();
    }

    public static void LocateAll(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            LocationManager.Locate(player);
        }
    }
}