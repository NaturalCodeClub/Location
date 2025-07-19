package org.xiaomu.Location;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ncc.Location.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class Commander implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage("[Location] 帮助");
            commandSender.sendMessage("/Location player [玩家名称] 获取玩家的真实地理位置定位.");
            commandSender.sendMessage("/Location flush 异步刷新当前服务器上所有玩家的地理定位信息.");
            commandSender.sendMessage("/Location reload 重载配置文件.");
            return true;
        }

        if (args[0].equalsIgnoreCase("player")) {
            if (args.length < 2) {
                commandSender.sendMessage("[Location] 缺少参数 [玩家名称]. 用法: /Location player [玩家名称]");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);

            if (!(player == null)) {
                commandSender.sendMessage(player.getName() + " >> IP: " + LocationManager.getIP(player) + " " + LocationManager.getCountry(player) + " " + LocationManager.getProvince(player) + " " + LocationManager.getCity(player) + " | " + LocationManager.getIsp(player));
            } else {
                commandSender.sendMessage("[Location] 此玩家似乎不在线!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("flush")) {
            LocationManager.removeAll();
            LocationManager.LocateAll();
            commandSender.sendMessage("[Location] 正在异步刷新所有玩家的地理定位信息.");
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")){
            ConfigManager.reloadConfig(commandSender);
            return true;
        }

        commandSender.sendMessage("[Location] 未知的命令.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return List.of("help", "flush", "player","reload");
        }
        if (strings.length == 2) {
            if (strings[0].equals("player")) {
                List l = new ArrayList<String>();
                Bukkit.getOnlinePlayers().forEach(player -> {
                    l.add(player.getName());
                });
                return l;
            }
        }
        return null;
    }
}