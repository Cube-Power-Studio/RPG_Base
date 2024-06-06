package rpg.rpg_base.StatManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;

import java.util.HashMap;
import java.util.UUID;

public class DamageManager {
    private static final HashMap<Player, Integer> playerBaseDamage = new HashMap<>();
    private static final HashMap<Player, Integer> playerDamage = new HashMap<>();
    private static final HashMap<UUID, Integer> entityBaseDamage = new HashMap<>();
    private static final HashMap<UUID, Integer> entityDamage = new HashMap<>();

    public static int getPlayerBaseDamage(Player  player){
        return playerBaseDamage.getOrDefault(player, 0);
    }
    public static void setPlayerBaseDamage(Player player, int i){
        playerBaseDamage.put(player, i);
    }
    public static int getPlayerDamage(Player p){
        return playerDamage.getOrDefault(p, 0);
    }
    public static void setPlayerDamage(Player player, int i){
        playerDamage.put(player, i);
    }
    public static void addPlayerDamage(Player player, int i){
        playerDamage.put(player, getPlayerBaseDamage(player) + i);
    }
    public static void remPlayerDamage(Player player, int i){
        playerDamage.put(player, getPlayerBaseDamage(player)-i);
    }
    public static int getEntityBaseDamage(UUID u){
        return entityBaseDamage.getOrDefault(u, 1);
    }
    public static void setEntityBaseDamage(UUID u, int i){
        entityBaseDamage.put(u, i);
    }
    public static int getEntityDamage(UUID u){
        return entityDamage.getOrDefault(u, 1);
    }
    public static void setEntityDamage(UUID u, int i){
        entityDamage.put(u, i);
    }
    public static void addEntityDamage(UUID u, int i){
        entityDamage.put(u, getEntityBaseDamage(u) + i);
    }
    public static void remEntityDamage(UUID u, int i){
        entityDamage.put(u, getEntityBaseDamage(u)-i);
    }
}
