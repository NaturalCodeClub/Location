package org.xiaomu.Location;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Location extends JavaPlugin {
    private static Location instance;
    public static final String anthor = "xiaomu18";
    public static final String version = "1.0.4";

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("插件加载中...");

        if (Bukkit.getPluginCommand("Location") != null) {
            Bukkit.getPluginCommand("Location").setExecutor(new Commander());
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new papiHook().register();
        } else {
            getLogger().warning("未找到 PlaceholderAPI 插件, 无法注册变量.");
        }

        Bukkit.getPluginManager().registerEvents(new EventManager(),this);

        getLogger().info("插件加载完毕! 作者 " + anthor + " | 版本 " + version);

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
        getLogger().info("数据释放完毕！插件已卸载.");
    }
}