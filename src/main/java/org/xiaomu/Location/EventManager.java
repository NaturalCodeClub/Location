package org.xiaomu.Location;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Location.getInstance().getLogger().info("玩家 " + e.getPlayer().getName() + " 加入服务器，异步定位其地理位置.");
        LocationManager.Locate(e.getPlayer(),false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Location.getInstance().getLogger().info("玩家 " + e.getPlayer().getName() + " 离线了, 释放其地理定位信息.");
        LocationManager.removePlayer(e.getPlayer().getName());
    }
}