package rpg.rpg_base.CustomMobs;

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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;


public class MobFlagsHandler extends FlagValueChangeHandler<State> {

    public static Factory FACTORY()
    {
        return new Factory();
    }
    public static class Factory extends Handler.Factory<MobFlagsHandler>{
        @Override
        public MobFlagsHandler create(Session session) {
            return new MobFlagsHandler(session);
        }
    }
    protected MobFlagsHandler(Session session) {
        super(session, MobFlags.customMobsFlag );
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, State state) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            Path filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG", region.getId() + ".yml");

            createEntitySections(filePath.toFile());
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, State state, State t1, MoveType moveType) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            Path filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG", region.getId() + ".yml");

            createEntitySections(filePath.toFile());
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, State state, MoveType moveType) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            Path filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG", region.getId() + ".yml");

            createEntitySections(filePath.toFile());
        }

        return true;
    }
    private void createEntitySections(File file) {
        if(!file.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            for (EntityType entityType : EntityType.values()) {
                Class<? extends Entity> entityClass = entityType.getEntityClass();

                if (entityClass != null && LivingEntity.class.isAssignableFrom(entityClass)) {
                    String entityTypeName = entityType.name().toLowerCase();
                    if (!entityTypeName.equals("player")) {
                        cfg.createSection(entityTypeName + ".minlvl");
                        cfg.createSection(entityTypeName + ".maxlvl");
                        cfg.createSection(entityTypeName + ".spawn");
                        cfg.set(entityTypeName + ".minlvl", 1);
                        cfg.set(entityTypeName + ".maxlvl", 10);
                        cfg.set(entityTypeName + ".spawn", false);
                    }
                }
            }

            try {
                cfg.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
