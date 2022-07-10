package com.zpedroo.farmsword.utils.menu;

import com.google.common.collect.Lists;
import com.zpedroo.farmsword.managers.DataManager;
import com.zpedroo.farmsword.objects.Enchant;
import com.zpedroo.farmsword.utils.FileUtils;
import com.zpedroo.farmsword.utils.builder.InventoryBuilder;
import com.zpedroo.farmsword.utils.builder.InventoryUtils;
import com.zpedroo.farmsword.utils.builder.ItemBuilder;
import com.zpedroo.farmsword.utils.color.Colorize;
import com.zpedroo.farmsword.utils.farmsword.FarmSwordUtils;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;

import static com.zpedroo.farmsword.utils.config.Settings.QUALITY_CURRENCY;

public class Menus extends InventoryUtils {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    public Menus() {
        instance = this;
    }

    public void openUpgradeMenu(Player player, ItemStack itemToUpgrade) {
        FileUtils.Files file = FileUtils.Files.UPGRADE;
        FileConfiguration fileConfiguration = FileUtils.get().getFile(file).get();

        String title = Colorize.getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String items : FileUtils.get().getSection(file, "Inventory.items")) {
            String action = FileUtils.get().getString(file, "Inventory.items." + items + ".action");
            String[] split = action.split(":");
            String upgradeElementName = split.length > 1 ? split[1] : "NULL";
            Enchant enchant = DataManager.getInstance().getEnchantByName(upgradeElementName);
            ItemStack item = buildUpgradeItem(player, itemToUpgrade, fileConfiguration, items, action, enchant);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + items + ".slot");

            inventory.addItem(item, slot, () -> {
                if (!StringUtils.containsIgnoreCase(action, "UPGRADE:")) return;

                ItemStack newItem = null;
                switch (upgradeElementName.toUpperCase()) {
                    case "QUALITY":
                        if (!FarmSwordUtils.canUpgradeQuality(player, itemToUpgrade)) {
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                            return;
                        }

                        newItem = FarmSwordUtils.upgradeQuality(player, itemToUpgrade);
                        break;
                    default:
                        if (enchant == null) return;
                        if (!FarmSwordUtils.canUpgradeEnchant(itemToUpgrade, enchant)) {
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                            return;
                        }

                        newItem = FarmSwordUtils.upgradeEnchantment(itemToUpgrade, enchant);
                        break;
                }

                openUpgradeMenu(player, newItem);
                player.setItemInHand(newItem);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 4f);
            }, ActionType.ALL_CLICKS);
        }

        int itemSlot = FileUtils.get().getInt(file, "Inventory.item-slot");
        if (itemSlot != -1) {
            inventory.addItem(itemToUpgrade, itemSlot);
        }

        inventory.open(player);
    }

    @NotNull
    private ItemStack buildUpgradeItem(Player player, ItemStack itemToUpgrade, FileConfiguration fileConfiguration, String items, String action, Enchant enchant) {
        ItemStack item = null;
        String toGet = getElementToGet(player, itemToUpgrade, action, enchant);

        if (fileConfiguration.contains("Inventory.items." + items + "." + toGet)) {
            String[] placeholders = getUpgradePlaceholders();
            String[] replacers = getUpgradeReplacers(itemToUpgrade, enchant);

            item = ItemBuilder.build(fileConfiguration, "Inventory.items." + items + "." + toGet, placeholders, replacers).build();
        } else {
            item = ItemBuilder.build(fileConfiguration, "Inventory.items." + items).build();
        }

        return item;
    }

    @Nullable
    private String getElementToGet(Player player, ItemStack itemToUpgrade, String action, Enchant enchant) {
        String toGet = null;
        if (enchant != null) {
            toGet = getItemEnchantStatus(itemToUpgrade, enchant);
        } else if (StringUtils.containsIgnoreCase(action, "QUALITY")) {
            toGet = getItemQualityStatus(player, itemToUpgrade);
        }

        return toGet;
    }

    private String[] getUpgradePlaceholders() {
        List<String> placeholders = Lists.newArrayList(FarmSwordUtils.getPlaceholders());
        placeholders.add("{cost}");
        placeholders.add("{required_level}");

        return placeholders.toArray(new String[0]);
    }

    private String[] getUpgradeReplacers(@NotNull ItemStack item, @Nullable Enchant enchant) {
        List<String> replacers = Lists.newArrayList(FarmSwordUtils.getReplacers(item));
        BigInteger upgradeCost;
        int upgradeLevelRequired;
        if (enchant == null) {
            upgradeCost = FarmSwordUtils.getQualityUpgradeCost(item);
            upgradeLevelRequired = FarmSwordUtils.getQualityUpgradeLevelRequired(item);

            replacers.add(QUALITY_CURRENCY == null ? NumberFormatter.formatThousand(upgradeCost.doubleValue()) : QUALITY_CURRENCY.getAmountDisplay(upgradeCost));
            replacers.add(NumberFormatter.formatThousand(upgradeLevelRequired));
        } else {
            upgradeCost = BigInteger.valueOf(FarmSwordUtils.getEnchantUpgradeCost(item, enchant));
            upgradeLevelRequired = FarmSwordUtils.getEnchantUpgradeLevelRequired(item, enchant);

            replacers.add(NumberFormatter.formatThousand(upgradeCost.doubleValue()));
            replacers.add(NumberFormatter.formatThousand(upgradeLevelRequired));
        }

        return replacers.toArray(new String[0]);
    }

    private String getItemEnchantStatus(@NotNull ItemStack item, @Nullable Enchant enchant) {
        if (enchant == null) return "undefined";
        if (FarmSwordUtils.isMaxEnchantLevel(item, enchant)) {
            return "max-level";
        } else if (!FarmSwordUtils.isUnlockedEnchant(item, enchant)) {
            return "locked";
        } else if (!FarmSwordUtils.canUpgradeEnchant(item, enchant)) {
            return "can-not-upgrade";
        }

        return "can-upgrade";
    }

    private String getItemQualityStatus(@Nullable Player player, @NotNull ItemStack item) {
        if (FarmSwordUtils.isMaxQualityLevel(item)) {
            return "max-level";
        } else if (!FarmSwordUtils.isUnlockedQuality(item)) {
            return "locked";
        } else if (!FarmSwordUtils.canUpgradeQuality(player, item)) {
            return "can-not-upgrade";
        }

        return "can-upgrade";
    }
}