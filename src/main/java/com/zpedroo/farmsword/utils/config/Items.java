package com.zpedroo.farmsword.utils.config;

import com.zpedroo.farmsword.managers.DataManager;
import com.zpedroo.farmsword.objects.Enchant;
import com.zpedroo.farmsword.utils.FileUtils;
import com.zpedroo.farmsword.utils.builder.ItemBuilder;
import com.zpedroo.farmsword.utils.farmsword.FarmSwordUtils;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Items {

    private static final ItemStack POINTS_ITEM = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Points-Item").build();
    private static final ItemStack FARM_SWORD_ITEM = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Farm-Sword-Item").build();

    @NotNull
    public static ItemStack getPointsItem(int amount) {
        NBTItem nbt = new NBTItem(POINTS_ITEM.clone());
        nbt.setInteger(FarmSwordUtils.POINTS_ITEM_NBT, amount);

        String[] placeholders = new String[]{
                "{amount}"
        };
        String[] replacers = new String[]{
                NumberFormatter.getInstance().formatThousand(amount)
        };

        return replaceItemPlaceholders(nbt.getItem(), placeholders, replacers);
    }

    @NotNull
    public static ItemStack getFarmSwordItem() {
        NBTItem nbt = new NBTItem(FARM_SWORD_ITEM.clone());
        nbt.setBoolean(FarmSwordUtils.IDENTIFIER_NBT, true);

        ItemStack item = nbt.getItem();
        return replaceItemPlaceholders(item, FarmSwordUtils.getPlaceholders(), FarmSwordUtils.getReplacers(item));
    }

    @NotNull
    public static ItemStack getFarmSwordItem(@NotNull ItemStack baseItem) {
        NBTItem nbt = new NBTItem(FARM_SWORD_ITEM.clone());
        nbt.setBoolean(FarmSwordUtils.IDENTIFIER_NBT, true);

        for (Enchant enchant : DataManager.getInstance().getEnchants()) {
            int level = FarmSwordUtils.getEnchantmentLevel(baseItem, enchant);
            if (level <= enchant.getInitialLevel()) continue;

            String enchantName = enchant.getName();
            nbt.setInteger(enchantName, level);
            Enchantment enchantment = Enchantment.getByName(enchantName.toUpperCase());
            if (enchantment != null) {
                int finalLevel = FarmSwordUtils.applyQualityBonus(baseItem, level);
                nbt.getItem().addUnsafeEnchantment(enchantment, finalLevel);
            }
        }

        nbt.setDouble(FarmSwordUtils.EXPERIENCE_NBT, FarmSwordUtils.getItemExperience(baseItem));
        nbt.setString(FarmSwordUtils.FARM_SWORD_POINTS_NBT, FarmSwordUtils.getItemPoints(baseItem).toString());
        nbt.setInteger(FarmSwordUtils.QUALITY_NBT, FarmSwordUtils.getItemQuality(baseItem));

        ItemStack item = nbt.getItem();
        return replaceItemPlaceholders(item, FarmSwordUtils.getPlaceholders(), FarmSwordUtils.getReplacers(item));
    }

    @NotNull
    private static ItemStack replaceItemPlaceholders(ItemStack item, String[] placeholders, String[] replacers) {
        if (item.getItemMeta() != null) {
            String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
            ItemMeta meta = item.getItemMeta();
            if (displayName != null) meta.setDisplayName(StringUtils.replaceEach(displayName, placeholders, replacers));
            if (lore != null) {
                List<String> newLore = new ArrayList<>(lore.size());
                for (String str : lore) {
                    newLore.add(StringUtils.replaceEach(str, placeholders, replacers));
                }

                meta.setLore(newLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}