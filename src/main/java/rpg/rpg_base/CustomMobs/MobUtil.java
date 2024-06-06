package rpg.rpg_base.CustomMobs;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.entity.Entity;

import java.util.*;

public class MobUtil {
    // Get all entities in a specified region
    public static List<Entity> getEntitiesInRegion(World world, String regionId) {
        List<Entity> entities = new ArrayList<>();

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(new BukkitWorld(world));
        if (regions == null) {
            return entities;
        }

        ProtectedRegion region = regions.getRegion(regionId);
        if (region == null) {
            return entities;
        }

        // Get the minimum and maximum points of the region
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        // Iterate through all entities in the world and check if they are inside the region
        for (Entity entity : world.getEntities()) {
            Location loc = entity.getLocation();
            if (isWithinBounds(loc, min, max)) {
                entities.add(entity);
            }
        }
        return entities;
    }
    public static int getEntitiesWithTag(String tag, World world){
        int i = 0;

        for(Entity entity : world.getEntities()){
            if (entity.getScoreboardTags().contains(tag)){
                i++;
            }
        }
        return i;
    }

    // Helper method to check if a location is within bounds
    private static boolean isWithinBounds(Location loc, BlockVector3 min, BlockVector3 max) {
        return loc.getBlockX() >= min.getBlockX() && loc.getBlockX() <= max.getBlockX()
                && loc.getBlockY() >= min.getBlockY() && loc.getBlockY() <= max.getBlockY()
                && loc.getBlockZ() >= min.getBlockZ() && loc.getBlockZ() <= max.getBlockZ();
    }
}
