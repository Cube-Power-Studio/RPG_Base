package rpg.rpg_base.StatManager;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomMobs.MobManager;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.UUID;

public class HealthManager {
    private static RPG_Base plugin;
    public HealthManager (RPG_Base plugin){
        HealthManager.plugin = plugin;
    }
    private static final HashMap<Player, Integer> playerMaxHealth = new HashMap<>();
    private static final HashMap<Player, Integer> playerHealth = new HashMap<>();
    private static final HashMap<Player, Integer> playerHealthRegen = new HashMap<>();
    public static final HashMap<UUID, HealthRegen> healthRegenTasks = new HashMap<>();
    private static final HashMap<UUID, Integer> entityMaxHealth = new HashMap<>();
    private static final HashMap<UUID, Integer> entityHealth = new HashMap<>();

    @SuppressWarnings({"ConstantConditions","StatementWithEmptyBody"})

    public static void distributeDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity target = e.getEntity();

        if (target instanceof Player) {
            Player player = (Player) target;
            Integer damageAmount = null;

            if (e.getCause().equals(EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK) || e.getCause().equals(EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    if (damager instanceof Player) {
                        damageAmount = DamageManager.getPlayerDamage((Player) damager);
                    } else {
                        damageAmount = DamageManager.getEntityDamage(damager.getUniqueId()) + DamageManager.getEntityBaseDamage(damager.getUniqueId());
                    }
                }

                if (e.getCause().equals(EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                    if (damageAmount != null) {
                        damageAmount /= 3;
                    }
                }
            }

            if (damageAmount == null) {
                damageAmount = (int) e.getDamage();
            }

            remPlayerHealth(player, damageAmount);

            if (getPlayerHealth(player) <= 0) {
                player.setHealth(0);
            }
        } else {
            UUID entityUUID = target.getUniqueId();
            Integer damageAmount = null;

            if (e.getCause().equals(EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK) || e.getCause().equals(EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    if (damager instanceof Player) {
                        damageAmount = DamageManager.getPlayerDamage((Player) damager);
                    } else {
                        damageAmount = DamageManager.getEntityDamage(damager.getUniqueId()) + DamageManager.getEntityBaseDamage(damager.getUniqueId());
                    }
                }

                if (e.getCause().equals(EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                    if (damageAmount != null) {
                        damageAmount /= 3;
                    }
                }
            }

            if (damageAmount == null) {
                damageAmount = (int) e.getDamage();
            }

            remEntityHealth(entityUUID, damageAmount);

            if (getEntityHealth(entityUUID) <= 0) {
                target.getPersistentDataContainer().set(MobManager.killerKey, PersistentDataType.STRING, damager.getUniqueId().toString());
                ((Damageable) target).setHealth(0);
            }
        }

        e.setDamage(0);
    }

    public static void healthRegen(Player player) {
        UUID playerId = player.getUniqueId();
        if (getPlayerHealth(player) < getPlayerMaxHealth(player)) {
            // Check if there is a running HealthRegen task for the player
            HealthRegen existingTask = healthRegenTasks.get(playerId);
            if (existingTask == null || !existingTask.isRunning()) {
                // Create and start a new HealthRegen task
                HealthRegen healthRegen = new HealthRegen(player);
                healthRegen.runTaskTimer(plugin, 0, 20);
                healthRegenTasks.put(playerId, healthRegen);
            }
        }
    }

    public boolean isHealthRegenRunning(Player player) {
        UUID playerId = player.getUniqueId();
        HealthRegen healthRegen = healthRegenTasks.get(playerId);
        return healthRegen != null && healthRegen.isRunning();
    }

    public static int getPlayerMaxHealth(Player p){
        return playerMaxHealth.getOrDefault(p, 100);
    }
    public static void addPlayerMaxHealth(Player p, int i){
        playerMaxHealth.put(p, getPlayerMaxHealth(p) + i);
    }
    public static void remPlayerMaxHealth(Player p, int i){
        playerMaxHealth.put(p, getPlayerMaxHealth(p) - i);
    }
    public static void setPlayerMaxHealth(Player p, int i) {playerMaxHealth.put(p, i);};
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
