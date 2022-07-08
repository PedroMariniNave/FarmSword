package com.zpedroo.farmsword.utils.config;

import com.zpedroo.farmsword.utils.FileUtils;
import com.zpedroo.farmsword.utils.color.Colorize;

public class Titles {

    public static final String[] FARM_SWORD_UPGRADE = new String[]{
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.farm-sword-upgrade.title")),
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.farm-sword-upgrade.subtitle"))
    };
}