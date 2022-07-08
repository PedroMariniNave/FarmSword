package com.zpedroo.farmsword.managers;

import com.zpedroo.farmsword.managers.cache.DataCache;
import com.zpedroo.farmsword.objects.Enchant;
import com.zpedroo.farmsword.objects.FarmMob;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DataManager {

    private static DataManager instance;
    public static DataManager getInstance() { return instance; }

    private final DataCache dataCache = new DataCache();

    public DataManager() {
        instance = this;
    }

    @Nullable
    public Enchant getEnchantByName(String enchantName) {
        return dataCache.getEnchants().get(enchantName.toUpperCase());
    }

    @Nullable
    public FarmMob getDamageFarmMobByType(EntityType entityType) {
        return dataCache.getDamageMobs().get(entityType);
    }

    @Nullable
    public FarmMob getKillFarmMobByType(EntityType entityType) {
        return dataCache.getKillMobs().get(entityType);
    }

    public Collection<Enchant> getEnchants() {
        return dataCache.getEnchants().values();
    }

    public DataCache getCache() {
        return dataCache;
    }
}