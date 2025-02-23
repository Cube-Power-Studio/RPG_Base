package rpg.rpg_base.Mining;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.configuration.file.YamlConfiguration;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;


public class MiningFlagHandler extends FlagValueChangeHandler<State> {

    public static Factory FACTORY()
    {
        return new Factory();
    }
    public static class Factory extends Handler.Factory<MiningFlagHandler>{
        @Override
        public MiningFlagHandler create(Session session) {
            return new MiningFlagHandler(session);
        }
    }
    protected MiningFlagHandler(Session session) {
        super(session, MiningFlags.customBlockMechanics );
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, State state) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            String regionId = region.getId();
            Path filePath;
            if (regionId.equalsIgnoreCase("__global__") || regionId.equalsIgnoreCase("__wglobal__")) {
                // Handle global region or the entire world
                filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/blocks", "global.yml");
            } else {
                // Handle specific regions
                filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/blocks", localPlayer.getWorld().getName(), regionId + ".yml");
            }
            createBlockSections(filePath.toFile());
        }
    }


    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, State state, State t1, MoveType moveType) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            Path filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/blocks", localPlayer.getWorld().getName(), region.getId() + ".yml");
            createBlockSections(filePath.toFile());
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, State state, MoveType moveType) {
        for (ProtectedRegion region : applicableRegionSet.getRegions()) {
            Path filePath = Paths.get(RPG_Base.getInstance().getDataFolder().getPath(), "WG/blocks", localPlayer.getWorld().getName(), region.getId() + ".yml");
            createBlockSections(filePath.toFile());
        }

        return true;
    }
    private void createBlockSections(File file) {
        if (!file.exists()) {
            RPG_Base.getInstance().getLogger().info("Creating blocks sections in " + file.getPath());
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            try {
                cfg.save(file);
                RPG_Base.getInstance().getLogger().info("Block sections successfully created in " + file.getPath());
            } catch (IOException e) {
                RPG_Base.getInstance().getLogger().log(Level.SEVERE, "Failed to save YAML configuration to " + file.getPath(), e);
            }
        }
    }
}
