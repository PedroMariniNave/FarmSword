package com.zpedroo.farmsword.listeners;

import com.zpedroo.farmsword.enums.EnchantProperty;
import com.zpedroo.farmsword.managers.DataManager;
import com.zpedroo.farmsword.objects.Enchant;
import com.zpedroo.farmsword.objects.FarmMob;
import com.zpedroo.farmsword.utils.config.Messages;
import com.zpedroo.farmsword.utils.config.Settings;
import com.zpedroo.farmsword.utils.config.Titles;
import com.zpedroo.farmsword.utils.farmsword.FarmSwordUtils;
import com.zpedroo.farmsword.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class FarmSwordListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        ItemStack item = player.getItemInHand();
        if (!FarmSwordUtils.isFarmSword(item)) return;

        EntityType entityType = event.getEntity().getType();
        FarmMob farmMob = DataManager.getInstance().getDamageFarmMobByType(entityType);
        if (farmMob == null) return;

        final int oldLevel = FarmSwordUtils.getItemLevel(item);
        Enchant enchant = DataManager.getInstance().getEnchantByName("exp");
        double bonus = 1 + FarmSwordUtils.getEnchantEffectByItem(item, enchant, EnchantProperty.MULTIPLIER);
        double expAmount = farmMob.getExpAmount();
        double expToGive = expAmount * bonus;
        ItemStack newItem = FarmSwordUtils.addItemExperience(item, expToGive);
        int newLevel = FarmSwordUtils.getItemLevel(newItem);

        if (isNewLevel(oldLevel, newLevel)) {
            sendUpgradeTitle(player, oldLevel, newLevel);
        }

        player.setItemInHand(newItem);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        ItemStack item = player.getItemInHand();
        if (!FarmSwordUtils.isFarmSword(item)) return;

        EntityType entityType = event.getEntity().getType();
        FarmMob farmMob = DataManager.getInstance().getKillFarmMobByType(entityType);
        if (farmMob == null) return;

        double expAmount = farmMob.getExpAmount();
        ItemStack newItem = FarmSwordUtils.addItemExperience(item, expAmount);
        player.setItemInHand(newItem);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!Settings.PREVENT_PLAYER_HIT || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        ItemStack item = player.getItemInHand();
        if (!FarmSwordUtils.isFarmSword(item)) return;

        event.setCancelled(true);
        event.setDamage(0);

        player.sendMessage(Messages.PLAYER_HIT);
    }

    private void sendUpgradeTitle(Player player, int oldLevel, int newLevel) {
        String[] placeholders = new String[]{ "{old_level}", "{new_level}" };
        String[] replacers = new String[]{
                NumberFormatter.formatThousand(oldLevel), NumberFormatter.formatThousand(newLevel)
        };
        player.sendTitle(
                StringUtils.replaceEach(Titles.FARM_SWORD_UPGRADE[0], placeholders, replacers),
                StringUtils.replaceEach(Titles.FARM_SWORD_UPGRADE[1], placeholders, replacers)
        );
    }

    private boolean isNewLevel(int oldLevel, int newLevel) {
        return oldLevel != newLevel;
    }
}