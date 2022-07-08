package com.zpedroo.farmsword.managers.cache;

import com.zpedroo.farmsword.enums.EnchantProperty;
import com.zpedroo.farmsword.objects.Enchant;
import com.zpedroo.farmsword.objects.FarmMob;
import com.zpedroo.farmsword.utils.FileUtils;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.EntityType;

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
        String where = "Damage-Mobs-Exp";
        for (String mobName : FileUtils.get().getSection(file, where)) {
            FarmMob farmMob = loadFarmMob(where, mobName, file);
            if (farmMob == null) continue;

            EntityType entityType = farmMob.getEntityType();
            ret.put(entityType, farmMob);
        }

        return ret;
    }

    private Map<EntityType, FarmMob> getKillMobsFromConfig() {
        FileUtils.Files file = FileUtils.Files.CONFIG;
        Map<EntityType, FarmMob> ret = new HashMap<>(4);
        String where = "Kill-Mobs-Exp";
        for (String mobName : FileUtils.get().getSection(file, where)) {
            FarmMob farmMob = loadFarmMob(where, mobName, file);
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
        int costPerLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".cost-per-level");
        double multiplierPerLevel = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".multiplier-per-level");

        Map<EnchantProperty, Number> enchantProperties = new HashMap<>(EnchantProperty.values().length);
        enchantProperties.put(EnchantProperty.MULTIPLIER, multiplierPerLevel);

        return new Enchant(enchantName, initialLevel, maxLevel, requiredLevel, costPerLevel, enchantProperties);
    }

    private FarmMob loadFarmMob(String where, String mobName, FileUtils.Files file) {
        EntityType entityType = getEntityTypeByName(mobName);
        if (entityType == null) return null;

        double expAmount = FileUtils.get().getDouble(file, where + "." + mobName);

        return new FarmMob(entityType, expAmount);
    }

    private EntityType getEntityTypeByName(String name) {
        for (EntityType entityType : EntityType.values()) {
            if (StringUtils.equalsIgnoreCase(entityType.name(), name)) return entityType;
        }

        return null;
    }
}