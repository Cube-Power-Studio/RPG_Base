package rpg.rpg_base.CustomizedClasses.EntityHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.Utils.PathFinder;
import rpg.rpg_base.Utils.Util;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.RPG_Base;

import java.util.*;

@SuppressWarnings("unused")
public class CEntity implements Cloneable {
    private final RPG_Base plugin;
    private final Util util;

    public static EntityType type;
    public static NamespacedKey regionKey = new NamespacedKey(RPG_Base.getInstance(), "region");
    public String region = "";
    public static NamespacedKey mobTypeKey = new NamespacedKey(RPG_Base.getInstance(), "mobType");
    public String mobType = "";
    public Mob entity;

    public int level = 0;
    public int lvlMin = 0;
    public int lvlMax = 0;
    public double dmgScalePerLvl = 0;
    public double hpScalePerLvl = 0;

    public String name = "";
    public String customEntityType = "";

    public int baseMaxHp = 0;
    public int maxHP = 0;
    public int currentHP = 0;
    public int def = 0;
    public int baseDamage = 0;
    public int damage = 0;

    public Location spawnLocation;
    public Location currentStrollLocation;
    public List<Location> walkableBlocksInRadius;
    public int strollRange = 5;

    public int seeRange = 10;
    public int trackingRange = 15;

    public List<CItem> dropList = new ArrayList<>();
    public List<String> possibleDrops = new ArrayList<>();
    public int goldDrop = 0;
    public int xpDrop = 0;

    public BukkitRunnable aiTask;

    public static HashMap<UUID, CEntity> customEntities = new HashMap<>();
    public static HashMap<String, CEntity> customEntitiesTemplates = new HashMap<>();

    public CEntity(RPG_Base plugin, Util util) {
        this.plugin = plugin;
        this.util = util;
    }

    public void loadEntity(ConfigurationSection config) {
        String entityType = config.getString(".type");
        baseMaxHp = config.getInt(".health");
        baseDamage = config.getInt(".damage");
        lvlMin = config.getInt(".minLvl");
        lvlMax = config.getInt(".maxLvl");
        hpScalePerLvl = config.getDouble(".hpScalePerLevel");
        dmgScalePerLvl = config.getDouble(".dmgScalePerLevel");
        xpDrop = config.getInt(".droppedXp");
        goldDrop = config.getInt(".droppedGold");
        possibleDrops.addAll(config.getStringList(".drops"));
        mobType = config.getString(".type");
        name = config.getString(".name");
        strollRange = config.contains(".strollRange") ? config.getInt(".strollRange") : 5;
        trackingRange = config.contains(".trackRange") ? config.getInt(".trackRange") : 15;
        customEntityType = config.getName();

        if (entityType != null) {
            type = EntityType.valueOf(entityType.toUpperCase());
        } else {
            type = EntityType.ARMOR_STAND;
        }

        customEntitiesTemplates.put(customEntityType, this);
    }

    public void startAi(){
        spawnLocation = spawnLocation.toBlockLocation();
        PathFinder pathFinder = new PathFinder(spawnLocation, 500, true, 2, strollRange, true);
        walkableBlocksInRadius = pathFinder.getWalkableBlocksInRadius();

        final int[] destinationCooldown = {100};
        int destinationCooldownMax = 80;

        entity.setAggressive(false);

        aiTask = new BukkitRunnable() {
            @Override
            public void run() {
                List<? extends Player> sortedPlayersByDistance = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getWorld().equals(entity.getWorld()))
                        .filter(player -> player.getGameMode().equals(GameMode.SURVIVAL))
                        .sorted((p1, p2) -> Double.compare(p1.getLocation().distanceSquared(entity.getLocation()), p2.getLocation().distanceSquared(entity.getLocation())))
                        .toList();

                if (entity.getTarget() == null) {
                    //plugin.getLogger().info("no target");
                    destinationCooldown[0]++;
                    for (Player player : sortedPlayersByDistance) {
                        if (player.getLocation().distanceSquared(entity.getLocation()) <= seeRange * seeRange
                            && player.getLocation().distanceSquared(spawnLocation) <= trackingRange * trackingRange) {
                            entity.setTarget(player);
                            destinationCooldown[0] = 0;
                            break; // Once a player is found, exit the loop
                        }
                    }
                    //plugin.getLogger().info("No target found");
                    if(destinationCooldown[0] >= destinationCooldownMax){
                        //plugin.getLogger().info("choosing next destination");
                        Random random = new Random();

                        if (walkableBlocksInRadius.isEmpty()) {
                            //plugin.getLogger().warning("No walkable blocks found for entity " + entity.getName());
                            killEntity();
                            return;
                        }

                        currentStrollLocation = walkableBlocksInRadius.get(random.nextInt(walkableBlocksInRadius.size()));
                        while(!util.isLocationInRegion(currentStrollLocation, region)){
                            walkableBlocksInRadius.remove(currentStrollLocation);
                            currentStrollLocation = walkableBlocksInRadius.get(random.nextInt(walkableBlocksInRadius.size()));
                        }

                        entity.getPathfinder().moveTo(currentStrollLocation, 0.9);

                        //System.out.println("Next walk location: " + currentStrollLocation);

                        destinationCooldown[0] = 0;
                    }else{
                        entity.getPathfinder().moveTo(currentStrollLocation, 0.9);
                        destinationCooldown[0] += 1;
                    }
                } else {
                    if (entity.getTarget().getLocation().distanceSquared(entity.getLocation()) > seeRange * seeRange
                            || ((Player) entity.getTarget()).getGameMode().equals(GameMode.CREATIVE)
                            || entity.getLocation().distance(spawnLocation) > trackingRange) {
                        entity.setTarget(null);
                    }
                }
            }
        };

        aiTask.runTaskTimer(plugin, 0, 10L);
    }

    public void updateDisplayName(){
        entity.customName(Component.text("[" + level + "Lvl] - ").color(NamedTextColor.GOLD)
                .append(Component.text(name).color(NamedTextColor.WHITE))
                .append(Component.text(" " + currentHP + "/" + maxHP + "❤").color(NamedTextColor.RED))
        );
    }

    public int getDamage(){
        return (int) Math.round(damage * level * dmgScalePerLvl);
    }

    public void dealDamage(int damage, Entity damager){
        if(damage - def > currentHP){
            entity.setCustomNameVisible(false);
            currentHP = 0;
            killEntity();
            if(damager instanceof Player){
                CPlayer.getPlayerByUUID(damager.getUniqueId()).xp += xpDrop;
                MoneyManager.addPlayerGold(((Player) damager).getPlayer(), goldDrop);
            }
        }else{
            currentHP -= damage - def;
            updateDisplayName();
        }
    }

    public Mob getEntity() {
        return entity;
    }

    public void killEntity(){
        aiTask.cancel();
        entity.setHealth(0);
        entity = null;
    }

    public static CEntity getEntityByUUID(UUID uuid){
        return customEntities.get(uuid);
    }

    @Override
    public String toString(){
        return name + "," + level + "," + entity.getUniqueId();
    }

    @Override
    public CEntity clone() {
        try {
            CEntity clone = (CEntity) super.clone();

            clone.dropList = new ArrayList<>(this.dropList);

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
