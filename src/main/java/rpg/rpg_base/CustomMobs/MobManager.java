    package rpg.rpg_base.CustomMobs;

    import com.sk89q.worldedit.bukkit.BukkitAdapter;
    import com.sk89q.worldedit.math.BlockVector3;
    import com.sk89q.worldguard.WorldGuard;
    import com.sk89q.worldguard.protection.ApplicableRegionSet;
    import com.sk89q.worldguard.protection.managers.RegionManager;
    import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
    import rpg.rpg_base.CustomItemsManager.ItemHandlers;
    import rpg.rpg_base.RPG_Base;
    import rpg.rpg_base.StatManager.DamageManager;
    import rpg.rpg_base.StatManager.HealthManager;

    import java.io.File;
    import java.util.HashMap;
    import java.util.List;
    import java.util.UUID;


    public class MobManager implements Listener {

        private static final HashMap<UUID, List<String>> customEntitiesDrops = new HashMap<>();
        @EventHandler
        public static void spawnEntity(EntitySpawnEvent event) {

            Entity entity = event.getEntity();

            if (entity instanceof LivingEntity && !(entity instanceof Player)) {


                com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(entity.getWorld());
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);
                BlockVector3 location = BlockVector3.at(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
                ApplicableRegionSet regions = regionManager.getApplicableRegions(location);

                for (ProtectedRegion region : regions) {
                    File regionFile = new File(RPG_Base.getInstance().getDataFolder() + "/WG/" + region.getId() + ".yml");
                    YamlConfiguration regionConfig = YamlConfiguration.loadConfiguration(regionFile);

                    String entityTypeName = entity.getName().toLowerCase();

                    if (!entityTypeName.equals("player") && !entityTypeName.equals("armor stand") && !entityTypeName.equals("glow squid")) {
                        if (regionConfig.isConfigurationSection(entityTypeName)) {
                            if (regionConfig.getBoolean(entityTypeName + ".spawn")) {
                                entity.addScoreboardTag("custom_mob");
                                int levelMin = regionConfig.getInt(entityTypeName + ".minlvl", -1);
                                int levelMax = regionConfig.getInt(entityTypeName + ".maxlvl", -1);

                                DamageManager.setEntityDamage(entity.getUniqueId(), regionConfig.getInt(entityTypeName + ".damage", 10));
                                List<String> drops = regionConfig.getStringList(entityTypeName + ".drops");

                                customEntitiesDrops.put(entity.getUniqueId(), drops);
                                if (levelMin != -1 && levelMax != -1) {
                                    MobLevelManager.setEntityLevel(entity.getUniqueId(), levelMin, levelMax);

                                } else {
                                    System.out.println("Missing or invalid minlvl or maxlvl for " + entityTypeName);
                                }
                            } else {
                                event.setCancelled(true);
                            }
                        } else {
                            System.out.println("Entity section is missing for " + entityTypeName);
                        }
                    }
                }
            }
            if (entity.getType().isAlive()) {
                entity.setCustomName(ChatColor.GOLD + "[" + MobLevelManager.getEntityLevel(entity.getUniqueId()) + "Lvl] - " + ChatColor.RESET + entity.getName() + " " + ChatColor.RED + HealthManager.getEntityHealth(entity.getUniqueId()) + "/" + HealthManager.getEntityMaxHealth((entity.getUniqueId())) + "❤");
                entity.setCustomNameVisible(true);
            }
        }

        @EventHandler
        public static void onEntityDamage(EntityDamageEvent event) {
            Entity entity = event.getEntity();
            if (entity.getType().isAlive()) {
                entity.setCustomNameVisible(true);
                entity.setCustomName(null);
                entity.setCustomName(ChatColor.GOLD + "[" + MobLevelManager.getEntityLevel(entity.getUniqueId()) + "Lvl] - " + ChatColor.RESET + entity.getName() + ChatColor.RED + HealthManager.getEntityHealth(entity.getUniqueId()) + "/" + HealthManager.getEntityMaxHealth((entity.getUniqueId())));
            }
        }

        @EventHandler
        public static void onEntityKill(EntityDeathEvent event) {
            event.getDrops().clear();
            Entity entity = event.getEntity();

            if (!(entity instanceof Player)) {
                com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(entity.getWorld());
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);
                BlockVector3 location = BlockVector3.at(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
                ApplicableRegionSet regions = regionManager.getApplicableRegions(location);

                for (ProtectedRegion region : regions) {
                    File regionFile = new File(RPG_Base.getInstance().getDataFolder() + "/WG/" + region.getId() + ".yml");
                    YamlConfiguration regionConfig = YamlConfiguration.loadConfiguration(regionFile);
                    String entityTypeName = entity.getName().toLowerCase();

                    if (!entityTypeName.equals("player") && !entityTypeName.equals("armor stand") && !entityTypeName.equals("glow squid")) {
                        if (entity.getType().isAlive() && customEntitiesDrops.containsKey(entity.getUniqueId())) {

                            // Get the drops from the YAML file
                            List<String> customDrops = customEntitiesDrops.get(entity.getUniqueId());

                            for (String customDrop : customDrops) {
                                Material material = Material.matchMaterial(customDrop);

                                if (material != null) {
                                    // If the string represents a valid material, create ItemStack with that material
                                    ItemStack itemStack = new ItemStack(material);
                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), itemStack);
                                } else {
                                    // If it's not a valid material, assume it's a custom item identifier and handle accordingly
                                    ItemStack customItemStack = ItemHandlers.getCustomItemByName(customDrop);

                                    if (customItemStack != null) {
                                        entity.getLocation().getWorld().dropItem(entity.getLocation(), customItemStack);
                                    } else {
                                        // Handle the case when the custom item is not found
                                        // For now, let's print a message to the console
                                        RPG_Base.getInstance().getLogger().warning("Custom item not found: " + customDrop);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }