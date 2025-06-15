package org.xiaomu.Location;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ncc.Location.ConfigManager;

import static org.ncc.Location.QueueManager.queueManager;

public class Location extends JavaPlugin {
    private static Location instance;
    public static final String author = "xiaomu18";
    public static final String version = "1.0.4";
//    public static final QueueManager queueManager = new QueueManager();

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("插件加载中...");

        //Config Init and Load
        ConfigManager.initConfig(getConfig());
        ConfigManager.loadConfig(getConfig());
        getLogger().info("Config Loaded!");

        if (Bukkit.getPluginCommand("Location") != null) {
            Bukkit.getPluginCommand("Location").setExecutor(new Commander());
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new papiHook().register();
        } else {
            getLogger().warning("未找到 PlaceholderAPI 插件, 无法注册变量.");
        }

        Bukkit.getPluginManager().registerEvents(new EventManager(),this);

        getLogger().info("插件加载完毕! 作者 " + author + " | 版本 " + version);

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            getLogger().info("检测到插件重载, 重新异步获取在线玩家定位.");
            LocationManager.LocateAll();
        }
    }

    public static Location getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        LocationManager.removeAll();

        //Queue Thread interrupt
        queueManager.getThread().interrupt();
        Bukkit.getGlobalRegionScheduler().cancelTasks(this);
        getLogger().info("数据释放完毕！插件已卸载.");
    }
}