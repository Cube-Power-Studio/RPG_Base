/*******************************************************************************
 * This file is part of RPG_Base.
 *
 *     RPG_Base is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     RPG_Base is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with RPG_Base.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package rpg.rpg_base.GeneralEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import rpg.rpg_base.IslandManager.InventorySave;
import rpg.rpg_base.IslandManager.Island;
import rpg.rpg_base.IslandManager.Island.SettingsFlag;
import rpg.rpg_base.IslandManager.Settings;
import rpg.rpg_base.IslandManager.events.IslandEnterEvent;
import rpg.rpg_base.IslandManager.events.IslandExitEvent;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.data.Util.Util;

/**
 * @author tastybento
 *         Provides protection to islands
 */
public class PlayerEvents implements Listener {
    private final RPG_Base plugin;
    private static final boolean DEBUG = false;
    // A set of falling players
    private static Set<UUID> fallingPlayers = new HashSet<>();
    private static Map<UUID, List<String>> temporaryPerms = new HashMap<>();
    private List<UUID> respawn;

    public PlayerEvents(final RPG_Base plugin) {
        this.plugin = plugin;
        respawn = new ArrayList<>();
    }


    /**
     * Gives temporary perms to players who are online when the server is reloaded or the plugin reloaded.
     */

    /**
     * Prevents changing of hunger while having a special permission and being on your island
     * @param e - event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHungerChange(final FoodLevelChangeEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName() + " food level = " + ((Player)e.getEntity()).getFoodLevel() + " new food level = " + e.getFoodLevel());

        }
        // Allow food increases
        if (e.getFoodLevel() - ((Player)e.getEntity()).getFoodLevel() > 0) {
            return;
        }
        if (e.getEntity().hasPermission(Settings.PERMPREFIX + "nohunger")) {
            if(plugin.getGrid().playerIsOnIsland((Player) e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * Gives temporary perms
     * Gives flymode if player has a specific permission and is on his island
     * @param e - event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerEnterOnIsland(final IslandEnterEvent e){
        Player player = plugin.getServer().getPlayer(e.getPlayer());
        if (player != null && !player.hasMetadata("NPC")) {
            if (DEBUG) {
                plugin.getLogger().info("DEBUG: player entered island");
                plugin.getLogger().info("DEBUG: island center is " + e.getIslandLocation());
                if (e.getIslandOwner() != null && plugin.getPlayers().isAKnownPlayer(e.getIslandOwner())) {
                    plugin.getLogger().info("DEBUG: island owner is " + plugin.getPlayers().getName(e.getIslandOwner()));
                } else {
                    plugin.getLogger().info("DEBUG: island is unowned or owner unknown");
                }
            }
        }
    }



    /**
     * Handle player joining
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Island island = plugin.getGrid().getProtectedIslandAt(event.getPlayer().getLocation());
        if (island != null) {
            // Fire entry event
            final IslandEnterEvent e = new IslandEnterEvent(event.getPlayer().getUniqueId(), island, event.getPlayer().getLocation());
            plugin.getServer().getPluginManager().callEvent(e);
        }
    }

    /**
     * Revoke temporary perms
     * Removes flymode with a delay if player leave his island.
     * @param e - event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeaveIsland(final IslandExitEvent e) {
        if (DEBUG)
            plugin.getLogger().info("DEBUG: island exit event");
        final Player player = plugin.getServer().getPlayer(e.getPlayer());
        if (player != null && !player.hasMetadata("NPC")) {
            if (DEBUG) {
                plugin.getLogger().info("DEBUG: player left island. e.getLocation = " + e.getLocation());
                plugin.getLogger().info("DEBUG: player location = " + player.getLocation());
            }
        }
    }



    /**
     * Places player back on their island if the setting is true
     * @param e - event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        if (!Settings.respawnOnIsland) {
            return;
        }
        if (respawn.contains(e.getPlayer().getUniqueId())) {
            respawn.remove(e.getPlayer().getUniqueId());
            Location respawnLocation = plugin.getGrid().getSafeHomeLocation(e.getPlayer().getUniqueId(), 1);
            if (respawnLocation != null) {
                //plugin.getLogger().info("DEBUG: Setting respawn location to " + respawnLocation);
                e.setRespawnLocation(respawnLocation);
                // Get island
            }
        }
    }

    /**
     * Registers death of player.
     * Places the player on the island respawn list if set
     * @param e - event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        // Died in island space?
        UUID playerUUID = e.getEntity().getUniqueId();
        // Check if player has an island
        if (plugin.getPlayers().hasIsland(playerUUID) || plugin.getPlayers().inTeam(playerUUID)) {
            if (Settings.respawnOnIsland) {
                // Add them to the list to be respawned on their island
                respawn.add(playerUUID);
            }
            // Add death to death count
            plugin.getPlayers().addDeath(playerUUID);
            if (Settings.deathpenalty != 0) {
                if (plugin.getPlayers().inTeam(playerUUID)) {
                    // Tell team
                    plugin.getMessages().tellOfflineTeam(playerUUID, ChatColor.GREEN + "(" + String.valueOf(plugin.getPlayers().getDeaths(playerUUID)) + " " + ")");
                }
            }
        }
    }

    /*
     * Prevent dropping items if player dies on another island
     * This option helps reduce the down side of dying due to traps, etc.
     * Also handles muting of death messages
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVistorDeath(final PlayerDeathEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        // Mute death messages
        if (Settings.muteDeathMessages) {
            e.setDeathMessage(null);
        }
        // If visitors will keep items and their level on death
        // This will override any global settings
        if (Settings.allowVisitorKeepInvOnDeath) {
            // If the player is not a visitor then they die and lose everything -
            // sorry :-(
            Island island = plugin.getGrid().getProtectedIslandAt(e.getEntity().getLocation());
            if (island != null && !island.getMembers().contains(e.getEntity().getUniqueId())) {
                // They are a visitor
                InventorySave.getInstance().savePlayerInventory(e.getEntity());
                e.getDrops().clear();
                e.setKeepLevel(true);
                e.setDroppedExp(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVistorSpawn(final PlayerRespawnEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        // This will override any global settings
        if (Settings.allowVisitorKeepInvOnDeath) {
            InventorySave.getInstance().loadPlayerInventory(e.getPlayer());
            InventorySave.getInstance().clearSavedInventory(e.getPlayer());
        }
    }

    /*
     * Prevent item drop by visitors
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVisitorDrop(final PlayerDropItemEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        Island island = plugin.getGrid().getIslandAt(e.getItemDrop().getLocation());
        if ((island != null && island.getIgsFlag(Island.SettingsFlag.VISITOR_ITEM_DROP))
                || e.getPlayer().isOp() || e.getPlayer().hasPermission(Settings.PERMPREFIX + "mod.bypassprotect")
                || plugin.getGrid().locationIsOnIsland(e.getPlayer(), e.getItemDrop().getLocation())) {
            return;
        }
        Util.sendMessage(e.getPlayer(), ChatColor.RED + plugin.myLocale(e.getPlayer().getUniqueId()).islandProtected);
        e.setCancelled(true);
    }


    /*
     * Prevent typing /island if falling - hard core
     * Checked if player teleports
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerFall(final PlayerMoveEvent e) {
        if (e.getPlayer().isDead()) {
            return;
        }
        /*
         * too spammy
         * if (debug) {
         * plugin.getLogger().info(e.getEventName());
         * }
         */
        // Check if air below player
        // plugin.getLogger().info("DEBUG:" +
        // Math.round(e.getPlayer().getVelocity().getY()));
        if (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR
                && e.getPlayer().getLocation().getBlock().getType() == Material.AIR) {
            // plugin.getLogger().info("DEBUG: falling");
            setFalling(e.getPlayer().getUniqueId());
        } else {
            // plugin.getLogger().info("DEBUG: not falling");
            unsetFalling(e.getPlayer().getUniqueId());
        }
    }

    /**
     * Prevents teleporting when falling based on setting by stopping commands
     *
     * @param e - event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerCommandPreprocessEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
        }
        // Check commands
        // plugin.getLogger().info("DEBUG: falling command: '" +
        // e.getMessage().substring(1).toLowerCase() + "'");
        if (isFalling(e.getPlayer().getUniqueId()) && (Settings.fallingCommandBlockList.contains("*") || Settings.fallingCommandBlockList.contains(e.getMessage().substring(1).toLowerCase()))) {
            // Sorry you are going to die
            Util.sendMessage(e.getPlayer(), "You don't have required permission");
            Util.sendMessage(e.getPlayer(), "Couldn't teleport");
            e.setCancelled(true);
        }
    }

    /**
     * Prevents teleporting when falling based on setting and teleporting to locked islands
     *
     * @param e - event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent e) {
        if (DEBUG) {
            plugin.getLogger().info(e.getEventName());
            plugin.getLogger().info("DEBUG: to = " + e.getTo());
            plugin.getLogger().info("DEBUG: from = " + e.getFrom());
        }
        // Options -
        // Player is in an island world and trying to teleport out - handle
        // Player is in an island world and trying to teleport within - handle
        // Player is not in an island world and trying to teleport in - handle
        // Player is not in an island world and teleporting not in - skip
        if (e.getTo() == null || e.getFrom() == null) {
            return;
        }
        // Check if ready
        if (plugin.getGrid() == null) {
            if (DEBUG)
                plugin.getLogger().info("DEBUG: grid is not ready");
            return;
        }
        // Teleporting while falling check
        if (!Settings.allowTeleportWhenFalling && e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && !e.getPlayer().isOp()) {
            if (DEBUG)
                plugin.getLogger().info("DEBUG: Teleporting while falling check");
            // If the player is allowed to teleport excuse them
            if (plugin.getPlayers().isInTeleport(e.getPlayer().getUniqueId())) {
                if (DEBUG)
                    plugin.getLogger().info("DEBUG: player is allowed to teleport excuse them");
                unsetFalling(e.getPlayer().getUniqueId());
            } else if (isFalling(e.getPlayer().getUniqueId())) {
                if (DEBUG)
                    plugin.getLogger().info("DEBUG: player is falling");
                // Sorry you are going to die
                Util.sendMessage(e.getPlayer(), "Cannot teleport");
                e.setCancelled(true);
                // Check if the player is in the void and kill them just in case
                if (e.getPlayer().getLocation().getBlockY() < 0) {
                    if (DEBUG)
                        plugin.getLogger().info("DEBUG: player is in the void");
                    e.getPlayer().setHealth(0D);
                    unsetFalling(e.getPlayer().getUniqueId());
                }
                return;
            }
        }
        //plugin.getLogger().info("DEBUG: From : " + e.getFrom());
        //plugin.getLogger().info("DEBUG: To : " + e.getTo());
        // Teleporting to a locked island
        if (DEBUG)
            plugin.getLogger().info("DEBUG: getting islands for to from");
        Island islandFrom = plugin.getGrid().getProtectedIslandAt(e.getFrom());
        Island islandTo = plugin.getGrid().getProtectedIslandAt(e.getTo());

        // Ender pearl and chorus fruit teleport checks
        if (e.getCause() != null) {
            if (e.getCause().equals(TeleportCause.ENDER_PEARL)) {
                if (DEBUG)
                    plugin.getLogger().info("DEBUG: Enderpearl");

                if (islandTo == null) {
                    if (Settings.defaultWorldSettings.get(Island.SettingsFlag.ENDER_PEARL)) {
                        return;
                    }
                } else {
                    if (DEBUG )
                        plugin.getLogger().info("DEBUG: islandTo is not null enderpearl");
                    if (DEBUG )
                        plugin.getLogger().info("DEBUG: islandTo is regular island");
                    if (islandTo.getIgsFlag(Island.SettingsFlag.ENDER_PEARL) || islandTo.getMembers().contains(e.getPlayer().getUniqueId())) {
                        if (DEBUG )
                            plugin.getLogger().info("DEBUG: enderpearl allowed");
                        return;
                    }
                }
                if (DEBUG )
                    plugin.getLogger().info("DEBUG: enderpearl not allowed");
                Util.sendMessage(e.getPlayer(), ChatColor.RED + "Island is protected!");
                e.setCancelled(true);
                return;
            } else if (!plugin.getServer().getVersion().contains("(MC: 1.8")
                    && !plugin.getServer().getVersion().contains("(MC: 1.7")) {
                if (DEBUG )
                    plugin.getLogger().info("DEBUG: chorus fruit check");
                // Chorus fruit only exist in 1.9 and above
                if (e.getCause().equals(TeleportCause.CHORUS_FRUIT)) {
                    if (DEBUG )
                        plugin.getLogger().info("DEBUG: chorus fruit");
                    return;
                }
            }
        }

        // Announcement entering
        // Only says something if there is a change in islands
        /*
         * Teleport Situations:
         * islandTo == null && islandFrom != null - exit
         * islandTo == null && islandFrom == null - nothing
         * islandTo != null && islandFrom == null - enter
         * islandTo != null && islandFrom != null - same PlayerIsland or teleport?
         * islandTo == islandFrom
         */

        if (DEBUG )
            plugin.getLogger().info("DEBUG: announcements");
        if (islandTo != null && islandFrom == null) {
            if (DEBUG )
                plugin.getLogger().info("DEBUG: entering");
            // Entering
            if (islandTo.getOwner() != null && (islandTo.isLocked() || plugin.getPlayers().isBanned(islandTo.getOwner(),e.getPlayer().getUniqueId()))) {
                // Locked island
                Util.sendMessage(e.getPlayer(), ChatColor.RED + "Island is locked!");
                if (!plugin.getGrid().locationIsOnIsland(e.getPlayer(), e.getTo()) && !e.getPlayer().isOp()
                        && !e.getPlayer().hasPermission(Settings.PERMPREFIX + "mod.bypassprotect")
                        && !e.getPlayer().hasPermission(Settings.PERMPREFIX + "mod.bypasslock")) {
                    if (DEBUG )
                        plugin.getLogger().info("DEBUG: not allowed to enter");
                    e.setCancelled(true);
                    return;
                }
            }
            if (islandTo.isSpawn()) {
                if (DEBUG )
                    plugin.getLogger().info("DEBUG: islandTo is locked spawn");
                if(islandTo.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES)) {
                    if (!plugin.myLocale(e.getPlayer().getUniqueId()).lockEnteringSpawn.isEmpty()) {
                        Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockEnteringSpawn);
                    }
                }
            } else {
                if (DEBUG )
                    plugin.getLogger().info("DEBUG: islandTo is locked regular");
                if(islandTo.getOwner() != null && islandTo.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES)) {
                    if (!plugin.myLocale(e.getPlayer().getUniqueId()).lockNowEntering.isEmpty()) {
                        Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockNowEntering.replace("[name]", plugin.getGrid().getIslandName(islandTo.getOwner())));
                    }
                }
            }
            // Fire entry event
            final IslandEnterEvent event = new IslandEnterEvent(e.getPlayer().getUniqueId(), islandTo, e.getTo());
            plugin.getServer().getPluginManager().callEvent(event);
        } else if (islandTo == null && islandFrom != null && (islandFrom.getOwner() != null || islandFrom.isSpawn())) {
            if (DEBUG )
                plugin.getLogger().info("DEBUG: Leaving");
            // Leaving
            if (islandFrom.isSpawn()) {
                if (DEBUG )
                    plugin.getLogger().info("DEBUG: leaving spawn");
                // Leaving
                if(islandFrom.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES)) {
                    if (!plugin.myLocale(e.getPlayer().getUniqueId()).lockLeavingSpawn.isEmpty()) {
                        Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockLeavingSpawn);
                    }
                }
            } else {
                if (DEBUG )
                    plugin.getLogger().info("DEBUG: leaving locked");
                if(islandFrom.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES)) {
                    if (!plugin.myLocale(e.getPlayer().getUniqueId()).lockNowLeaving.isEmpty()) {
                        Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockNowLeaving.replace("[name]", plugin.getGrid().getIslandName(islandFrom.getOwner())));
                    }
                }
            }
            // Fire exit event
            final IslandExitEvent event = new IslandExitEvent(e.getPlayer().getUniqueId(), islandFrom, e.getFrom());
            plugin.getServer().getPluginManager().callEvent(event);
        } else if (islandTo != null && islandFrom != null && !islandTo.equals(islandFrom)) {
            if (DEBUG )
                plugin.getLogger().info("DEBUG: jumping from one island to another - adjacent islands");
            // Teleporting from one islands to another
            // Entering
            if (islandTo.isLocked() || plugin.getPlayers().isBanned(islandTo.getOwner(),e.getPlayer().getUniqueId())) {
                Util.sendMessage(e.getPlayer(), ChatColor.RED + plugin.myLocale(e.getPlayer().getUniqueId()).lockIslandLocked);
                if (!plugin.getGrid().locationIsOnIsland(e.getPlayer(), e.getTo()) && !e.getPlayer().isOp()
                        && !e.getPlayer().hasPermission(Settings.PERMPREFIX + "mod.bypassprotect")
                        && !e.getPlayer().hasPermission(Settings.PERMPREFIX + "mod.bypasslock")) {
                    if (DEBUG )
                        plugin.getLogger().info("DEBUG: cannot enter");
                    e.setCancelled(true);
                    return;
                }
            }
            if (islandFrom.isSpawn()) {
                // Leaving
                if(islandFrom.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES) && !plugin.myLocale(e.getPlayer().getUniqueId()).lockLeavingSpawn.isEmpty()) {
                    Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockLeavingSpawn);
                }
            } else if (islandFrom.getOwner() != null && !plugin.myLocale(e.getPlayer().getUniqueId()).lockNowLeaving.isEmpty()) {
                if(islandFrom.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES)) {
                    Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockNowLeaving.replace("[name]", plugin.getGrid().getIslandName(islandFrom.getOwner())));
                }
            }
            if (islandTo.isSpawn()) {
                if(islandTo.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES) && !plugin.myLocale(e.getPlayer().getUniqueId()).lockEnteringSpawn.isEmpty()) {
                    Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockEnteringSpawn);
                }
            } else if (islandTo.getOwner() != null && !plugin.myLocale(e.getPlayer().getUniqueId()).lockNowEntering.isEmpty()) {
                if(islandTo.getIgsFlag(SettingsFlag.ENTER_EXIT_MESSAGES)) {
                    Util.sendEnterExit(e.getPlayer(), plugin.myLocale(e.getPlayer().getUniqueId()).lockNowEntering.replace("[name]", plugin.getGrid().getIslandName(islandTo.getOwner())));
                }
            }
            // Fire exit event
            final IslandExitEvent event = new IslandExitEvent(e.getPlayer().getUniqueId(), islandFrom, e.getFrom());
            plugin.getServer().getPluginManager().callEvent(event);
            // Fire entry event
            final IslandEnterEvent event2 = new IslandEnterEvent(e.getPlayer().getUniqueId(), islandTo, e.getTo());
            plugin.getServer().getPluginManager().callEvent(event2);
        } else if (islandTo != null && islandFrom != null && (islandTo.equals(islandFrom) && !e.getFrom().getWorld().equals(e.getTo().getWorld()))) {
            if (DEBUG )
                plugin.getLogger().info("DEBUG: jumping from dimension to another - same island");
            // Fire exit event
            final IslandExitEvent event = new IslandExitEvent(e.getPlayer().getUniqueId(), islandFrom, e.getFrom());
            plugin.getServer().getPluginManager().callEvent(event);
            // Fire entry event
            final IslandEnterEvent event2 = new IslandEnterEvent(e.getPlayer().getUniqueId(), islandTo, e.getTo());
            plugin.getServer().getPluginManager().callEvent(event2);
        }
    }




    /**
     * Used to prevent teleporting when falling
     *
     * @param uniqueId - unique ID
     * @return true or false
     */
    public static boolean isFalling(UUID uniqueId) {
        return fallingPlayers.contains(uniqueId);
    }

    /**
     * Used to prevent teleporting when falling
     *
     * @param uniqueId - unique ID
     */
    public static void setFalling(UUID uniqueId) {
        fallingPlayers.add(uniqueId);
    }

    /**
     * Unset the falling flag
     *
     * @param uniqueId - unique ID
     */
    public static void unsetFalling(UUID uniqueId) {
        // getLogger().info("DEBUG: unset falling");
        fallingPlayers.remove(uniqueId);
    }

    /**
     * Prevents visitors from using commands on islands, like /spawner
     * @param e - event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVisitorCommand(final PlayerCommandPreprocessEvent e) {
        if (DEBUG) {
            plugin.getLogger().info("Visitor command " + e.getEventName() + ": " + e.getMessage());
        }
        // Check banned commands
        //plugin.getLogger().info(Settings.visitorCommandBlockList.toString());
        String[] args = e.getMessage().substring(1).toLowerCase().split(" ");
        if (Settings.visitorCommandBlockList.contains(args[0])) {
            Util.sendMessage(e.getPlayer(), ChatColor.RED + "Island is protected");
            e.setCancelled(true);
        }
    }


    /**
     * Prevents visitors from getting damage if invinciblevisitors option is set to TRUE
     * @param e - event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVisitorGetDamage(EntityDamageByEntityEvent e){
        if(!Settings.invincibleVisitors
                || !Settings.visitorDamagePrevention.contains(e.getCause())
                || !(e.getEntity() instanceof Player)
                || !e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
            return;
        }
        // Find out who the attacker is
        Player attacker = null;
        if (e.getDamager() instanceof Player) {
            attacker = (Player)e.getDamager();
        }
        if (e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile)e.getDamager();
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player)projectile.getShooter();
            }
        }
        // If we cannot work out who is the attacker then just prevent the damage and return
        if (attacker == null) {
            e.setCancelled(true);
            return;
        }
        // Check if at spawn
        if (plugin.getGrid().isAtSpawn(e.getEntity().getLocation())) {
            // PVP is not allowed
            attacker.sendMessage(ChatColor.RED + "PVP OFF");
            e.setCancelled(true);
        } else {
            // Not at spawn, protect visitor
            attacker.sendMessage(ChatColor.RED + "PVP OFF");
            e.setCancelled(true);
        }
    }

    /**
     * Prevents visitors from getting damage if invinciblevisitors option is set to TRUE
     * @param e - event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVisitorGetDamage(EntityDamageEvent e){
        if(!Settings.invincibleVisitors
                || !(e.getEntity() instanceof Player)
                || e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
            return;
        }
        Player p = (Player) e.getEntity();

        if (Settings.visitorDamagePrevention.contains(e.getCause())) e.setCancelled(true);

        if(e.getCause().equals(DamageCause.VOID)) {
            if (plugin.getPlayers().hasIsland(p.getUniqueId()) || plugin.getPlayers().inTeam(p.getUniqueId())) {
                Location safePlace = plugin.getGrid().getSafeHomeLocation(p.getUniqueId(), 1);
                if (safePlace != null) {
                    unsetFalling(p.getUniqueId());
                    p.teleport(safePlace);
                    // Set their fall distance to zero otherwise they crash onto their island and die
                    p.setFallDistance(0);
                    e.setCancelled(true);
                    return;
                }
            }
            if (plugin.getGrid().getSpawn() != null) {
                p.teleport(plugin.getGrid().getSpawnPoint());
                // Set their fall distance to zero otherwise they crash onto their island and die
                p.setFallDistance(0);
                e.setCancelled(true);
                return;
            } else if (!p.performCommand("spawn")) {
                // If this command doesn't work, let them die otherwise they may get trapped in the void forever
                return;
            } else {
                // Set their fall distance to zero otherwise they crash onto their island and die
                p.setFallDistance(0);
                e.setCancelled(true);
            }
        }
    }

    /**
     * Protect players from damage when teleporting
     * @param e - event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerTeleportDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        if (plugin.getPlayers().isInTeleport(p.getUniqueId())) {
            if (DEBUG)
                plugin.getLogger().info("DEBUG: protecting player from teleport damage");
            p.setFallDistance(0);
            e.setCancelled(true);
        }
    }
}
