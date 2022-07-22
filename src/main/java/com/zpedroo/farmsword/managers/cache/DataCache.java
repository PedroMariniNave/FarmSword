package com.zpedroo.farmsword.managers.cache;

import com.zpedroo.farmsword.enums.EnchantProperty;
import com.zpedroo.farmsword.objects.Enchant;
import com.zpedroo.farmsword.objects.FarmMob;
import com.zpedroo.farmsword.utils.FileUtils;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.EntityType;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DataCache {

    private final Map<String, Enchant> enchants = getEnchantsFromConfig();
    private final Map<EntityType, FarmMob> damageMobs = getDamageMobsFromConfig();
    private final Map<EntityType, FarmMob> killMobs = getKillMobsFromConfig();

    private Map<String, Enchant> getEnchantsFromConfig() {
        FileUtils.Files file = FileUtils.Files.CONFIG;
        Map<String, Enchant> ret = new HashMap<>(4);
        for (String enchantName : FileUtils.get().getSection(file, "Enchants")) {
            Enchant enchant = loadEnchant(enchantName, file);
            ret.put(enchantName.toUpperCase(), enchant);
        }

        return ret;
    }

    private Map<EntityType, FarmMob> getDamageMobsFromConfig() {
        FileUtils.Files file = FileUtils.Files.CONFIG;
        Map<EntityType, FarmMob> ret = new HashMap<>(4);
        for (String str : FileUtils.get().getStringList(file, "Damage-Mobs")) {
            FarmMob farmMob = loadFarmMob(str);
            if (farmMob == null) continue;

            EntityType entityType = farmMob.getEntityType();
            ret.put(entityType, farmMob);
        }

        return ret;
    }

    private Map<EntityType, FarmMob> getKillMobsFromConfig() {
        FileUtils.Files file = FileUtils.Files.CONFIG;
        Map<EntityType, FarmMob> ret = new HashMap<>(4);
        for (String str : FileUtils.get().getStringList(file, "Kill-Mobs")) {
            FarmMob farmMob = loadFarmMob(str);
            if (farmMob == null) continue;

            EntityType entityType = farmMob.getEntityType();
            ret.put(entityType, farmMob);
        }

        return ret;
    }

    private Enchant loadEnchant(String enchantName, FileUtils.Files file) {
        if (!FileUtils.get().getFile(file).get().contains("Enchants." + enchantName)) return null;

        int initialLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.initial");
        int maxLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.max");
        int requiredLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.requirement-per-upgrade");
        BigInteger costPerLevel = NumberFormatter.getInstance().filter(FileUtils.get().getString(file, "Enchants." + enchantName + ".cost-per-level", "0"));
        double multiplierPerLevel = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".multiplier-per-level");

        Map<EnchantProperty, Number> enchantProperties = new HashMap<>(EnchantProperty.values().length);
        enchantProperties.put(EnchantProperty.MULTIPLIER, multiplierPerLevel);

        return new Enchant(enchantName, initialLevel, maxLevel, requiredLevel, costPerLevel, enchantProperties);
    }

    @Nullable
    private FarmMob loadFarmMob(String str) {
        String[] split = str.split(",");
        if (split.length <= 2) return null;

        EntityType entityType = getEntityTypeByName(split[0].toUpperCase());
        if (entityType == null) return null;

        double expAmount = Double.parseDouble(split[1]);
        BigInteger pointsAmount = NumberFormatter.getInstance().filter(split[2]);

        return new FarmMob(entityType, expAmount, pointsAmount);
    }

    private EntityType getEntityTypeByName(String name) {
        for (EntityType entityType : EntityType.values()) {
            if (StringUtils.equalsIgnoreCase(entityType.name(), name)) return entityType;
        }

        return null;
    }
}