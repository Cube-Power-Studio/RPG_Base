package rpg.rpg_base.GeneralEvents;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import rpg.rpg_base.IslandManager.Settings;
import rpg.rpg_base.RPG_Base;

/**
 * This class is to catch chats and do two things: (1) substitute in the island level to the chat string
 * and (2) implement team chat. As it can be called asynchronously (and usually it is when a player chats)
 * it cannot access any HashMaps or Bukkit APIs without running the risk of a clash with another thread
 * or the main thread. As such two things are done:
 * 1. To handle the level substitution, a thread-safe hashmap of players and levels is stored and updated
 * in this class.
 * 2. To handle team chat, a thread-safe hashmap is used to store whether team chat is on for a player or not
 * and if it is, the team chat itself is queued to run on the next server tick, i.e., in the main thread
 * This all ensures it's thread-safe.
 * @author tastybento
 *
 */
public class ChatListener implements Listener {

    private final RPG_Base plugin;
    private final Map<UUID,Boolean> teamChatUsers;
    private final Map<UUID,String> playerLevels;
    private final Set<UUID> spies;
    private static final boolean DEBUG = false;

    /**
     * @param plugin - ASkyBlock plugin object
     */
    public ChatListener(RPG_Base plugin) {
        this.teamChatUsers = new ConcurrentHashMap<>();
        this.playerLevels = new ConcurrentHashMap<>();
        this.plugin = plugin;
        // Add all online player Levels

        // Initialize spies
        spies = new HashSet<>();
    }

    private static final BigInteger THOUSAND = BigInteger.valueOf(1000);
    /**
     * Provides an easy way to "fancy" the island level in chat
     * @since 3.0.8.3
     */
    private static final TreeMap<BigInteger, String> LEVELS;
    static {
        LEVELS = new TreeMap<>();

        LEVELS.put(THOUSAND, "k");
        LEVELS.put(THOUSAND.pow(2), "M");
        LEVELS.put(THOUSAND.pow(3), "G");
        LEVELS.put(THOUSAND.pow(4), "T");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (DEBUG)
            RPG_Base.getInstance().getLogger().info("DEBUG: " + event.getEventName());
        // Substitute variable - thread safe
        String level = "";
        if (DEBUG) {
            RPG_Base.getInstance().getLogger().info("DEBUG: getFormat = " + event.getFormat());
            RPG_Base.getInstance().getLogger().info("DEBUG: getMessage = " + event.getMessage());
        }
        String format = event.getFormat();
        if (!Settings.chatLevelPrefix.isEmpty()) {
            format = format.replace(Settings.chatLevelPrefix, level);
            if (DEBUG)
                RPG_Base.getInstance().getLogger().info("DEBUG: format (island level substitute) = " + format);
        }
        event.setFormat(format);
        if (DEBUG)
            RPG_Base.getInstance().getLogger().info("DEBUG: format set");
        // Team chat
        if (Settings.teamChat && teamChatUsers.containsKey(event.getPlayer().getUniqueId())) {
            if (DEBUG)
                RPG_Base.getInstance().getLogger().info("DEBUG: team chat");
            // Cancel the event
            event.setCancelled(true);
            // Queue the sync task because you cannot use HashMaps asynchronously. Delaying to the next tick
            // won't be a major issue for sync events either.
//            Bukkit.getScheduler().runTask(plugin, new Runnable() {
//                @Override
//                public void run() {
//                    teamChat(event,event.getMessage());
//                }});
        }
    }

//    private void teamChat(final AsyncPlayerChatEvent event, String message) {
//        Player player = event.getPlayer();
//        UUID playerUUID = player.getUniqueId();
//        //Bukkit.getLogger().info("DEBUG: post: " + message);
//        // Is team chat on for this player
//        // Find out if this player is in a team (should be if team chat is on)
//        // TODO: remove when player resets or leaves team
//        if (plugin.getPlayers().inTeam(playerUUID)) {
//            List<UUID> teamMembers = plugin.getPlayers().getMembers(player.getUniqueId());
//            // Tell only the team members if they are online
//            boolean onLine = false;
//            if (Settings.chatIslandPlayer.isEmpty()) {
//                message = plugin.myLocale(playerUUID).teamChatPrefix + message;
//            } else {
//                message = plugin.myLocale(playerUUID).teamChatPrefix.replace(Settings.chatIslandPlayer,player.getDisplayName()) + message;
//            }
//            for (UUID teamMember : teamMembers) {
//                Player teamPlayer = plugin.getServer().getPlayer(teamMember);
//                if (teamPlayer != null) {
//                    player.sendMessage(String.valueOf(teamPlayer), message);
//                    if (!teamMember.equals(playerUUID)) {
//                        onLine = true;
//                    }
//                }
//            }
//            // Spy function
//            if (onLine) {
//                for (Player onlinePlayer: plugin.getServer().getOnlinePlayers()) {
//                    if (spies.contains(onlinePlayer.getUniqueId()) && onlinePlayer.hasPermission(Settings.PERMPREFIX + "mod.spy")) {
//                        player.sendMessage(String.valueOf(onlinePlayer), ChatColor.RED + "[TCSpy] " + ChatColor.WHITE + message);
//                    }
//                }
//                //Log teamchat
//                if(Settings.logTeamChat) RPG_Base.getInstance().getLogger().info(ChatColor.stripColor(message));
//            }
//            if (!onLine) {
//                player.sendMessage(player, ChatColor.RED + plugin.myLocale(playerUUID).teamChatNoTeamAround);
//                player.sendMessage(player, ChatColor.RED + plugin.myLocale(playerUUID).teamChatStatusOff);
//                teamChatUsers.remove(playerUUID);
//            }
//        } else {
//            player.sendMessage(player, ChatColor.RED + plugin.myLocale(playerUUID).teamChatNoTeamAround);
//            player.sendMessage(player, ChatColor.RED + plugin.myLocale(playerUUID).teamChatStatusOff);
//            // Not in a team any more so delete
//            teamChatUsers.remove(playerUUID);
//        }
//    }

    /**
     * Adds player to team chat
     * @param playerUUID - the player's UUID
     */
    public void setPlayer(UUID playerUUID) {
        this.teamChatUsers.put(playerUUID,true);
    }

    /**
     * Removes player from team chat
     * @param playerUUID - the player's UUID
     */
    public void unSetPlayer(UUID playerUUID) {
        this.teamChatUsers.remove(playerUUID);
    }

    /**
     * Whether the player has team chat on or not
     * @param playerUUID - the player's UUID
     * @return true if team chat is on
     */
    public boolean isTeamChat(UUID playerUUID) {
        return this.teamChatUsers.containsKey(playerUUID);
    }

    /**
     * Store the player's level for use in their chat tag
     * @param playerUUID - the player's UUID
     * @param l
     */
    public void setPlayerLevel(UUID playerUUID, long l) {
        //RPG_Base.getLogger()).info("DEBUG: putting " + playerUUID.toString() + " Level " + level);
        playerLevels.put(playerUUID, String.valueOf(l));
    }

    /**
     * Return the player's level for use in chat - async safe
     * @param playerUUID - the player's UUID
     * @return Player's level as string
     */
    public String getPlayerLevel(UUID playerUUID) {
        return playerLevels.get(playerUUID);
    }


    /**
     * Toggles team chat spy. Spy must also have the spy permission to see chats
     * @param playerUUID - the player's UUID
     * @return true if toggled on, false if toggled off
     */
    public boolean toggleSpy(UUID playerUUID) {
        if (spies.contains(playerUUID)) {
            spies.remove(playerUUID);
            return false;
        } else {
            spies.add(playerUUID);
            return true;
        }
    }
}