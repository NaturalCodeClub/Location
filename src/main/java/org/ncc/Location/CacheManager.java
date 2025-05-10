package org.ncc.Location;

import org.bukkit.Bukkit;
import org.ncc.Location.Utils.ApiData;
import org.xiaomu.Location.Location;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheManager {

    private static final ConcurrentHashMap<String, ApiData> cacheLocation = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Long> cacheTimeStamp = new ConcurrentHashMap<>();

    public void initTaskScheduler() {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(Location.getInstance(), scheduledTask -> {
            for (String s : cacheTimeStamp.keySet()) {
                if(System.currentTimeMillis() - cacheTimeStamp.get(s) >= TimeUnit.HOURS.toMillis(ConfigManager.CACHE_OUTDATED_RATE)) {
                    cacheTimeStamp.remove(s);
                    cacheLocation.remove(s);
                }
            }
        }, 1, (long) ConfigManager.CHECK_INTERVAL_MINUTES * 60 * 20);
    }

    public static boolean requestIsCacheExist(String IP) {
        return cacheLocation.containsKey(IP);
    }
    public static ApiData getCacheApiData(String IP) {
        return cacheLocation.get(IP);
    }

    public static void addCache(String IP, ApiData apiData) {
        cacheLocation.put(IP, apiData);
    }

}
