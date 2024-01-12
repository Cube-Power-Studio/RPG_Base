package rpg.rpg_base.StatManager;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;


public class HealthManager {
    private static RPG_Base plugin;
    public HealthManager (RPG_Base plugin){
        HealthManager.plugin = plugin;
    }
    private static final HashMap<Player, Integer> playerMaxHealth = new HashMap<>();
    private static final HashMap<Player, Integer> playerHealth = new HashMap<>();
    private static final HashMap<Player, Integer> playerHealthRegen = new HashMap<>();
    private static final HashMap<UUID, Integer> entityMaxHealth = new HashMap<>();
    private static final HashMap<UUID, Integer> entityHealth = new HashMap<>();



    public static void distributeDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
                if (damager instanceof Player) {
                    Integer playerDamage = DamageManager.getPlayerDamage((Player) damager);
                    if (playerDamage != null) {
                        remPlayerHealth(player, playerDamage);
                    } else {
                        // Handle the case when playerDamage is null
                    }
                } else {
                    Integer entityDamage = DamageManager.getEntityDamage(damager.getUniqueId());
                    if (entityDamage != null) {
                        remPlayerHealth(player, entityDamage);
                    } else {
                        // Handle the case when entityDamage is null
                    }
                }
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
                if (damager instanceof Player) {
                    Integer playerDamage = DamageManager.getPlayerDamage((Player) damager);
                    if (playerDamage != null) {
                        remPlayerHealth(player, playerDamage / 3);
                    } else {
                        // Handle the case when playerDamage is null
                    }
                } else {
                    Integer entityDamage = DamageManager.getEntityDamage(damager.getUniqueId());
                    if (entityDamage != null) {
                        remPlayerHealth(player, entityDamage / 3);
                    } else {
                        // Handle the case when entityDamage is null
                    }
                }
            } else {
                remPlayerHealth(player, (int) e.getDamage());
            }

            if (getPlayerHealth(player) <= 0) {
                player.setHealth(0);
            }
        } else {
            Entity entity = e.getEntity();

            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
                if (damager instanceof Player) {
                    Integer playerDamage = DamageManager.getPlayerDamage((Player) damager);
                    if (playerDamage != null) {
                        remEntityHealth(e.getEntity().getUniqueId(), playerDamage);
                    } else {
                        // Handle the case when playerDamage is null
                    }
                } else {
                    Integer entityDamage = DamageManager.getEntityDamage(damager.getUniqueId());
                    if (entityDamage != null) {
                        remEntityHealth(e.getEntity().getUniqueId(), entityDamage);
                    } else {
                        // Handle the case when entityDamage is null
                    }
                }
            } else if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
                if (damager instanceof Player) {
                    Integer playerDamage = DamageManager.getPlayerDamage((Player) damager);
                    if (playerDamage != null) {
                        remEntityHealth(e.getEntity().getUniqueId(), playerDamage / 3);
                    } else {
                        // Handle the case when playerDamage is null
                    }
                } else {
                    Integer entityDamage = DamageManager.getEntityDamage(damager.getUniqueId());
                    if (entityDamage != null) {
                        remEntityHealth(e.getEntity().getUniqueId(), entityDamage / 3);
                    } else {
                        // Handle the case when entityDamage is null
                    }
                }
            } else {
                remEntityHealth(e.getEntity().getUniqueId(), (int) e.getDamage());
            }

            if (getEntityHealth(entity.getUniqueId()) <= 0) {
                Damageable damageable = (Damageable) entity;
                damageable.setHealth(0);
            }
        }
        e.setDamage(0);
    }
    //Player section of HealthManager
    public static int getPlayerMaxHealth(Player p){
        return playerMaxHealth.getOrDefault(p, 100);
    }
    public static void addPlayerMaxHealth(Player p, int i){
        playerMaxHealth.put(p, getPlayerMaxHealth(p) + i);
    }
    public static void remPlayerMaxHealth(Player p, int i){
        playerMaxHealth.put(p, getPlayerMaxHealth(p) - i);
    }
    public static int getPlayerHealth(Player p){
        return playerHealth.getOrDefault(p, 100);
    }
    public static void addPlayerHealth(Player p, int i){
        playerHealth.put(p, getPlayerHealth(p) + i);
    }
    public static void remPlayerHealth(Player p, int i){
        playerHealth.put(p, getPlayerHealth(p) - i);
    }
    public static void setPlayerHealth(Player p, int i){
        playerHealth.put(p, i);
    }
    public static int getPlayerHealthRegen(Player p){
        return playerHealthRegen.getOrDefault(p, 5);
    }
    public static void addPlayerHealthRegen(Player p, int i){
        playerHealthRegen.put(p, getPlayerHealthRegen(p) + i);
    }
    public static void remPlayerHealthRegen(Player p, int i){
        playerHealthRegen.put(p, getPlayerHealthRegen(p) - i);
    }





    //Entity section of HealthManager
    public static int getEntityMaxHealth(UUID e){
        return entityMaxHealth.getOrDefault(e, 100);
    }
    public static void addEntityMaxHealth(UUID e, int i){
        entityMaxHealth.put(e, getEntityMaxHealth(e) + i);
    }
    public static void remEntityMaxHealth(UUID e, int i){
        entityMaxHealth.put(e, getEntityMaxHealth(e) - i);
    }
    public static void setEntityMaxHealth(UUID e, int i){
        entityMaxHealth.put(e, i);
    }
    public static void setEntityHealth(UUID entity, int health) {
        entityHealth.put(entity, health);
    }

    public static int getEntityHealth(UUID entity) {
        return entityHealth.getOrDefault(entity, 100); // Default health value
    }
    public static void addEntityHealth(UUID e, int i){
        entityHealth.put(e, getEntityHealth(e) + i);
    }
    public static void remEntityHealth(UUID e, int i){
        entityHealth.put(e, getEntityHealth(e) - i);
    }


}
