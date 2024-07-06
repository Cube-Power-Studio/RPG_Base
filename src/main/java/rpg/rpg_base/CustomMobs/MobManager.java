    package rpg.rpg_base.CustomMobs;

    import com.sk89q.worldedit.bukkit.BukkitAdapter;
    import com.sk89q.worldedit.math.BlockVector3;
    import com.sk89q.worldedit.world.World;
    import com.sk89q.worldguard.WorldGuard;
    import com.sk89q.worldguard.protection.ApplicableRegionSet;
    import com.sk89q.worldguard.protection.flags.StateFlag;
    import com.sk89q.worldguard.protection.managers.RegionManager;
    import com.sk89q.worldguard.protection.regions.ProtectedRegion;

    import com.sk89q.worldguard.protection.regions.RegionContainer;
    import org.bukkit.Bukkit;
    import org.bukkit.ChatColor;
    import org.bukkit.Material;
    import org.bukkit.NamespacedKey;
    import org.bukkit.configuration.file.YamlConfiguration;
    import org.bukkit.entity.Entity;
    import org.bukkit.entity.EntityType;
    import org.bukkit.entity.LivingEntity;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.entity.EntityDamageEvent;
    import org.bukkit.event.entity.EntityDeathEvent;
    import org.bukkit.event.entity.EntitySpawnEvent;
    import org.bukkit.inventory.ItemStack;
    import org.bukkit.persistence.PersistentDataType;
    import org.bukkit.scheduler.BukkitRunnable;
    import org.bukkit.util.BlockVector;
    import rpg.rpg_base.CustomItemsManager.ItemHandlers;
    import rpg.rpg_base.RPG_Base;
    import rpg.rpg_base.StatManager.DamageManager;
    import rpg.rpg_base.StatManager.HealthManager;
    import rpg.rpg_base.StatManager.LevelManager;

    import java.io.File;
    import java.util.*;


    public class MobManager implements Listener {

        private static final List<UUID> mobList = new ArrayList<>();
        public static final HashMap<UUID, ItemStack[]> mobDrops = new HashMap<>();
        public static final HashMap<UUID, Integer> mobXpDrops = new HashMap<>();
        public static final HashMap<UUID, String> mobNames = new HashMap<>();
        public static RPG_Base rpg_base;
        public static NamespacedKey killerKey = new NamespacedKey(RPG_Base.getInstance(), "killer");;

        public MobManager(RPG_Base rpg_base){
            MobManager.rpg_base = rpg_base;
        }

        @EventHandler
        public void spawnEntity(EntitySpawnEvent event) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = event.getEntity();

                    World weWorld = BukkitAdapter.adapt(entity.getWorld());
                    RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);
                    BlockVector3 location = BlockVector3.at(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
                    ApplicableRegionSet regions = regionManager.getApplicableRegions(location);

                    for (ProtectedRegion region : regions) {
                        if(!entity.getType().equals(EntityType.ARROW) || !entity.getType().equals(EntityType.SPECTRAL_ARROW)) {
                            if (region.getFlag(MobFlags.customMobsFlag) == StateFlag.State.ALLOW) {
                                if (mobList.contains(entity.getUniqueId())) {
                                    HealthManager.setEntityHealth(entity.getUniqueId(), HealthManager.getEntityMaxHealth(entity.getUniqueId()));
                                    mobDrops.put(entity.getUniqueId(), CustomEntity.droppedItems.get(entity.getUniqueId()));
                                    mobXpDrops.put(entity.getUniqueId(), CustomEntity.droppedXp.get(entity.getUniqueId()));
                                    mobNames.put(entity.getUniqueId(), CustomEntity.mobName.get(entity.getUniqueId()));
                                } else {
                                    System.out.println("Canceled spawn event for: " + event.getEntity().getName());
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                    if (entity.getType().isAlive()) {
                        entity.setCustomName(ChatColor.GOLD + "[" + MobLevelManager.getEntityLevel(entity.getUniqueId()) + "Lvl] - " + ChatColor.RESET + mobNames.get(entity.getUniqueId()) + " " + ChatColor.RED + HealthManager.getEntityHealth(entity.getUniqueId()) + "/" + HealthManager.getEntityMaxHealth((entity.getUniqueId())) + "❤");
                        entity.setCustomNameVisible(true);
                    }
                }
            }.runTaskLater(rpg_base, 10);
            if(event.isCancelled()){
                rpg_base.getLogger().warning("ENTITY SPAWN CANCELLED FOR: " + event.getEntity());
            }
        }

        @EventHandler
        public static void onEntityKill(EntityDeathEvent event) {
            event.setDroppedExp(0);
            event.getDrops().clear();

            new BukkitRunnable() {
                @Override
                public void run() {
                    Entity entity = event.getEntity();
                    Player player = null;
                    if (entity.getPersistentDataContainer().get(killerKey, PersistentDataType.STRING) != null) {
                        player = Bukkit.getServer().getPlayer(UUID.fromString(entity.getPersistentDataContainer().get(killerKey, PersistentDataType.STRING)));
                    }

                    if (player != null) {
                        if (!(entity instanceof Player)) {
                            if (mobList.contains(entity.getUniqueId())) {
                                System.out.println(mobNames.get(entity.getUniqueId()));

                                player.getInventory().addItem(mobDrops.get(entity.getUniqueId()));
                                LevelManager.addXp(player, mobXpDrops.get(entity.getUniqueId()));

                                mobNames.remove(entity.getUniqueId());
                                mobList.remove(entity.getUniqueId());
                                mobDrops.remove(entity.getUniqueId());
                                mobXpDrops.remove(entity.getUniqueId());
                            } else {
                                System.out.println("CANCELLED EVERYTHING FOR " + mobNames.get(entity.getUniqueId()) + "  ||  " + entity.getCustomName());
                            }
                        }
                    }


                    entity.setCustomName(" ");

                    World world = BukkitAdapter.adapt(entity.getWorld());
                    RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    if (entity.getPersistentDataContainer().get(CustomEntity.regionKey, PersistentDataType.STRING) != null) {
                        System.out.println("SPAWNING MOB");
                        ProtectedRegion protectedRegion = regionContainer.get(world).getRegion(entity.getPersistentDataContainer().get(CustomEntity.regionKey, PersistentDataType.STRING));
                        new SpawnMobs(rpg_base, protectedRegion, entity.getWorld()).runTaskLater(rpg_base, 1000);
                    } else {
                        System.out.println("Nothing happened");
                    }
                }
            }.runTaskLater(rpg_base, 1);
        }
        public static void registerMob(UUID uuid){
            mobList.add(uuid);
        }
    }