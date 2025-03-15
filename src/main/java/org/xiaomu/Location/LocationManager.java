package org.xiaomu.Location;

import com.alibaba.fastjson.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ncc.Location.QueueManager;
import org.ncc.Location.Utils.ApiData;
import org.xiaomu.Location.utils.getRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LocationManager {
    private static final HashMap<String, Boolean> locateState = new HashMap<>();
    //    private static final HashMap<String, JSONObject> Locations = new HashMap<>();
    private static final ConcurrentHashMap<String, ApiData> newLocations = new ConcurrentHashMap<>();
    //TODO finish it
    private static final ConcurrentHashMap<String, ApiData> cacheLocation = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Long> cacheTimeStamp = new ConcurrentHashMap<>();
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
        else{
            //Retry need to sleep for no longer fail
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

            String stringResult = getRequest.sendGet("https://api.mir6.com/api/ip?ip=" + playerIP + "&type=json");

            if (!stringResult.equals("Error") && !stringResult.equals("定位失败")) {
                JSONObject objectJson = JSONObject.parseObject(stringResult);

                String code = objectJson.getString("code");
                String msg = objectJson.getString("msg");

                if (code.equals("200") && msg.equals("success")) {
                    stringData = objectJson.getString("data");
                    JSONObject dataJson = JSONObject.parseObject(stringData);

                    Location.getInstance().getLogger().info("对玩家 " + playerName + "(IP: " + dataJson.getString("ip") + ") 的定位成功.");
//                            Locations.put(playerName, dataJson);
                    newLocations.put(playerName, new ApiData(dataJson));
                    locateState.put(playerName, true);
                    requestingPlayers.remove(player);
                    retryMap.remove(player);
                } else if (code.equals("202")) {
                    //TODO finish it
                    Location.getInstance().getLogger().warning("QPS设置过高，无法定位，玩家 " + playerIP + " 的定位将于下一周期进行.");
//                    int i = retryMap.get(player);
//                    i++;
//                    retryMap.put(player, i);
                    retryMap.merge(player, 1, Integer::sum);
                    Locate(player);
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
//                            //TODO finish it
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

        if (newLocations.get(player.getName()).getCountry().equals("局域网") || newLocations.get(player.getName()).getCountry().equals("本地局域网") || newLocations.get(player.getName()).getCountry().equals("保留地址")) {
            return "本地";
        }
        if (!locateState.get(player.getName())) {
            return "未知";
        }
        return newLocations.get(player.getName()).getCountry();

    }

    public static String getProvince(Player player) {
//        if (IsExistence(player.getName(), "province")) {
//            return Locations.get(player.getName()).getString("province");
//        } else {
//            return "未知";
//        }

        if (newLocations.get(player.getName()).getProvince().isEmpty()) {
            return "未知";
        }
        if (!locateState.get(player.getName())) {
            return "未知";
        }
        return newLocations.get(player.getName()).getProvince();
    }

    public static String getCity(Player player) {
        if (newLocations.get(player.getName()).getCity().isEmpty()) {
            return "未知";
        }
        if (!locateState.get(player.getName())) {
            return "未知";
        }
        return newLocations.get(player.getName()).getCity();
    }

    public static String getIsp(Player player) {
//        if (IsExistence(player.getName(), "isp")) {
//            return Locations.get(player.getName()).getString("isp");
//        } else {
//            return "未知";
//        }
        if (newLocations.get(player.getName()).getIsp().isEmpty()) {
            return "未知";
        }
        if (!locateState.get(player.getName())) {
            return "未知";
        }
        return newLocations.get(player.getName()).getIsp();
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
        locateState.clear();
    }

    public static void LocateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            LocationManager.Locate(player);
        }
    }
}