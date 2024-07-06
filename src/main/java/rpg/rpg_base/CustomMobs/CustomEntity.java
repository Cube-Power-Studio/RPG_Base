package rpg.rpg_base.CustomMobs;


import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.DamageManager;
import rpg.rpg_base.StatManager.HealthManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CustomEntity {
    public static String name;
    public static EntityType type;
    public static NamespacedKey regionKey = new NamespacedKey(RPG_Base.getInstance(), "region");;
    public static String tagVal;
    public static String entityHealth;
    public static String entityDamage;
    public static String entityLvlMin;
    public static String entityLvlMax;
    public static double entityStatScalePerLvl;
    public static int xpDrop;
    public static List<String> drops;
    public static HashMap<UUID, ItemStack[]> droppedItems = new HashMap<>();
    public static HashMap<UUID, Integer> droppedXp = new HashMap<>();
    public static HashMap<UUID, String> mobName = new HashMap<>();
    public static MobDrops selector = new MobDrops();

    public void spawnEntity(Location location, File entityFile, ProtectedRegion region) {
        loadEntityConfig(entityFile); // Ensure this sets 'type', 'entityHealth', and 'entityDamage' correctly

        if (type == null) {
            System.err.println("Error: Entity type is null!");
            return;
        }

        World world = location.getWorld();
        if (world == null || !world.isChunkLoaded(location.getChunk())) {
            System.err.println("Error: World or chunk is not loaded!");
            return;
        }

        try {
            Entity entity = world.spawnEntity(location, type);
            if (entity != null) {
                MobManager.registerMob(entity.getUniqueId());
                if (entity instanceof LivingEntity) {

                    if(entity instanceof Zombie){
                        ((Zombie) entity).setAdult();
                    }
                    if(entity instanceof Zoglin){
                        ((Zoglin)entity).setAdult();
                    }
                    if(entity instanceof Piglin){
                        ((Piglin) entity).setAdult();
                    }

                    MobLevelManager.setEntityLevel(entity.getUniqueId(), Integer.parseInt(entityLvlMin), Integer.parseInt(entityLvlMax));
                    HealthManager.setEntityMaxHealth(entity.getUniqueId(), (int) (Integer.parseInt(entityHealth) * entityStatScalePerLvl));
                    HealthManager.setEntityHealth(entity.getUniqueId(), HealthManager.getEntityMaxHealth(entity.getUniqueId()));
                    DamageManager.setEntityBaseDamage(entity.getUniqueId(), (int) (Integer.parseInt(entityDamage) * entityStatScalePerLvl));

                    tagVal = region.getId();

                    entity.getPersistentDataContainer().set(regionKey, PersistentDataType.STRING ,tagVal);

                    for (String mobDrop : drops) {
                        ItemStack item = new ItemStack(Material.DIRT);

                        Material matchedMaterial = Material.matchMaterial(mobDrop.split(",")[0].toUpperCase());
                        if (matchedMaterial != null) {
                            item = new ItemStack(matchedMaterial);
                        }else if(ItemHandlers.getCustomItemByName(mobDrop.split(",")[0])!=null){
                            item = ItemHandlers.getCustomItemByName(mobDrop.split(",")[0]);
                        }
                        float chance = Float.parseFloat(mobDrop.split(",")[1]);
                        selector.addDropChance(item, chance);
                    }
                    droppedItems.put(entity.getUniqueId(), selector.itemDrops());
                    droppedXp.put(entity.getUniqueId(), xpDrop);
                    mobName.put(entity.getUniqueId(), name);
                }
            } else {
                System.out.println("Failed to spawn entity.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception occurred while spawning entity: " + e.getMessage());
        }
    }

    public static void loadEntityConfig(File file){
        YamlConfiguration entity = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection entitySection = entity.getConfigurationSection("");
        if(entitySection!=null){
            Set<String> mobKeys = entitySection.getKeys(false);

            for(String key : mobKeys) {
                String entityType = entity.getString(key + ".type");
                entityHealth = entity.getString(key + ".health");
                entityDamage = entity.getString(key + ".damage");
                entityLvlMin = entity.getString(key + ".minLvl");
                entityLvlMax = entity.getString(key + ".maxLvl");
                entityStatScalePerLvl = entity.getDouble(key + ".statScalePerLevel");
                xpDrop = entity.getInt(key + ".droppedXp");
                drops = entity.getStringList(key + ".drops");
                name = entity.getString(key + ".name");

                type = EntityType.valueOf(entityType.toUpperCase());
                System.out.println("TYpe = " + type);
            }
        }else{
            System.out.println("CONFIG SECTION NULL");
        }
    }
}