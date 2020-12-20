package me.mr_redstone5230.switchdifficulty;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class SwitchDifficulty extends JavaPlugin implements CommandExecutor, TabCompleter {

    public static List<Difficulty> enabled = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        if (getConfig().getBoolean("enabled.peaceful") == true) {
            enabled.add(Difficulty.PEACEFUL);
        }
        if (getConfig().getBoolean("enabled.easy") == true) {
            enabled.add(Difficulty.EASY);
        }
        if (getConfig().getBoolean("enabled.normal") == true) {
            enabled.add(Difficulty.NORMAL);
        }
        if (getConfig().getBoolean("enabled.hard") == true) {
            enabled.add(Difficulty.HARD);
        }
        getServer().getPluginCommand("switch-difficulty").setExecutor(this);
        getServer().getPluginCommand("switch-difficulty").setTabCompleter(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isAdmin = false;
        if (sender.hasPermission("switch-difficulty.reload")) isAdmin = true;
        if (args == null || args.length == 0) {
            if (!isAdmin)
                sender.sendMessage(ChatColor.RED + "Usage: /switch-difficulty <" + getDifficultyList() + "/>");
            else
                sender.sendMessage(ChatColor.RED + "Usage: /switch-difficulty <" + getDifficultyList() + "/reload>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (isAdmin) {
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configuration file reloaded !");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Not enough permission !");
            return true;
        }
        boolean isPlayer = false;
        if (sender instanceof Player) { isPlayer = true; }

        Difficulty dif;
        try {
            dif = Difficulty.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            if (!isAdmin)
                sender.sendMessage(ChatColor.RED + "Invalid argument. Available args: " + getDifficultyList());
            else
                sender.sendMessage(ChatColor.RED + "Invalid argument. Available args: " + getDifficultyList() + "/reload");
            return true;
        }

        if (!isPlayer) {
            sender.sendMessage(ChatColor.RED + "Only a player can use this command !");
            return true;
        }
        Player player = (Player) sender;


        if (!enabled.contains(dif)) {
            sender.sendMessage(ChatColor.RED + "This difficulty isn't enabled in the plugin.");
            return true;
        }

        player.getWorld().setDifficulty(dif);
        player.sendMessage(ChatColor.GREEN + "The difficulty has been changed.");

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1)
            return new ArrayList<>();
        List<String> argss = new ArrayList<>();
        if (sender.hasPermission("switch-difficulty.reload"))
            argss.add("reload");

        if (!enabled.isEmpty()) {
            enabled.forEach(dif -> {
                argss.add(dif.toString().toLowerCase());
            });
        }

        return argss;
    }

    public static String getDifficultyList() {
        if (enabled.isEmpty())
            return "(No difficulty available)";
        AtomicReference<String> response = new AtomicReference<>("");
        enabled.forEach(name -> {
            if (response.get() != "")
                response.set(response + "/");
           response.set(response.get() + name.toString().toLowerCase());
        });
        return response.get();
    }
}
