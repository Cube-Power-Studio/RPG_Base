package rpg.rpg_base.CustomizedClasses.EntityHandler;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.PlayerSkills;
import rpg.rpg_base.Data.Util;
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
    public int strollRange = 0;

    public int seeRange = 0;
    public int trackingRange = 0;

    public List<CItem> dropList = new ArrayList<>();
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
        maxHP = config.getInt(".health");
        damage = config.getInt(".damage");
        lvlMin = config.getInt(".minLvl");
        lvlMax = config.getInt(".maxLvl");
        hpScalePerLvl = config.getDouble(".hpScalePerLevel");
        dmgScalePerLvl = config.getDouble(".dmgScalePerLevel");
        xpDrop = config.getInt(".droppedXp");
        goldDrop = config.getInt(".droppedGold");
        MobDrops mobDrops = new MobDrops();
        config.getStringList(".drops").forEach(drop -> {
            String[] dropParameters = drop.split(",");
            String[] dropMinMax = dropParameters[2].split("-");

            int dropMin = Integer.parseInt(dropMinMax[0]);
            int dropMax = Integer.parseInt(dropMinMax[1]);

            CItem item = CItem.customItemsByName.get(dropParameters[0]);

            double chance = Double.parseDouble(dropParameters[1]);

            mobDrops.addDropChance(item, chance, dropMin, dropMax);
        });
        mobType = config.getString(".type");
        name = config.getString(".name");
        customEntityType = config.getName();

        if (entityType != null) {
            type = EntityType.valueOf(entityType.toUpperCase());
        } else {
            type = EntityType.ARMOR_STAND;
        }

        customEntitiesTemplates.put(customEntityType, this);
    }

    public void startAi(){
        aiTask = new BukkitRunnable() {
            @Override
            public void run() {
                List<? extends Player> sortedPlayersByDistance = Bukkit.getOnlinePlayers().stream()
                        .sorted((p1, p2) -> {
                            double distance1 = p1.getLocation().distanceSquared(entity.getLocation());
                            double distance2 = p2.getLocation().distanceSquared(entity.getLocation());
                            return Double.compare(distance1, distance2);
                        }).toList();

                for(Player player : sortedPlayersByDistance) {
                    if (player.getLocation().distanceSquared(entity.getLocation()) > seeRange) {
                        Random random = new Random();

                        // Generate random offsets within the range (-range to +range)
                        double offsetX = random.nextDouble() * (strollRange * 2) - strollRange;
                        double offsetY = random.nextDouble() * (strollRange * 2) - strollRange;
                        double offsetZ = random.nextDouble() * (strollRange * 2) - strollRange;

                        // Create a new randomized location
                        Location randomLocation = spawnLocation.clone().add(offsetX, offsetY, offsetZ);

                        // Ensure the Y-coordinate stays within reasonable bounds (e.g., above the ground)
                        randomLocation.setY(spawnLocation.getY());

                        entity.getPathfinder().moveTo(randomLocation, 1.0);

                        break;
                    }else{
                        entity.setTarget(player);
                    }
                }
            }
        };

        aiTask.runTaskTimer(plugin, 10L, 10L);
    }

    public void updateDisplayName(){
        entity.customName(Component.text("[" + level + "Lvl] - ").color(NamedTextColor.GOLD)
                .append(Component.text(name).color(NamedTextColor.WHITE))
                .append(Component.text(currentHP + "/" + maxHP + "❤").color(NamedTextColor.RED))
        );
    }

    public int getDamage(){
        return (int) Math.round(damage * level * dmgScalePerLvl);
    }

    public void dealDamage(int damage){
        if(damage - def >= currentHP){
            entity.setCustomNameVisible(false);
            currentHP = 0;
            killEntity();
        }else{
            currentHP =- damage - def;
            updateDisplayName();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void killEntity(){
        aiTask.cancel();
        entity.setHealth(0);
        customEntities.remove(entity.getUniqueId());
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
