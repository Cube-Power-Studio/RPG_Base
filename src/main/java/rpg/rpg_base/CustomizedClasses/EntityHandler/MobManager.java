package rpg.rpg_base.CustomizedClasses.EntityHandler;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.Utils.Util;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.Arrays;
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
        BukkitRunnable checkSpawn = new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getEntity() instanceof LivingEntity) {
                    if (!CitizensAPI.getNPCRegistry().isNPC(e.getEntity())
                            && !(e.getEntity() instanceof Player)
                            && CEntity.getEntityByUUID(e.getEntity().getUniqueId()) == null) {
                        e.setCancelled(true);
                    }
                }
            }
        };

        checkSpawn.runTaskLater(RPG_Base.getInstance(), 5);
    }

    @EventHandler
    public void despawnUnregisteredEntities(EntityMoveEvent e) {
        if (CEntity.getEntityByUUID(e.getEntity().getUniqueId()) == null
            && !(e.getEntity() instanceof Player)
            && !CitizensAPI.getNPCRegistry().isNPC(e.getEntity())
            && e.getEntity().getType().isAlive()) {
            e.getEntity().remove();
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        if(!(e.getEntity() instanceof Player)) {
            e.getDrops().clear();
            if(CEntity.customEntities.containsKey(e.getEntity().getUniqueId())){
                for (CItem item : CEntity.customEntities.get(e.getEntity().getUniqueId()).dropList) {
                    e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), item.getItem());
                }
                CEntity.customEntities.remove(e.getEntity().getUniqueId());
            }
            e.setDroppedExp(0);
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

        entity.maxHP = (int) (entity.baseMaxHp + entity.baseMaxHp * entity.level * entity.hpScalePerLvl);
        entity.currentHP = entity.maxHP;
        entity.damage = (int) (entity.baseDamage + entity.baseDamage * entity.level * entity.dmgScalePerLvl);

        entity.spawnLocation = location;

        entity.entity = (Mob) location.getWorld().spawnEntity(location, EntityType.valueOf(entity.mobType));

        if (entity instanceof Zombie) {
            ((Zombie)entity).setAdult();
        }

        if (entity instanceof Zoglin) {
            ((Zoglin)entity).setAdult();
        }

        if (entity instanceof Piglin) {
            ((Piglin)entity).setAdult();
        }

        entity.getEntity().getEquipment().clear();

        MobDrops mobDrops = new MobDrops();

        for(String drop : entity.possibleDrops){
            String[] dropParameters = drop.split(",");
            String[] dropMinMax = dropParameters[2].split("-");

            int dropMin = Integer.parseInt(dropMinMax[0]);
            int dropMax = Integer.parseInt(dropMinMax[1]);

            CItem item = CItem.customItemsByName.get(dropParameters[0]);

            double chance = Double.parseDouble(dropParameters[1]);


            mobDrops.addDropChance(item, chance, dropMin, dropMax);
        }

        entity.dropList = Arrays.asList(mobDrops.itemDrops());

        entity.entity.getPersistentDataContainer().set(CEntity.mobTypeKey, PersistentDataType.STRING, entity.customEntityType);

        entity.entity.setCustomNameVisible(true);
        entity.updateDisplayName();

        entity.startAi();
        entity.updateDisplayName();

        CEntity.customEntities.put(entity.getEntity().getUniqueId(), entity);
    }
    public static void spawnMob(String mobName, Location location, ProtectedRegion region){
        CEntity entity = CEntity.customEntitiesTemplates.get(mobName).clone();

        Random random = new Random();

        entity.level = random.nextInt(entity.lvlMin, entity.lvlMax);

        entity.maxHP = (int) (entity.baseMaxHp + entity.baseMaxHp * entity.level * entity.hpScalePerLvl);
        entity.currentHP = entity.maxHP;
        entity.damage = (int) (entity.baseDamage + entity.baseDamage * entity.level * entity.dmgScalePerLvl);

        entity.spawnLocation = location;

        entity.entity = (Mob) location.getWorld().spawnEntity(location, EntityType.valueOf(entity.mobType));

        if (entity instanceof Zombie) {
            ((Zombie)entity).setAdult();
        }

        if (entity instanceof Zoglin) {
            ((Zoglin)entity).setAdult();
        }

        if (entity instanceof Piglin) {
            ((Piglin)entity).setAdult();
        }

        entity.getEntity().getEquipment().clear();

        MobDrops mobDrops = new MobDrops();

        for(String drop : entity.possibleDrops){
            String[] dropParameters = drop.split(",");
            String[] dropMinMax = dropParameters[2].split("-");

            int dropMin = Integer.parseInt(dropMinMax[0]);
            int dropMax = Integer.parseInt(dropMinMax[1]);

            CItem item = CItem.customItemsByName.get(dropParameters[0]);

            double chance = Double.parseDouble(dropParameters[1]);


            mobDrops.addDropChance(item, chance, dropMin, dropMax);
        }

        entity.dropList = Arrays.asList(mobDrops.itemDrops());

        entity.region = region.getId();
        entity.entity.getPersistentDataContainer().set(CEntity.regionKey, PersistentDataType.STRING, region.getId());
        entity.entity.getPersistentDataContainer().set(CEntity.mobTypeKey, PersistentDataType.STRING, entity.customEntityType);

        entity.entity.setCustomNameVisible(true);
        entity.updateDisplayName();

        entity.startAi();
        entity.updateDisplayName();
        entity.getEntity().setPersistent(true);
        entity.getEntity().setRemoveWhenFarAway(false);

        CEntity.customEntities.put(entity.getEntity().getUniqueId(), entity);
    }
}
