package rpg.rpg_base.CustomizedClasses.Entities.MobClasses;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawnManager;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawningNode;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.*;

public class MobManager implements Listener {
    static Map<String, RpgMob> mobTemplatesMap = new HashMap<>();
    static Map<UUID, RpgMob> mobMap = new HashMap<>();

    private static BukkitRunnable mobSpawnTask;

    public static void registerTemplate(String key, RpgMob template){
        mobTemplatesMap.put(key, template);
    }

    public static void resetTemplates(){
        mobTemplatesMap.clear();
    }

    public static RpgMob getMobTemplate(String regKey){
        return  mobTemplatesMap.get(regKey);
    }

    public static void registerMob(RpgMob mob){
        mobMap.put(mob.getEntity().getUniqueId(), mob);
    }

    public static RpgMob getMob(UUID uuid){
        return mobMap.getOrDefault(uuid, null);
    }

    public static Map<UUID, RpgMob> getMobsMap(){
        return mobMap;
    }

    public static void removeMob(RpgMob mob){
        mobMap.remove(mob.getEntity().getUniqueId());
    }

    public static Map<String, RpgMob> getMobTemplates() {
        return mobTemplatesMap;
    }

    public static void loadMobs(){
        mobTemplatesMap.clear();

        File mobsFolder = new File(RPG_Base.getInstance().getDataFolder() + "/custom_mobs");

        for(File mobFile : mobsFolder.listFiles()) {
            YamlConfiguration mobConfigs = YamlConfiguration.loadConfiguration(mobFile);

            for(String mobKey : mobConfigs.getKeys(false)){
                registerTemplate(mobKey, RpgMob.load(mobConfigs.getConfigurationSection(mobKey)));
            }
        }
    }

    public static RpgMob spawnMob(String mobName, Location location, String nodeId){
        RpgMob entity = MobManager.getMobTemplate(mobName).clone();

        Random random = new Random();

        entity.level = random.nextInt(entity.minLevel, entity.maxLevel);

        entity.spawnLocation = location;

        entity.entity = (Mob) location.getWorld().spawnEntity(location, EntityType.valueOf(entity.mcMobType.toUpperCase()));

        if (entity.entity instanceof Zombie) {
            ((Zombie)entity.entity).setAdult();
        }

        if (entity.entity instanceof ZombieVillager) {
            ((ZombieVillager)entity.entity).setAdult();
        }

        if (entity.entity instanceof Zoglin) {
            ((Zoglin)entity.entity).setAdult();
        }

        if (entity.entity instanceof Piglin) {
            ((Piglin)entity.entity).setAdult();
        }

        entity.getEntity().getEquipment().clear();

        if (entity.entity instanceof Pillager){
            entity.entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        }

        entity.entity.getPersistentDataContainer().set(RpgMob.mobTypeKey, PersistentDataType.STRING, entity.mobType);
        entity.entity.getPersistentDataContainer().set(RpgMob.customMobKey, PersistentDataType.BYTE, (byte) 1);

        entity.entity.setCustomNameVisible(true);
        entity.update();

        entity.update();
        entity.heal(entity.getMaxHp());
        entity.startAi();
        entity.getEntity().setPersistent(true);
        entity.getEntity().setRemoveWhenFarAway(false);
        entity.entity.getPersistentDataContainer().set(RpgMob.nodeId, PersistentDataType.STRING, nodeId);

        registerMob(entity);

        return entity;
    }

    @EventHandler
    public void cancelEntitySpawn(CreatureSpawnEvent event){
        BukkitRunnable checkSpawn = new BukkitRunnable() {
            @Override
            public void run() {
                if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL ||
                   event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT){

                    event.setCancelled(true);
                    event.getEntity().remove();
                }
            }
        };

        checkSpawn.runTaskLater(RPG_Base.getInstance(), 5);
    }

    @EventHandler
    public void removeUnregisteredEntities(EntityMoveEvent event) {
        byte flag = Optional.ofNullable(
                event.getEntity().getPersistentDataContainer().get(RpgMob.customMobKey, PersistentDataType.BYTE)
        ).orElse((byte) 0);

        if (flag == (byte) 1 && getMob(event.getEntity().getUniqueId()) == null) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event){
        System.out.println(event.getEntity());
        if(getMob(event.getEntity().getUniqueId()) != null){
            RpgMob mob = getMob(event.getEntity().getUniqueId());
            SpawningNode spawningNode = SpawnManager.getSpawningNode(mob.entity.getPersistentDataContainer().get(RpgMob.nodeId, PersistentDataType.STRING));
            spawningNode.removeCurrentSpawnedMob();
            spawningNode.spawnMob();
            mob.killEntity(event);
        }
    }

}
