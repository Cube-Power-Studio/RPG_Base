package rpg.rpg_base.GeneralEvents;

import net.citizensnpcs.api.CitizensAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import rpg.rpg_base.Crafting.CraftingGui;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.Data.PlayerDataManager;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.RPG_Base;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Events implements Listener {
    private final RPG_Base plugin;
    private final GUIManager guiManager;

    public Events(RPG_Base plugin, GUIManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event){
        CPlayer player = new CPlayer(event.getPlayer());

        CPlayer.customPlayer.put(player.getPlayer().getUniqueId(), player);

        PlayerDataManager.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        PlayerDataManager.savePlayerData(event.getPlayer());
        CPlayer.customPlayer.remove(event.getPlayer().getUniqueId());
    }

    int fireCount = 0;
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e){
        if(fireCount <= 0 ){
            Object killer = CPlayer.getPlayerByUUID(e.getPlayer().getUniqueId()).killer;
            if(killer instanceof CEntity entity) {
                String mobType = entity.name;
                int entityLevel = entity.level;

                Component playerName = Component.text(e.getPlayer().getName(), NamedTextColor.WHITE);
                Component killedBy = Component.text(" was killed by ", NamedTextColor.GRAY);
                Component mobName = Component.text(mobType, NamedTextColor.RED);
                Component levelLabel = Component.text(" lvl ", NamedTextColor.GRAY);
                Component level = Component.text("[" + entityLevel + "]", NamedTextColor.GOLD);

                // Concatenate the components
                Component deathMessage = playerName
                        .append(killedBy)
                        .append(mobName)
                        .append(levelLabel)
                        .append(level);
                e.deathMessage(deathMessage);

            } else if(killer instanceof  CPlayer player) {
                String playerName = player.getPlayer().getName();
                int playerLevel = player.level;

                Component killedPlayerName = Component.text(e.getPlayer().getName(), NamedTextColor.WHITE);
                Component killedBy = Component.text(" was killed by ", NamedTextColor.GRAY);
                Component killerName = Component.text(playerName, NamedTextColor.RED);
                Component levelLabel = Component.text(" lvl ", NamedTextColor.GRAY);
                Component level = Component.text("[" + playerLevel + "]", NamedTextColor.GOLD);

                Component deathMessage = killedPlayerName
                        .append(killedBy)
                        .append(killerName)
                        .append(levelLabel)
                        .append(level);
                e.deathMessage(deathMessage);
            }
            fireCount++;
        }else{
            e.deathMessage(null);
            fireCount = 0;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        if(!(e.getEntity() instanceof Player)) {
            CEntity damagedEntity = CEntity.getEntityByUUID(e.getEntity().getUniqueId());
            if (CitizensAPI.getNPCRegistry().isNPC(damagedEntity.getEntity())) {
                e.setCancelled(true);
            } else if (e.getDamager() instanceof Player) {
                CPlayer player = CPlayer.getPlayerByUUID(e.getDamager().getUniqueId());

                int damage = player.damage;

                switch (e.getCause()) {
                    case ENTITY_SWEEP_ATTACK -> damagedEntity.dealDamage(damage / 3, e.getDamager());
                    case ENTITY_ATTACK -> damagedEntity.dealDamage(damage, e.getDamager());
                }
            } else if (e.getDamager() instanceof Mob) {
                CEntity damagerEntity = CEntity.getEntityByUUID(e.getDamager().getUniqueId());

                damagedEntity.dealDamage(damagerEntity.damage, e.getDamager());
                damagerEntity.updateDisplayName();
            } else {
                e.setCancelled(true);
            }
        }else{
            CPlayer damagedPlayer = CPlayer.getPlayerByUUID(e.getEntity().getUniqueId());

            if(e.getDamager() instanceof Player){
                CPlayer player = CPlayer.getPlayerByUUID(e.getDamager().getUniqueId());

                switch (e.getCause()) {
                    case ENTITY_SWEEP_ATTACK -> damagedPlayer.dealDamage(player.damage / 3, player);
                    case ENTITY_ATTACK -> damagedPlayer.dealDamage(player.damage, player);
                }
            }else if(e.getDamager() instanceof Mob){
                CEntity damager = CEntity.getEntityByUUID(e.getDamager().getUniqueId());
                damagedPlayer.dealDamage(damager.damage, damager);
            }
        }
        e.setDamage(0);
    }

    @EventHandler
    private void onItemDamage(PlayerItemDamageEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event){
        Profile profile = BetonQuest.getInstance().getProfileProvider().getProfile(event.getPlayer());
        
        if(Conversation.getConversation(profile) != null){
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockClick(PlayerInteractEvent event){
        if(event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE)){
            event.setCancelled(true);
            this.guiManager.openGui(new CraftingGui(plugin), event.getPlayer());
        }

    }

    @EventHandler
    private void onInvOpen(InventoryOpenEvent event){
        ItemManager.updateItems(event.getInventory());
    }

    @EventHandler
    private void onRecipeUnlocked(PlayerRecipeDiscoverEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    private void onInventoryCraftingClick(InventoryClickEvent e){
        if(e.getInventory().getType() == InventoryType.PLAYER){
            List<Integer> craftingSlots = List.of(80, 81, 82, 83);
            if(craftingSlots.contains(e.getSlot())){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryCraftingDrag(InventoryDragEvent e) {
        if (e.getInventory().getType() == InventoryType.PLAYER) {
            List<Integer> craftingSlots = Arrays.asList(80, 81, 82, 83);

            // Get the set of raw slots involved in the drag event
            Set<Integer> draggedSlots = e.getRawSlots();

            // Check if any of the dragged slots are in the crafting area
            for (int slot : draggedSlots) {
                if (craftingSlots.contains(slot)) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
}
