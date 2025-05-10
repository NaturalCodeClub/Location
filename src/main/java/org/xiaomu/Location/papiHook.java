package org.xiaomu.Location;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class papiHook extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return Location.author;
    }

    @Override
    public String getIdentifier() {
        return "Location";
    }

    @Override
    public String getVersion() {
        return Location.version;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        Player onlinePlayer = Bukkit.getPlayer(player.getName());

        if (onlinePlayer == null) {
            return "玩家不在线";
        }

        if(params.equalsIgnoreCase("Country")){
            if(LocationManager.isRequesting(onlinePlayer)) return "获取中";
            return LocationManager.getCountry(onlinePlayer);
        }

        if(params.equalsIgnoreCase("Province")) {
            if(LocationManager.isRequesting(onlinePlayer)) return "获取中";
            return LocationManager.getProvince(onlinePlayer);
        }

        if(params.equalsIgnoreCase("City")) {
            if(LocationManager.isRequesting(onlinePlayer)) return "获取中";
            return LocationManager.getCity(onlinePlayer);
        }

        if(params.equalsIgnoreCase("Isp")) {
            if(LocationManager.isRequesting(onlinePlayer)) return "获取中";
            return LocationManager.getIsp(onlinePlayer);
        }
        return null; // Placeholder is unknown by the Expansion
    }

    //Make it no longer not work when execute /papi reload
    @Override
    public boolean persist() {
        return true;
    }
}
