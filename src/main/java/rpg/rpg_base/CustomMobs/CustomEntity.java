package rpg.rpg_base.CustomMobs;


import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.DamageManager;
import rpg.rpg_base.StatManager.HealthManager;

import java.io.File;
import java.util.List;
import java.util.Set;

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

                    MobLevelManager.setEntityScaling(name, entityStatScalePerLvl);
                    MobLevelManager.setEntityLevel(entity.getUniqueId(), Integer.parseInt(entityLvlMin), Integer.parseInt(entityLvlMax));
                    HealthManager.setEntityMaxHealth(entity.getUniqueId(), Integer.parseInt(entityHealth));
                    HealthManager.setEntityHealth(entity.getUniqueId(), HealthManager.getEntityMaxHealth(entity.getUniqueId()));
                    DamageManager.setEntityBaseDamage(entity.getUniqueId(), Integer.parseInt(entityDamage));

                    MobManager.registerMob(entity.getUniqueId());

                    tagVal = region.getId();

                    entity.getPersistentDataContainer().set(regionKey, PersistentDataType.STRING ,tagVal);
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
                name = key;
                String entityType = entity.getString(key + ".type");
                entityHealth = entity.getString(key + ".health");
                entityDamage = entity.getString(key + ".damage");
                entityLvlMin = entity.getString(key + ".minLvl");
                entityLvlMax = entity.getString(key + ".maxLvl");
                entityStatScalePerLvl = entity.getDouble(key + ".statScalePerLevel");
                xpDrop = entity.getInt(key + ".droppedXp");
                drops = entity.getStringList(key + "drops");

                type = EntityType.valueOf(entityType.toUpperCase());
                System.out.println("TYpe = " + type);
            }
        }else{
            System.out.println("CONFIG SECTION NULL");
        }
    }
}