package rpg.rpg_base.StatManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.RPG_Base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static rpg.rpg_base.StatManager.HealthManager.healthRegen;

public class StatUpdates extends BukkitRunnable {
    private static RPG_Base plugin;

    public StatUpdates(RPG_Base plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Player player: Bukkit.getOnlinePlayers()){
            ItemHandlers.setEntityItem(player.getUniqueId(), player.getInventory().getContents());
            ItemStack[] itemList = ItemHandlers.getEntityItem(player.getUniqueId());
            List<ItemStack> countedItems = new ArrayList<>();
            int itemDamage = 0;
            int itemHealth = 0;
            for(ItemStack item:itemList){
                if(item!=null) {
                    if(!countedItems.contains(item)) {
                        if (Arrays.asList(player.getInventory().getArmorContents()).contains(item) || player.getInventory().getItemInMainHand().equals(item)) {
                            countedItems.add(item);
                            itemDamage += ItemHandlers.getItemDamage(item);
                            itemHealth += ItemHandlers.getItemHealth(item);
                        }
                    }
                }
            }
            DamageManager.setPlayerDamage(player, itemDamage + StrengthManager.getStrength_dmg(player));
            HealthManager.setPlayerMaxHealth(player, itemHealth + EnduranceManager.getEndurance_hp(player)+100);
            if (HealthManager.getPlayerHealth(player) < HealthManager.getPlayerMaxHealth(player)) {
                HealthRegen healthRegen = new HealthRegen(player);
                if (!healthRegen.isRunning()) {
                    healthRegen.runTaskTimer(plugin, 0, 20);  // Schedule the task to run every second (20 ticks)
                    healthRegen.setScheduled(true);
                }
            }
            if (HealthManager.getPlayerHealth(player) > HealthManager.getPlayerMaxHealth(player)) {
                HealthManager.setPlayerHealth(player, HealthManager.getPlayerMaxHealth(player));
            }
        }
    }
}
