package rpg.rpg_base.CustomizedClasses.EntityHandler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class MobFlagsHandler extends FlagValueChangeHandler<State> {

    public static Factory FACTORY() {
        return new Factory();
    }

    public static class Factory extends Handler.Factory<MobFlagsHandler> {
        @Override
        public MobFlagsHandler create(Session session) {
            return new MobFlagsHandler(session);
        }
    }

    protected MobFlagsHandler(Session session) {
        super(session, MobFlags.customMobsFlag);
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, State state) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            String regionId = region.getId();
            Path filePath;
            if (regionId.equalsIgnoreCase("__global__") || regionId.equalsIgnoreCase("__wglobal__")) {
                // Handle global region or the entire world
                filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/mobs", "global.yml");
            } else {
                // Handle specific regions
                filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/mobs", localPlayer.getWorld().getName(), regionId + ".yml");
            }
            createEntitySections(filePath.toFile());
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, State oldState, State newState, MoveType moveType) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            Path filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/mobs", localPlayer.getWorld().getName(), region.getId() + ".yml");
            createEntitySections(filePath.toFile());
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, State state, MoveType moveType) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            Path filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/mobs", localPlayer.getWorld().getName(), region.getId() + ".yml");
            createEntitySections(filePath.toFile());
        }
        return true;
    }

    private void createEntitySections(File file) {
        if (!file.exists()) {
            RPG_Base.getInstance().getLogger().info("Creating entity sections in " + file.getPath());
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            for (EntityType entityType : EntityType.values()) {
                Class<? extends Entity> entityClass = entityType.getEntityClass();

                if (entityClass != null && LivingEntity.class.isAssignableFrom(entityClass)) {
                    String entityTypeName = entityType.name().toLowerCase();
                    if (!entityTypeName.equals("player")) {
                        cfg.createSection("numberOfMobsInRegion");
                        cfg.set("numberOfMobsInRegion", 10);
                    }
                }
            }

            try {
                cfg.save(file);
                RPG_Base.getInstance().getLogger().info("Entity sections successfully created in " + file.getPath());
            } catch (IOException e) {
                RPG_Base.getInstance().getLogger().log(Level.SEVERE, "Failed to save YAML configuration to " + file.getPath(), e);
            }
        }
    }
}
