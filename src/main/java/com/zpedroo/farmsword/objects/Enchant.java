package com.zpedroo.farmsword.objects;

import com.zpedroo.farmsword.enums.EnchantProperty;
import com.zpedroo.farmsword.utils.farmsword.FarmSwordUtils;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Enchant {

    private final String name;
    private final int initialLevel;
    private final int maxLevel;
    private final int requiredLevel;
    private final BigInteger costPerLevel;
    private final Map<EnchantProperty, Number> enchantProperties;

    public Number getPropertyEffect(EnchantProperty property) {
        return enchantProperties.getOrDefault(property, 0);
    }

    public List<String> getPlaceholders() {
        List<String> placeholders = new ArrayList<>();
        placeholders.add("{" + name + "}");
        placeholders.add("{" + name + "_next}");

        for (EnchantProperty property : EnchantProperty.values()) {
            placeholders.add("{" + name + "_" + property.name().toLowerCase() + "}");
            placeholders.add("{" + name + "_next_" + property.name().toLowerCase() + "}");
        }

        return placeholders;
    }

    public List<String> getReplacers(@NotNull ItemStack item) {
        List<String> replacers = new ArrayList<>();

        int quality = FarmSwordUtils.getItemQuality(item);
        int level = FarmSwordUtils.getEnchantmentLevel(item, this);
        int nextLevel = level + 1;

        replacers.add(NumberFormatter.getInstance().convertToRoman(level));
        replacers.add(NumberFormatter.getInstance().convertToRoman(nextLevel));

        for (EnchantProperty property : EnchantProperty.values()) {
            replacers.add(NumberFormatter.getInstance().formatDecimal(FarmSwordUtils.getEnchantEffectByLevel(this, property, level, quality)));
            replacers.add(NumberFormatter.getInstance().formatDecimal(FarmSwordUtils.getEnchantEffectByLevel(this, property, nextLevel, quality)));
        }

        return replacers;
    }
}