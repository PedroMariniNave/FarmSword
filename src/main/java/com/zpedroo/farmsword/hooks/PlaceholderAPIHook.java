package com.zpedroo.farmsword.hooks;

import com.zpedroo.farmsword.utils.farmsword.FarmSwordUtils;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import com.zpedroo.farmsword.utils.progress.ProgressConverter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Plugin plugin;

    public PlaceholderAPIHook(Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @NotNull
    public String getIdentifier() {
        return "farmsword";
    }

    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(@NotNull Player player, @NotNull String identifier) {
        ItemStack item = player.getItemInHand();
        double experience = FarmSwordUtils.getItemExperience(item);
        switch (identifier.toUpperCase()) {
            case "LEVEL":
                int level = FarmSwordUtils.getItemLevel(item);
                return NumberFormatter.getInstance().formatThousand(level);
            case "QUALITY":
                int quality = FarmSwordUtils.getItemQuality(item);
                return ProgressConverter.convertQuality(quality);
            case "PROGRESS":
                return ProgressConverter.convertExperience(experience);
            case "PERCENTAGE":
                return NumberFormatter.getInstance().formatDecimal(ProgressConverter.getPercentage(experience));
            case "POINTS":
                BigInteger pointsAmount = FarmSwordUtils.getItemPoints(item);
                return NumberFormatter.getInstance().format(pointsAmount);
        }

        return null;
    }
}