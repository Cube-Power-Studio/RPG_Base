package rpg.rpg_base.CustomizedClasses.EntityHandler;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.Data.Util;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.Random;

public class MobManager implements Listener {
    private final Util util;
    private final EntitySpawner entitySpawner;

    public MobManager(Util util, EntitySpawner entitySpawner) {
        this.util = util;
        this.entitySpawner = entitySpawner;
    }

    public BukkitRunnable spawnMobsInRegions(){
        return new BukkitRunnable() {
            @Override
            public void run() {
                for(World world : Bukkit.getWorlds()){
                    RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
                    for(ProtectedRegion region : regions.getRegions().values()){
                        System.out.println("Spawning entities in region : " + region.getId());
                        if(region.getFlag(MobFlags.customMobsFlag) == StateFlag.State.ALLOW){
                            entitySpawner.spawnEntitiesInRegion(region, world);
                        }
                    }
                }
            }
        };
    }

    @EventHandler
    public void cancelEntitySpawn(EntitySpawnEvent e){
        if (e.getEntity() instanceof LivingEntity) {
            if (!CitizensAPI.getNPCRegistry().isNPC(e.getEntity())
                    && !(e.getEntity() instanceof Player)
                    && CEntity.getEntityByUUID(e.getEntity().getUniqueId()) == null) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void despawnUnregisteredEntities(EntityMoveEvent e) {
        if (
                e.getEntity().getPersistentDataContainer().get(CEntity.mobTypeKey, PersistentDataType.STRING) != null
                && CEntity.getEntityByUUID(e.getEntity().getUniqueId()) == null) {
            e.getEntity().remove();
        }
    }

    public void reloadEntities(){
        CEntity.customEntitiesTemplates.clear();

        File mobsFolder = new File(RPG_Base.getInstance().getDataFolder() + "/custom_mobs");

        for(File mobFile : mobsFolder.listFiles()){
            YamlConfiguration mobConfigs = YamlConfiguration.loadConfiguration(mobFile);

            for(String mobKey : mobConfigs.getKeys(false)){
                CEntity entity = new CEntity(RPG_Base.getInstance(), util);
                entity.loadEntity(mobConfigs.getConfigurationSection(mobKey));
            }
        }
    }

    public static void spawnMob(String mobName, Location location){
        CEntity entity = CEntity.customEntitiesTemplates.get(mobName).clone();

        Random random = new Random();

        entity.level = random.nextInt(entity.lvlMin, entity.lvlMax);

        entity.maxHP = (int) (entity.baseMaxHp * entity.level * entity.hpScalePerLvl);
        entity.currentHP = entity.maxHP;
        entity.damage = (int) (entity.baseDamage * entity.level * entity.dmgScalePerLvl);

        entity.spawnLocation = location;

        entity.entity = (Mob) location.getWorld().spawnEntity(location, EntityType.valueOf(entity.mobType));

        RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = location.getWorld();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return;
        }

        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        ProtectedRegion region = regions.getRegions().stream().findFirst().orElse(regionManager.getRegion("__global__"));

        entity.entity.getPersistentDataContainer().set(CEntity.regionKey, PersistentDataType.STRING , region.getId());
        entity.entity.getPersistentDataContainer().set(CEntity.mobTypeKey, PersistentDataType.STRING, entity.customEntityType);

        entity.entity.setCustomNameVisible(true);
        entity.updateDisplayName();

        entity.startAi();

        CEntity.customEntities.put(entity.getEntity().getUniqueId(), entity);
    }
}
