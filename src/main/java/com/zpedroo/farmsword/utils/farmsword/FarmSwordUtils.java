package com.zpedroo.farmsword.utils.farmsword;

import com.zpedroo.farmsword.enums.EnchantProperty;
import com.zpedroo.farmsword.managers.DataManager;
import com.zpedroo.farmsword.objects.Enchant;
import com.zpedroo.farmsword.utils.config.Items;
import com.zpedroo.farmsword.utils.config.Quality;
import com.zpedroo.farmsword.utils.config.Settings;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import com.zpedroo.farmsword.utils.formula.ExperienceManager;
import com.zpedroo.farmsword.utils.progress.ProgressConverter;
import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.general.Currency;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FarmSwordUtils {

    public static final String POINTS_ITEM_NBT = "FarmSwordPointsItem";
    public static final String FARM_SWORD_POINTS_NBT = "FarmSwordPoints";
    public static final String EXPERIENCE_NBT = "FarmSwordExperience";
    public static final String QUALITY_NBT = "FarmSwordQuality";
    public static final String IDENTIFIER_NBT = "FarmSword";

    public static ItemStack addItemPoints(@NotNull ItemStack item, BigInteger amount) {
        return setItemPoints(item, getItemPoints(item).add(amount));
    }

    public static ItemStack removeItemPoints(@NotNull ItemStack item, BigInteger amount) {
        return setItemPoints(item, getItemPoints(item).subtract(amount));
    }

    public static ItemStack setItemPoints(@NotNull ItemStack item, BigInteger amount) {
        NBTItem nbt = new NBTItem(item);
        nbt.setString(FARM_SWORD_POINTS_NBT, amount.toString());

        return Items.getFarmSwordItem(nbt.getItem());
    }

    public static ItemStack addItemExperience(@NotNull ItemStack item, double amount) {
        return setItemExperience(item, getItemExperience(item) + amount);
    }

    public static ItemStack setItemExperience(@NotNull ItemStack item, double amount) {
        NBTItem nbt = new NBTItem(item);
        nbt.setDouble(EXPERIENCE_NBT, amount);

        return Items.getFarmSwordItem(nbt.getItem());
    }

    public static int getItemLevel(@NotNull ItemStack item) {
        return ExperienceManager.getLevel(getItemExperience(item));
    }

    public static double getItemExperience(@NotNull ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey(EXPERIENCE_NBT)) return 0;

        return nbt.getDouble(EXPERIENCE_NBT);
    }

    public static int getItemQuality(@NotNull ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey(QUALITY_NBT)) return Quality.INITIAL;

        return nbt.getInteger(QUALITY_NBT);
    }

    public static BigInteger getItemPoints(@NotNull ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey(FARM_SWORD_POINTS_NBT)) return BigInteger.ZERO;

        return new BigInteger(nbt.getString(FARM_SWORD_POINTS_NBT));
    }

    public static double getPropertyValue(Enchant enchant, EnchantProperty property) {
        return enchant.getPropertyEffect(property).doubleValue();
    }

    public static double getEnchantEffectByItem(@NotNull ItemStack item, Enchant enchant, EnchantProperty property) {
        return getEnchantEffectByLevel(enchant, property, getEnchantmentLevel(item, enchant), getItemQuality(item));
    }

    public static double getEnchantEffectByLevel(@Nullable Enchant enchant, @Nullable EnchantProperty property, int level, int quality) {
        if (enchant == null || property == null) return 0;

        double propertyValue = getPropertyValue(enchant, property);
        double qualityBonus = getFinalQualityBonus(quality);

        return level * propertyValue * qualityBonus;
    }

    public static int getEnchantmentLevel(@NotNull ItemStack item, @Nullable Enchant enchant) {
        if (enchant == null) return 0;

        String enchantName = enchant.getName();
        Enchantment enchantment = Enchantment.getByName(enchantName.toUpperCase());
        if (enchantment != null) {
            int level = item.getEnchantmentLevel(enchantment);
            return reverseQualityBonus(item, level);
        }

        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey(enchant.getName())) return enchant.getInitialLevel();

        return nbt.getInteger(enchant.getName());
    }

    public static ItemStack addEnchantmentLevel(@NotNull ItemStack item, @NotNull Enchant enchant, int level) {
        return setEnchantmentLevel(item, enchant, getEnchantmentLevel(item, enchant) + level);
    }

    public static ItemStack setEnchantmentLevel(@NotNull ItemStack item, Enchant enchant, int level) {
        NBTItem nbt = new NBTItem(item);
        String enchantName = enchant.getName();
        nbt.setInteger(enchantName, level);
        Enchantment enchantment = Enchantment.getByName(enchantName.toUpperCase());
        final ItemStack nbtItem = nbt.getItem();
        if (enchantment != null) {
            int finalLevel = applyQualityBonus(item, level);
            nbtItem.addUnsafeEnchantment(enchantment, finalLevel);
        }

        return Items.getFarmSwordItem(nbtItem);
    }

    public static BigInteger getEnchantUpgradeCost(@NotNull ItemStack item, @Nullable Enchant enchant) {
        if (enchant == null) return BigInteger.ZERO;

        int enchantLevel = getEnchantmentLevel(item, enchant);
        int nextLevel = enchantLevel + 1;
        BigInteger costPerLevel = enchant.getCostPerLevel();

        return BigInteger.valueOf(enchantLevel).multiply(costPerLevel);
    }

    public static int getEnchantUpgradeLevelRequired(@NotNull ItemStack item, @Nullable Enchant enchant) {
        if (enchant == null) return 0;

        int enchantLevel = getEnchantmentLevel(item, enchant);
        int nextLevel = enchantLevel + 1;
        int levelPerUpgrade = enchant.getRequiredLevel();

        return nextLevel * levelPerUpgrade;
    }

    public static ItemStack upgradeEnchantment(@NotNull ItemStack item, @NotNull Enchant enchant) {
        BigInteger upgradeCost = getEnchantUpgradeCost(item, enchant);
        item = removeItemPoints(item, upgradeCost);
        item = addEnchantmentLevel(item, enchant, 1);

        return item;
    }

    public static ItemStack addQuality(@NotNull ItemStack item, int amount) {
        return setItemQuality(item, getItemQuality(item) + amount);
    }

    public static ItemStack setItemQuality(@NotNull ItemStack item, int quality) {
        NBTItem nbt = new NBTItem(item);
        nbt.setInteger(QUALITY_NBT, quality);

        return Items.getFarmSwordItem(nbt.getItem());
    }

    public static BigInteger getQualityUpgradeCost(@NotNull ItemStack item) {
        int quality = getItemQuality(item);
        int nextLevel = quality + 1;
        BigInteger costPerLevel = Quality.COST_PER_QUALITY;

        return BigInteger.valueOf(nextLevel).multiply(costPerLevel);
    }

    public static int getQualityUpgradeLevelRequired(@NotNull ItemStack item) {
        int quality = getItemQuality(item);
        int nextLevel = quality + 1;
        int levelPerUpgrade = Quality.ITEM_LEVEL_PER_QUALITY;

        return nextLevel * levelPerUpgrade;
    }

    public static double getQualityBonus(@NotNull ItemStack item) {
        return getQualityBonus(getItemQuality(item));
    }

    public static double getQualityBonus(int quality) {
        return quality * Quality.BONUS_PER_QUALITY;
    }

    public static double getFinalQualityBonus(ItemStack item) {
        return getFinalQualityBonus(getItemQuality(item));
    }

    public static double getFinalQualityBonus(int quality) {
        return 1 + (quality * Quality.BONUS_PER_QUALITY);
    }

    public static ItemStack upgradeQuality(@Nullable Player player, @NotNull ItemStack item) {
        BigInteger upgradeCost = getQualityUpgradeCost(item);
        Currency currency = Settings.QUALITY_CURRENCY;
        item = addQuality(item, 1);
        if (currency != null && player != null) {
            CurrencyAPI.removeCurrencyAmount(player.getUniqueId(), currency, upgradeCost);
            return item;
        }

        item = removeItemPoints(item, upgradeCost);
        return item;
    }

    public static boolean isFarmSword(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) return false;

        NBTItem nbt = new NBTItem(item);
        return nbt.hasKey(IDENTIFIER_NBT);
    }

    public static boolean isUnlockedEnchant(ItemStack item, Enchant enchant) {
        int itemLevel = getItemLevel(item);
        int requiredLevel = getEnchantUpgradeLevelRequired(item, enchant);

        return itemLevel >= requiredLevel;
    }

    public static boolean canUpgradeEnchant(ItemStack item, Enchant enchant) {
        if (!isUnlockedEnchant(item, enchant) || isMaxEnchantLevel(item, enchant)) return false;

        BigInteger itemPointsAmount = getItemPoints(item);
        BigInteger upgradeCost = getEnchantUpgradeCost(item, enchant);

        return itemPointsAmount.compareTo(upgradeCost) >= 0;
    }

    public static boolean isUnlockedQuality(ItemStack item) {
        int itemLevel = getItemLevel(item);
        int requiredLevel = getQualityUpgradeLevelRequired(item);

        return itemLevel >= requiredLevel;
    }

    public static int applyQualityBonus(@NotNull ItemStack item, int level) {
        return (int) (level * getFinalQualityBonus(item));
    }

    public static int reverseQualityBonus(@NotNull ItemStack item, int level) {
        double qualityBonus = getFinalQualityBonus(item);

        return (int) (level / qualityBonus);
    }

    public static boolean canUpgradeQuality(@NotNull ItemStack item) {
        return canUpgradeQuality(null, item);
    }

    public static boolean canUpgradeQuality(@Nullable Player player, @NotNull ItemStack item) {
        if (!isUnlockedQuality(item) || isMaxQualityLevel(item)) return false;

        Currency currency = Settings.QUALITY_CURRENCY;
        BigInteger upgradeCost = getQualityUpgradeCost(item);
        if (currency != null && player != null) {
            BigInteger currencyAmount = CurrencyAPI.getCurrencyAmount(player.getUniqueId(), currency);

            return currencyAmount.compareTo(upgradeCost) >= 0;
        }

        BigInteger itemPointsAmount = getItemPoints(item);
        return itemPointsAmount.compareTo(upgradeCost) >= 0;
    }

    public static boolean isMaxEnchantLevel(ItemStack item, Enchant enchant) {
        return getEnchantmentLevel(item, enchant) >= enchant.getMaxLevel();
    }

    public static boolean isMaxQualityLevel(ItemStack item) {
        return getItemQuality(item) >= Quality.MAX;
    }

    public static String[] getPlaceholders() {
        Collection<Enchant> enchants = DataManager.getInstance().getEnchants();
        List<String> placeholders = new ArrayList<>();

        for (Enchant enchant : enchants) {
            placeholders.addAll(enchant.getPlaceholders());
        }

        placeholders.add("{points}");
        placeholders.add("{level}");
        placeholders.add("{percentage}");
        placeholders.add("{progress}");
        placeholders.add("{quality}");
        placeholders.add("{quality_next}");
        placeholders.add("{quality_bonus}");
        placeholders.add("{quality_next_bonus}");

        return placeholders.toArray(new String[0]);
    }

    public static String[] getReplacers(@NotNull ItemStack item) {
        Collection<Enchant> enchants = DataManager.getInstance().getEnchants();
        List<String> replacers = new ArrayList<>(enchants.size());

        for (Enchant enchant : enchants) {
            replacers.addAll(enchant.getReplacers(item));
        }

        int itemQuality = getItemQuality(item);
        int nextItemQuality = itemQuality + 1;

        replacers.add(NumberFormatter.getInstance().format(getItemPoints(item)));
        replacers.add(NumberFormatter.getInstance().formatThousand(getItemLevel(item)));
        replacers.add(NumberFormatter.getInstance().formatDecimal(ProgressConverter.getPercentage(getItemExperience(item))));
        replacers.add(ProgressConverter.convertExperience(getItemExperience(item)));
        replacers.add(ProgressConverter.convertQuality(itemQuality));
        replacers.add(ProgressConverter.convertQuality(nextItemQuality));
        replacers.add(NumberFormatter.getInstance().formatDecimal(getQualityBonus(itemQuality)));
        replacers.add(NumberFormatter.getInstance().formatDecimal(getQualityBonus(nextItemQuality)));

        return replacers.toArray(new String[0]);
    }
}