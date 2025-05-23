package org.ncc.Location;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.xiaomu.Location.Location;
import org.xiaomu.Location.papiHook;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {
    public static File configFile = new File(Location.getInstance().getDataFolder(), "config.yml");
    public static FileConfiguration config;

    public static File langFile;
    public static FileConfiguration lang;

    public static int CACHE_OUTDATED_RATE;
    public static int CHECK_INTERVAL_MINUTES;
    public static int QPS;
    public static int RETRY_COUNT_DROP;

    //TODO finish it

    public static void loadConfig(FileConfiguration conf) {
        config = conf;
        getConfigValue(config);
    }

    public static void saveConfig(FileConfiguration conf, File path) {
        try {
            conf.save(path);
        } catch (IOException e) {
            Location.getInstance().getLogger().log(Level.SEVERE, "Failed to save config, please check your file permission settings.");
        }
    }

    public static void reloadConfig(CommandSender sender) {
        config = YamlConfiguration.loadConfiguration(configFile);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new papiHook().register();
        } else {
            Location.getInstance().getLogger().warning("未找到 PlaceholderAPI 插件, 无法注册变量.");
        }
        getConfigValue(config);
        sender.sendMessage(Component.text("Reload successfully!", NamedTextColor.GREEN));
    }

    public static void getConfigValue(FileConfiguration conf) {
        CACHE_OUTDATED_RATE = conf.getInt("cache-outdated-rate", 24);
        CHECK_INTERVAL_MINUTES = conf.getInt("check-interval-minute", 10);
        QPS = conf.getInt("qps", 15);
        RETRY_COUNT_DROP = conf.getInt("retry-count-drop", 5);
    }

    public static void initConfig(FileConfiguration conf) {

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                Location.getInstance().getLogger().log(Level.SEVERE, "Failed to create config file, please check your file permission settings.");
                Location.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
                Location.getInstance().onDisable();
                return;
            }
        }
        if (conf.get("cache-outdated-rate") == null) {
            conf.set("cache-outdated-rate", 24);
            conf.setComments("cache-outdated-rate", List.of("配置缓存在内存中各项IP位置信息的清除时间，单位为小时，需要注意的是缓存并不持久化保存，意味着重启服务器缓存会丢失."));

        }
        if (conf.get("check-interval-minute") == null) {
            conf.set("check-interval-minute", 10);
            conf.setComments("check-interval-minute", List.of("配置插件检测缓存的周期，单位为分钟."));
        }

        if (conf.get("qps") == null) {
            conf.set("qps", 15);
            conf.setComments("qps", List.of("配置插件查询IP信息的Api访问频次，单位为 次/秒 如果您不更改本项目代码中所访问的Api，请不要使其高于 15 ."));
        }

        if(conf.get("retry-count-drop") == null) {
            conf.set("retry-count-drop", 5);
            conf.setComments("retry-count-drop",List.of("配置当获取地址时API返回代码 202 时插件重试获取地址的次数，超出此次数，玩家的位置将直接显示为 “未知” ."));
        }
        try {
            conf.save(configFile);
        } catch (IOException e) {
            Location.getInstance().getLogger().log(Level.SEVERE, "Failed to create config file, please check your file permission settings.");
            Location.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
            Location.getInstance().onDisable();
        }

    }
}
