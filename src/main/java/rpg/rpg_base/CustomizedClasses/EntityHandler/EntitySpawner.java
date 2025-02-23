package rpg.rpg_base.CustomizedClasses.EntityHandler;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.Data.Util;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.*;

public class EntitySpawner {
    private final RPG_Base plugin = RPG_Base.getInstance();
    private final Util util;

    public static final HashMap<ProtectedRegion, Integer> scheduledSpawns = new HashMap<>();
    private static final List<ProtectedRegion> scheduledRegions = new ArrayList<>();

    public EntitySpawner(Util util) {
        this.util = util;
    }

    public void spawnEntitiesInRegion(ProtectedRegion region, World world){
        if(region.getFlag(MobFlags.customMobsFlag) == StateFlag.State.ALLOW){
            String regionID = region.getId();

            File path = new File(RPG_Base.getInstance().getDataFolder(), "/WG/mobs/" + world.getName() + "/" + regionID + ".yml");
            if (!path.exists()) {
                System.out.println("Region file does not exist: " + path.getAbsolutePath());
                return;
            }

            YamlConfiguration regionFile = YamlConfiguration.loadConfiguration(path);
            int maxNumberOfMobsInRegion = regionFile.getInt("numberOfMobsInRegion");
            int currentMobsInRegion = getEntitiesWithTag(regionID, world);
            int scheduledSpawnsInRegion = scheduledSpawns.getOrDefault(region, 0);

            if (currentMobsInRegion < maxNumberOfMobsInRegion) {
                int mobsNeeded = maxNumberOfMobsInRegion - (currentMobsInRegion + scheduledSpawnsInRegion);
                if (!scheduledRegions.contains(region)) {
                    scheduledRegions.add(region);
                }

                Random random = new Random();

                for (int i = 0; i < mobsNeeded; i++) {
                    new SpawnEntitiesInRegion(util, region, world).runTaskLater(plugin, random.nextInt(100) + 20);
                    scheduledSpawns.put(region, scheduledSpawns.getOrDefault(region, 0) + 1);
                }
            } else {
                scheduledSpawns.remove(region);
                scheduledRegions.remove(region);
            }
        }
    }

    public static int getEntitiesWithTag(String tag, World world) {
        return (int) world.getEntitiesByClass(Entity.class).stream()
                .filter(entity -> Objects.equals(entity.getPersistentDataContainer().get(CEntity.regionKey, PersistentDataType.STRING), tag))
                .count();
    }
}
