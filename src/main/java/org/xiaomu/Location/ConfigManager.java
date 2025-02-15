package org.xiaomu.Location;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {
    public static File configFile = new File(Location.getInstance().getDataFolder(), "config.yml");
    public static FileConfiguration config;

    public static File langFile;
    public static FileConfiguration lang;
    //TODO finish it

    public static void loadConfig(FileConfiguration conf) {
        config = conf;
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
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new papiHook().register();
        } else {
            Location.getInstance().getLogger().warning("未找到 PlaceholderAPI 插件, 无法注册变量.");
        }
        sender.sendMessage(Component.text("Reload successfully!", NamedTextColor.GREEN));
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
            conf.setComments("cache-outdated-rate", List.of("配置缓存在内存中各项IP位置信息的清除时间，单位为小时，需要注意的是缓存并不持久化保存，意味着重启服务器缓存会丢失"));

        }
        if (conf.get("check-interval") == null) {
            conf.set("check-interval", 1);
            conf.setComments("check-interval", List.of("配置插件检测缓存的周期，单位为小时"));
        }

        if (conf.get("qps") == null) {
            conf.set("qps", 15);
            conf.setComments("qps", List.of("配置插件查询IP信息的Api访问频次，单位为 次/秒 如果您不更改本项目代码中所访问的Api，请不要使其高于 15 "));

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
