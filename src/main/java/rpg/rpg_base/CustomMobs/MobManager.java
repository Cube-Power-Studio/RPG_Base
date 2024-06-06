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
    import org.bukkit.ChatColor;
    import org.bukkit.Material;
    import org.bukkit.configuration.file.YamlConfiguration;
    import org.bukkit.entity.Entity;
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

    import java.io.File;
    import java.util.*;


    public class MobManager implements Listener {

        private static final HashMap<UUID, List<String>> customEntitiesDrops = new HashMap<>();
        private static final List<UUID> mobList = new ArrayList<>();
        public static RPG_Base rpg_base;

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
                        if(region.getFlag(MobFlags.customMobsFlag) == StateFlag.State.ALLOW){
                            if (mobList.contains(entity.getUniqueId())) {

                            }else{
                                System.out.println("Canceled spawn event for: " + event.getEntity().getName());
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (entity.getType().isAlive()) {
                        entity.setCustomName(ChatColor.GOLD + "[" + MobLevelManager.getEntityLevel(entity.getUniqueId()) + "Lvl] - " + ChatColor.RESET + entity.getName() + " " + ChatColor.RED + HealthManager.getEntityHealth(entity.getUniqueId()) + "/" + HealthManager.getEntityMaxHealth((entity.getUniqueId())) + "❤");
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
            event.getDrops().clear();
            Entity entity = event.getEntity();
            entity.setCustomName(null);

            if (!(entity instanceof Player)) {
                String entityTypeName = entity.getName().toLowerCase();
                if(mobList.contains(entity.getUniqueId())) {
                    if (!entityTypeName.equals("player") && !entityTypeName.equals("armor stand") && !entityTypeName.equals("glow squid")) {
                        if (entity.getType().isAlive() && customEntitiesDrops.containsKey(entity.getUniqueId())) {

//                            // Get the drops from the YAML file
//                            List<String> customDrops = customEntitiesDrops.get(entity.getUniqueId());
//
//                            for (String customDrop : customDrops) {
//                                Material material = Material.matchMaterial(customDrop);
//
//                                if (material != null) {
//                                    // If the string represents a valid material, create ItemStack with that material
//                                    ItemStack itemStack = new ItemStack(material);
//                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), itemStack);
//                                } else {
//                                    // If it's not a valid material, assume it's a custom item identifier and handle accordingly
//                                    ItemStack customItemStack = ItemHandlers.getCustomItemByName(customDrop);
//
//                                    if (customItemStack != null) {
//                                        entity.getLocation().getWorld().dropItem(entity.getLocation(), customItemStack);
//                                    } else {
//                                        // Handle the case when the custom item is not found
//                                        // For now, let's print a message to the console
//                                        RPG_Base.getInstance().getLogger().warning("Custom item not found: " + customDrop + " For mob: " + entityTypeName);
//                                    }
//                                }
//                            }

                        }
                    }
                }
            }

            World world = BukkitAdapter.adapt(entity.getWorld());
            RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            if(entity.getPersistentDataContainer().get(CustomEntity.regionKey, PersistentDataType.STRING) != null) {
                System.out.println("SPAWNING MOB");
                ProtectedRegion protectedRegion = regionContainer.get(world).getRegion(entity.getPersistentDataContainer().get(CustomEntity.regionKey, PersistentDataType.STRING));
                new SpawnMobs(rpg_base, protectedRegion, entity.getWorld()).run();
            }else{
                System.out.println("Nothing happened");
            }
        }
        public static void registerMob(UUID uuid){
            mobList.add(uuid);
        }
    }