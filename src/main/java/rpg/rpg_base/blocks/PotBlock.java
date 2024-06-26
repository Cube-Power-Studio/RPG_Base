package rpg.rpg_base.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.data.NMSAbstraction;
import rpg.rpg_base.tags.IntTag;
import rpg.rpg_base.tags.StringTag;
import rpg.rpg_base.tags.Tag;

import java.util.HashMap;
import java.util.Map;

public class PotBlock {
    private Material potItem;
    private int potItemData;

    private static final Map<String, Material> potItemList;

    static {
        potItemList = new HashMap<>();
        potItemList.put("", Material.AIR);
        potItemList.put("minecraft:red_flower", Material.POPPY);
        potItemList.put("minecraft:yellow_flower", Material.DANDELION);
        potItemList.put("minecraft:sapling", Material.LEGACY_SAPLING);
        potItemList.put("minecraft:red_mushroom", Material.RED_MUSHROOM);
        potItemList.put("minecraft:brown_mushroom", Material.BROWN_MUSHROOM);
        potItemList.put("minecraft:cactus", Material.CACTUS);
        potItemList.put("minecraft:deadbush", Material.DEAD_BUSH);
        potItemList.put("minecraft:tallgrass", Material.TALL_GRASS);
    }

    public boolean set(NMSAbstraction nms, Block block) {
        if(potItem != Material.AIR){
            nms.setFlowerPotBlock(block, new ItemStack(potItem, 1,(short) potItemData));
        }
        return true;
    }

    public boolean prep(Map<String, Tag> tileData) {
        // Initialize as default
        potItem = Material.AIR;
        potItemData = 0;
        try {
            if(tileData.containsKey("Item")){

                // Get the item in the pot
                if (tileData.get("Item") instanceof IntTag) {
                    // Item is a number, not a material
                    int id = ((IntTag) tileData.get("Item")).getValue();
                    potItem = Material.getMaterial(String.valueOf(id));
                    // Check it's a viable pot item
                    if (!potItemList.containsValue(potItem)) {
                        // No, so reset to AIR
                        potItem = Material.AIR;
                    }
                } else if (tileData.get("Item") instanceof StringTag) {
                    // Item is a material
                    String itemName = ((StringTag) tileData.get("Item")).getValue();
                    if (potItemList.containsKey(itemName)){
                        // Check it's a viable pot item
                        if (potItemList.containsKey(itemName)) {
                            potItem = potItemList.get(itemName);
                        }
                    }
                }

                if(tileData.containsKey("Data")){
                    int dataTag = ((IntTag) tileData.get("Data")).getValue();
                    // We should check data for each type of potItem
                    if(potItem == Material.POPPY){
                        if(dataTag >= 0 && dataTag <= 8){
                            potItemData = dataTag;
                        } else {
                            // Prevent hacks
                            potItemData = 0;
                        }
                    } else if(potItem == Material.LEGACY_YELLOW_FLOWER ||
                            potItem == Material.RED_MUSHROOM ||
                            potItem == Material.BROWN_MUSHROOM ||
                            potItem == Material.CACTUS){
                        // Set to 0 anyway
                        potItemData = 0;
                    } else if(potItem == Material.LEGACY_SAPLING){
                        if(dataTag >= 0 && dataTag <= 4){
                            potItemData = dataTag;
                        } else {
                            // Prevent hacks
                            potItemData = 0;
                        }
                    } else if(potItem == Material.TALL_GRASS){
                        // Only 0 or 2
                        if(dataTag == 0 || dataTag == 2){
                            potItemData = dataTag;
                        } else {
                            potItemData = 0;
                        }
                    } else {
                        // ERROR ?
                        potItemData = 0;
                    }
                }
                else {
                    potItemData = 0;
                }
            }
            //Bukkit.getLogger().info("Debug: flowerpot item = " + potItem.toString());
            //Bukkit.getLogger().info("Debug: flowerpot item data = " + potItemData);
            //Bukkit.getLogger().info("Debug: flowerpot materialdata = " + new MaterialData(potItem,(byte) potItemData).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
