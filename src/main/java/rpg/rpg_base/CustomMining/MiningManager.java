package rpg.rpg_base.CustomMining;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.RPG_Base;

import java.io.File;
import java.util.*;

public class MiningManager implements Listener {
    private static RPG_Base plugin;
    private static final Random random = new Random();

    public MiningManager(RPG_Base plugin){
        MiningManager.plugin = plugin;
    }

    @EventHandler
    public static void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (isBlockInRegionWithFlag(block, MiningFlags.customBlockMechanics)) {
            if (!block.getType().equals(Material.COBBLESTONE)) {
                String regionId = getBlockRegion(block).getId();
                File file = new File(RPG_Base.getInstance().getDataFolder(), "WG/blocks/" + block.getWorld().getName() +"/" + regionId + ".yml");
                YamlConfiguration blockFile = YamlConfiguration.loadConfiguration(file);

//                System.out.println("Contents of blockFile:");
//                for (String key : blockFile.getKeys(true)) {
//                    System.out.println(key + ": " + blockFile.get(key));
//                }

                String BlockmaterialName = block.getWorld().getBlockAt(block.getLocation()).getBlockData().getMaterial().name().toLowerCase();
                String key = "spawn." + BlockmaterialName;

                if (blockFile.contains(key)) {
                    BlockChanceSelector selector = new BlockChanceSelector();

                    List<String> spawnList = blockFile.getStringList(key);
                    for (String blockMaterial : spawnList) {
                        String materialName = blockMaterial.split(",")[0].toUpperCase();
                        float chance = Float.parseFloat(blockMaterial.split(",")[1]);
                        selector.addBlockChance(Material.matchMaterial(materialName), chance);
                    }

                    String selectedMaterialName = selector.selectBlock();
                    Material selectedMaterial = Material.matchMaterial(selectedMaterialName);
//                    System.out.println(selectedMaterialName);
                    if (selectedMaterial != null) {
                        new BlockSpawner(block, selectedMaterial).runTaskLater(plugin, (random.nextInt(201) + 600));
                    }else{
                        new BlockSpawner(block, Material.DIRT).runTaskLater(plugin, (random.nextInt(201) + 600));
                    }
                    block.setType(Material.COBBLESTONE);

                } else {
//                    System.out.println("No spawn list defined for this block material.");
                }

                String key1 = "drops." + BlockmaterialName;

                if (blockFile.contains(key1)) {
                    BlockChanceSelector selector = new BlockChanceSelector();

                    List<String> dropList = blockFile.getStringList(key1);
                    for (String blockDrop : dropList) {
                        ItemStack item = new ItemStack(Material.DIRT);

                        Material matchedMaterial = Material.matchMaterial(blockDrop.split(",")[0].toUpperCase());
                        if (matchedMaterial != null) {
                            item = new ItemStack(matchedMaterial);
                        }else if(ItemHandlers.getCustomItemByName(blockDrop.split(",")[0])!=null){
                            item = ItemHandlers.getCustomItemByName(blockDrop.split(",")[0]);
                        }
                        float chance = Float.parseFloat(blockDrop.split(",")[1]);
                        selector.addDropChance(item, chance);
                    }

                    if(!dropList.isEmpty()){
                        e.getBlock().getDrops().clear();
                        e.getPlayer().getInventory().addItem(selector.itemDrops());
                    }

                }


            } else {
                e.getBlock().getDrops().clear();
                e.getPlayer().getInventory().addItem(new ItemStack(Material.COBBLESTONE));
                block.getWorld().getBlockAt(block.getLocation()).setType(Material.BEDROCK);
            }
            e.setCancelled(true);
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
