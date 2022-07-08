package com.zpedroo.farmsword.utils.config;

import com.zpedroo.farmsword.utils.FileUtils;
import com.zpedroo.farmsword.utils.color.Colorize;

public class Messages {

    public static final String COOLDOWN = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.cooldown"));

    public static final String PLAYER_HIT = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.player-hit"));
}