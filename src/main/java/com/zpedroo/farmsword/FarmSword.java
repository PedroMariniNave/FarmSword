package com.zpedroo.farmsword;

import com.zpedroo.farmsword.commands.FarmSwordCmd;
import com.zpedroo.farmsword.listeners.FarmSwordListeners;
import com.zpedroo.farmsword.listeners.PlayerGeneralListeners;
import com.zpedroo.farmsword.listeners.PointsListeners;
import com.zpedroo.farmsword.managers.DataManager;
import com.zpedroo.farmsword.utils.FileUtils;
import com.zpedroo.farmsword.utils.cooldown.Cooldown;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import com.zpedroo.farmsword.utils.menu.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

import static com.zpedroo.farmsword.utils.config.Settings.ALIASES;
import static com.zpedroo.farmsword.utils.config.Settings.COMMAND;

public class FarmSword extends JavaPlugin {

    private static FarmSword instance;
    public static FarmSword get() { return instance; }

    public void onEnable() {
        instance = this;
        new FileUtils(this);
        new DataManager();
        new Cooldown();
        new Menus();

        registerListeners();
        registerCommand(COMMAND, ALIASES, new FarmSwordCmd());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerGeneralListeners(), this);
        getServer().getPluginManager().registerEvents(new FarmSwordListeners(), this);
        getServer().getPluginManager().registerEvents(new PointsListeners(), this);
    }

    private void registerCommand(String command, List<String> aliases, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCmd = constructor.newInstance(command, this);
            pluginCmd.setAliases(aliases);
            pluginCmd.setExecutor(executor);

            Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            commandMap.register(getName().toLowerCase(), pluginCmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}