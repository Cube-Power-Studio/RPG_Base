package rpg.rpg_base.CustomizedClasses.EntityHandler;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Utils.Util;

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
        if(!WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).hasRegion(region.getId())){
            return;
        }
        if(region.getFlag(MobFlags.customMobsFlag) == StateFlag.State.ALLOW){
            String regionID = region.getId();

            File path = new File(RPG_Base.getInstance().getDataFolder(), "/WG/mobs/" + world.getName() + "/" + regionID + ".yml");
            if (!path.exists()) {
                System.out.println("Region file does not exist: " + path.getAbsolutePath());
                return;
            }

            YamlConfiguration regionFile = YamlConfiguration.loadConfiguration(path);
            int maxNumberOfMobsInRegion = regionFile.getInt("numberOfMobsInRegion");
            int currentMobsInRegion = getEntitiesWithRegionTag(regionID);
//            System.out.println("Entities in region: " + regionID + ":" + currentMobsInRegion);
            int scheduledSpawnsInRegion = scheduledSpawns.getOrDefault(region, 0);
//            System.out.println("Scheduled entities in region: " + regionID + ":" + scheduledSpawnsInRegion);

            if (currentMobsInRegion + scheduledSpawnsInRegion <= maxNumberOfMobsInRegion) {
                int mobsNeeded = maxNumberOfMobsInRegion - (currentMobsInRegion + scheduledSpawnsInRegion);
                if (!scheduledRegions.contains(region)) {
                    scheduledRegions.add(region);
                }

                Random random = new Random();

                for (int i = 0; i < mobsNeeded; i++) {
                    //System.out.println("DODANO MOBA DO " + regionID);
                    new SpawnEntitiesInRegion(util, region, world).runTaskLater(plugin, random.nextInt(100) + 20);
                }
                //System.out.println(mobsNeeded + " Potrzebnych mobów");
                scheduledSpawns.put(region, scheduledSpawnsInRegion + mobsNeeded);
            } else {
                scheduledSpawns.remove(region);
                scheduledRegions.remove(region);
            }
        }
    }

    private int getEntitiesWithRegionTag(String regionId) {
        return (int) CEntity.customEntities.values().stream()
                .filter(Objects::nonNull) // Ensure entity isn't null
                .filter(entity -> entity.getEntity() != null && !entity.getEntity().isDead()) // Ensure entity exists & isn't dead
                .filter(entity -> Objects.equals(
                        entity.getEntity().getPersistentDataContainer().get(CEntity.regionKey, PersistentDataType.STRING),
                        regionId
                ))
                .count();
    }

}
