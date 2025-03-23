package rpg.rpg_base.GeneralEvents;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.Data.DataBaseColumn;
import rpg.rpg_base.Data.DataBaseManager;
import rpg.rpg_base.Crafting.CraftingGui;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
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

        if (DataBaseManager.getValueOfCell(DataBaseColumn.LVL, player.getPlayer().getUniqueId().toString()) != null) {
            player.level = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.LVL, player.getPlayer().getUniqueId().toString()));
        } else {
            player.level = 0;
        }
        if (DataBaseManager.getValueOfCell(DataBaseColumn.ELVL, player.getPlayer().getUniqueId().toString()) != null) {
            player.playerSkills.enduranceLvl = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.ELVL, player.getPlayer().getUniqueId().toString()));
        } else {
            player.playerSkills.enduranceLvl = 0;
        }
        if (DataBaseManager.getValueOfCell(DataBaseColumn.SLVL, player.getPlayer().getUniqueId().toString()) != null) {
            player.playerSkills.strengthLvl = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.SLVL, player.getPlayer().getUniqueId().toString()));
        } else {
            player.playerSkills.strengthLvl = 0;
        }

        MoneyManager.setPlayerGold(player.getPlayer(), 0);
        MoneyManager.setPlayerRunicSigils(player.getPlayer(), 0);
        MoneyManager.setPlayerGuildMedals(player.getPlayer(), 0);

        if (DataBaseManager.getValueOfCell(DataBaseColumn.GOLD, player.getPlayer().getUniqueId().toString()) != null) {
            MoneyManager.setPlayerGold(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.GOLD, player.getPlayer().getUniqueId().toString())));
        } else {
            MoneyManager.setPlayerGold(player.getPlayer(), 50);
        }
        if (DataBaseManager.getValueOfCell(DataBaseColumn.RUNICSIGILS, player.getPlayer().getUniqueId().toString()) != null) {
            MoneyManager.setPlayerRunicSigils(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.RUNICSIGILS, player.getPlayer().getUniqueId().toString())));
        } else {
            MoneyManager.setPlayerRunicSigils(player.getPlayer(), 0);
        }
        if (DataBaseManager.getValueOfCell(DataBaseColumn.GUILDMEDALS, player.getPlayer().getUniqueId().toString()) != null) {
            MoneyManager.setPlayerGuildMedals(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.GUILDMEDALS, player.getPlayer().getUniqueId().toString())));
        } else {
            MoneyManager.setPlayerGuildMedals(player.getPlayer(), 0);
        }
        if (DataBaseManager.getValueOfCell(DataBaseColumn.XP, player.getPlayer().getUniqueId().toString()) != null) {
            player.xp = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.XP, player.getPlayer().getUniqueId().toString()));
        } else {
            player.xp = 0;
        }
        if (DataBaseManager.getValueOfCell(DataBaseColumn.TOTALXP, player.getPlayer().getUniqueId().toString()) != null) {
            player.totalXp = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.TOTALXP, player.getPlayer().getUniqueId().toString()));
        } else {
            player.totalXp = 0;
        }


        player.updateStats();
        player.currentHP = player.maxHP;

        CPlayer.customPlayer.put(player.getPlayer().getUniqueId(), player);
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        CPlayer cPlayer = CPlayer.customPlayer.get(event.getPlayer().getUniqueId());

        DataBaseManager.addColumnValue(DataBaseColumn.LVL, cPlayer.level, cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.ELVL, cPlayer.playerSkills.enduranceLvl, cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.SLVL, cPlayer.playerSkills.strengthLvl, cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.XP, cPlayer.xp, cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.TOTALXP, cPlayer.totalXp, cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.USERNAME, cPlayer.getPlayer().getName(), cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.GOLD, MoneyManager.getPlayerGold(cPlayer.getPlayer()), cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.RUNICSIGILS, MoneyManager.getPlayerRunicSigils(cPlayer.getPlayer()) , cPlayer.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.GUILDMEDALS, MoneyManager.getPlayerGuildMedals(cPlayer.getPlayer()) , cPlayer.getPlayer().getUniqueId().toString());

        cPlayer.regenTask.runTaskTimer(plugin, 10, 40);
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

                switch (e.getCause()) {
                    case ENTITY_SWEEP_ATTACK -> damagedEntity.dealDamage(player.damage / 3, e.getDamager());
                    case ENTITY_ATTACK -> damagedEntity.dealDamage(player.damage, e.getDamager());
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
        Profile profile = PlayerConverter.getID(event.getPlayer());
        
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
