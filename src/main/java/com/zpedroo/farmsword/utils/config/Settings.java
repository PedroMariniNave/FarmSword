package com.zpedroo.farmsword.utils.config;

import com.zpedroo.farmsword.utils.FileUtils;

import java.util.List;

public class Settings {

    public static final String COMMAND = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.command");

    public static final List<String> ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.aliases");

    public static final String ADMIN_PERMISSION = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.admin-permission");

    public static final int FARM_SWORD_PICKUP_COOLDOWN = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.farm-sword-pickup-cooldown");

    public static final boolean PREVENT_PLAYER_HIT = FileUtils.get().getBoolean(FileUtils.Files.CONFIG, "Settings.prevent-player-hit");
}