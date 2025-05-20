package rpg.rpg_base.CustomizedClasses.MiningHandler;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.*;

public class MiningManager implements Listener {
    private static RPG_Base plugin;

    public MiningManager(RPG_Base plugin){
        MiningManager.plugin = plugin;
    }

    @EventHandler
    public static void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        e.setDropItems(false); // Clear the drops

        if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            if (isBlockInRegionWithFlag(block, MiningFlags.customBlockMechanics)) {
                e.setCancelled(true);

                String regionId = getBlockRegion(block).getId();
                File file = new File(RPG_Base.getInstance().getDataFolder(), "WG/blocks/" + block.getWorld().getName() + "/" + regionId + ".yml");
                YamlConfiguration blockFile = YamlConfiguration.loadConfiguration(file);

                String materialOfBlockName = block.getType().name().toLowerCase();
                String key = "spawn." + materialOfBlockName;

                if (blockFile.contains(key)) {
                    Block replacement = block.getWorld().getBlockAt(block.getLocation());

                    BlockChanceSelector selector = new BlockChanceSelector();

                    List<String> spawnList = blockFile.getStringList(key + ".spawns");
                    for (String blockMaterial : spawnList) {
                        String[] parts = blockMaterial.split(",");
                        String materialName = parts[0].toUpperCase();
                        float chance = Float.parseFloat(parts[1]);
                        selector.addBlockChance(Material.matchMaterial(materialName), chance);
                    }

                    String selectedMaterialName = selector.selectBlock();
                    Material selectedMaterial = Material.matchMaterial(selectedMaterialName);

                    if (selectedMaterial != null) {
                        new BlockSpawner(block, selectedMaterial).runTaskLater(plugin, blockFile.getLong(key + ".respawnTimer") * 20L);
                    } else {
                        Bukkit.getLogger().info("No valid material selected, defaulting to ");
                        new BlockSpawner(block, Material.JIGSAW).runTaskLater(plugin, blockFile.getLong(key + ".respawnTimer") * 20L);
                    }

                    String breakReplaceKey = key + ".breakReplace";
                    if (blockFile.contains(breakReplaceKey) && blockFile.getString(breakReplaceKey) != null) {
                        Material breakReplaceMaterial = Material.matchMaterial(blockFile.getString(breakReplaceKey));
                        if (breakReplaceMaterial != null) {
                            replacement.setType(breakReplaceMaterial);
                        } else {
                            replacement.setType(Material.BEDROCK);
                            Bukkit.getLogger().info("Invalid breakReplace material, defaulting to BEDROCK");
                        }
                    } else {
                        replacement.setType(Material.BEDROCK);
                    }
                }


                String key1 = "drops." + materialOfBlockName;

                if (blockFile.contains(key1)) {
                    BlockChanceSelector dropSelector = new BlockChanceSelector();

                    List<String> dropList = blockFile.getStringList(key1 + ".drops");
                    for (String blockDrop : dropList) {

                        String[] dropParameters = blockDrop.split(",");
                        String[] dropMinMax;
                        int minDrop;
                        int maxDrop;
                        if (dropParameters[2].contains("-")) {
                            dropMinMax = dropParameters[2].split("-");
                            minDrop = Integer.parseInt(dropMinMax[0]);
                            maxDrop = Integer.parseInt(dropMinMax[1]);
                        } else {
                            minDrop = Integer.parseInt(dropParameters[2]);
                            maxDrop = Integer.parseInt(dropParameters[2]);
                        }

                        String materialName = dropParameters[0].toUpperCase();
                        double chance = Float.parseFloat(dropParameters[1]);

                        ItemStack item = new ItemStack(Material.DIRT);

                        if (CItem.customItemsByName.get(dropParameters[0]).getItem() != null) {
                            item = CItem.customItemsByName.get(dropParameters[0]).getItem();
                        }

                        if (item != null) {
                            dropSelector.addDropChance(item, chance, minDrop, maxDrop);
                        } else {
                            Bukkit.getLogger().warning("Invalid drop material: " + materialName);
                        }
                    }

                    if (!dropList.isEmpty()) {
                        ItemStack[] droppedItems = dropSelector.itemDrops();
                        for (ItemStack item : droppedItems) {
                            block.getWorld().dropItem(block.getLocation(), item);
                        }
                    }
                } else {
                    Bukkit.getLogger().warning("No drop configuration found for key: " + key1);
                }
            }
        }
    }

    private static boolean isBlockInRegionWithFlag(Block block, StateFlag flag) {
        World weWorld = BukkitAdapter.adapt(block.getWorld());
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);
        if (regionManager == null) return false;

        BlockVector3 location = BlockVector3.at(block.getX(), block.getY(), block.getZ());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);

        return regions.queryState(null, flag) == StateFlag.State.ALLOW;
    }
    private static ProtectedRegion getBlockRegion(Block block) {
        World weWorld = BukkitAdapter.adapt(block.getWorld());
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(weWorld);
        BlockVector3 location = BlockVector3.at(block.getX(), block.getY(), block.getZ());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);

        // Check if any region is applicable to the block
        if (!regions.getRegions().isEmpty()) {
            // Return the first region found (you may need to adjust this logic)
            ProtectedRegion region = regions.getRegions().iterator().next();
            for(ProtectedRegion regionIterator: regions){
                if(regionIterator.getPriority() > region.getPriority()){
                    region = regionIterator;
                }
            }
            return region;
        } else {
            // If no specific region found, return the default world region
            ProtectedRegion defaultRegion = regionManager.getRegion("__global__");
            if (defaultRegion != null) {
                Bukkit.getLogger().info("No region found for block at " + location + ". Using default world region.");
                return defaultRegion;
            } else {
                Bukkit.getLogger().warning("No region found for block at " + location + " and no default world region found.");
                return null; // No region found for the block and no default world region defined
            }
        }
    }
}
