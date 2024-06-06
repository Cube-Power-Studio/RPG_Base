package rpg.rpg_base.data;

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import rpg.rpg_base.blocks.BannerBlock;
import rpg.rpg_base.blocks.PotBlock;
import rpg.rpg_base.blocks.SkullBlock;
import rpg.rpg_base.tags.CompoundTag;
import rpg.rpg_base.tags.ListTag;
import rpg.rpg_base.tags.StringTag;
import rpg.rpg_base.tags.Tag;

import javax.json.*;


public class IslandBlock {
    private short typeId;
    private byte data;
    private int x;
    private int y;
    private int z;
    private List<String> signText;
    private BannerBlock banner;
    private SkullBlock skull;
    private PotBlock pot;
    private EntityType spawnerBlockType;
    // Chest contents
    private final Map<Byte,ItemStack> chestContents = new HashMap<>();
    protected static final Map<String, Material> WEtoM = new HashMap<>();
    protected static final Map<String, EntityType> WEtoME = new HashMap<>();

    static {
        // Establish the World Edit to Material look up
        // V1.8 items
        WEtoM.put("BREWING_STAND",Material.BREWING_STAND);
        WEtoM.put("CARROT_ON_A_STICK",Material.CARROT_ON_A_STICK);
        WEtoM.put("CARROT",Material.CARROT);
        WEtoM.put("CAULDRON", Material.CAULDRON);
        WEtoM.put("CHEST_MINECART", Material.CHEST_MINECART);
        WEtoM.put("CLOCK", Material.CLOCK);
        WEtoM.put("COBBLESTONE_WALL",Material.COBBLESTONE_WALL);
        WEtoM.put("COMMAND_BLOCK",Material.COMMAND_BLOCK);
        WEtoM.put("COMMANDBLOCK_MINECART",Material.COMMAND_BLOCK_MINECART);
        WEtoM.put("COMPARATOR",Material.COMPARATOR);
        WEtoM.put("COOKED_PORKCHOP", Material.COOKED_PORKCHOP);
        WEtoM.put("CLOCK", Material.CLOCK);
        WEtoM.put("CRAFTING_TABLE", Material.CRAFTING_TABLE);
        WEtoM.put("DIAMOND_HORSE_ARMOR",Material.DIAMOND_HORSE_ARMOR);
        WEtoM.put("DIAMOND_SHOVEL",Material.DIAMOND_SHOVEL);
        WEtoM.put("DYE",Material.INK_SAC);
        WEtoM.put("ENCHANTING_TABLE", Material.ENCHANTING_TABLE);
        WEtoM.put("END_PORTAL_FRAME",Material.END_PORTAL_FRAME);
        WEtoM.put("END_PORTAL", Material.END_PORTAL);
        WEtoM.put("END_STONE", Material.END_STONE);
        WEtoM.put("EXPERIENCE_BOTTLE",Material.EXPERIENCE_BOTTLE);
        WEtoM.put("FILLED_MAP",Material.MAP);
        WEtoM.put("FIRE_CHARGE",Material.FIRE_CHARGE);
        WEtoM.put("FIREWORKS",Material.FIREWORK_ROCKET);
        WEtoM.put("FLOWER_POT", Material.FLOWER_POT);
        WEtoM.put("GLASS_PANE",Material.LEGACY_THIN_GLASS);
        WEtoM.put("GOLDEN_CHESTPLATE",Material.GOLDEN_CHESTPLATE);
        WEtoM.put("GOLDEN_HORSE_ARMOR",Material.GOLDEN_HORSE_ARMOR);
        WEtoM.put("GOLDEN_LEGGINGS",Material.GOLDEN_LEGGINGS);
        WEtoM.put("GOLDEN_PICKAXE",Material.GOLDEN_PICKAXE);
        WEtoM.put("GOLDEN_RAIL",Material.POWERED_RAIL);
        WEtoM.put("GOLDEN_SHOVEL",Material.GOLDEN_SHOVEL);
        WEtoM.put("GOLDEN_SWORD", Material.GOLDEN_SWORD);
        WEtoM.put("GOLDEN_HELMET", Material.GOLDEN_HELMET);
        WEtoM.put("GOLDEN_HOE", Material.GOLDEN_HOE);
        WEtoM.put("GOLDEN_AXE", Material.GOLDEN_AXE);
        WEtoM.put("GOLDEN_BOOTS", Material.GOLDEN_BOOTS);
        WEtoM.put("GUNPOWDER", Material.GUNPOWDER);
        WEtoM.put("HARDENED_CLAY",Material.TERRACOTTA);
        WEtoM.put("HEAVY_WEIGHTED_PRESSURE_PLATE",Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        WEtoM.put("IRON_BARS",Material.IRON_BARS);
        WEtoM.put("IRON_HORSE_ARMOR",Material.IRON_HORSE_ARMOR);
        WEtoM.put("IRON_SHOVEL",Material.IRON_SHOVEL);
        WEtoM.put("LEAD",Material.LEAD);
        WEtoM.put("LEAVES2",Material.OAK_LEAVES);
        WEtoM.put("LIGHT_WEIGHTED_PRESSURE_PLATE",Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        WEtoM.put("MAP",Material.MAP);
        WEtoM.put("MYCELIUM", Material.MYCELIUM);
        WEtoM.put("NETHER_BRICK_FENCE",Material.LEGACY_NETHER_FENCE);
        WEtoM.put("NETHER_WART",Material.NETHER_WART);
        WEtoM.put("NETHERBRICK",Material.NETHER_BRICK);
        WEtoM.put("OAK_STAIRS",Material.LEGACY_WOOD_STAIRS);
        WEtoM.put("PISTON",Material.PISTON);
        WEtoM.put("PLANKS",Material.LEGACY_WOOD);
        WEtoM.put("POTATO", Material.POTATO);
        WEtoM.put("RAIL",Material.RAIL);
        WEtoM.put("RECORD_11",Material.MUSIC_DISC_11);
        WEtoM.put("RECORD_13",Material.MUSIC_DISC_13);
        WEtoM.put("RECORD_BLOCKS",Material.MUSIC_DISC_BLOCKS);
        WEtoM.put("RECORD_CAT",Material.MUSIC_DISC_CAT);
        WEtoM.put("RECORD_CHIRP",Material.MUSIC_DISC_CHIRP);
        WEtoM.put("RECORD_FAR",Material.MUSIC_DISC_FAR);
        WEtoM.put("RECORD_MALL",Material.MUSIC_DISC_MALL);
        WEtoM.put("RECORD_MELLOHI",Material.MUSIC_DISC_MELLOHI);
        WEtoM.put("RECORD_STAL",Material.MUSIC_DISC_STAL);
        WEtoM.put("RECORD_STRAD",Material.MUSIC_DISC_STRAD);
        WEtoM.put("RECORD_WAIT",Material.MUSIC_DISC_WAIT);
        WEtoM.put("RECORD_WARD",Material.MUSIC_DISC_WARD);
        WEtoM.put("REEDS",Material.SUGAR_CANE);
        WEtoM.put("REPEATER",Material.REPEATER);
        WEtoM.put("SKULL", Material.LEGACY_SKULL_ITEM);
        WEtoM.put("SPAWN_EGG",Material.LEGACY_MONSTER_EGG);
        WEtoM.put("STICKY_PISTON",Material.STICKY_PISTON);
        WEtoM.put("STONE_BRICK_STAIRS",Material.BRICK_STAIRS);
        WEtoM.put("STONE_BRICK_STAIRS",Material.LEGACY_SMOOTH_STAIRS);
        WEtoM.put("STONE_SHOVEL",Material.STONE_SHOVEL);
        WEtoM.put("STONE_SLAB",Material.LEGACY_STEP);
        WEtoM.put("STONE_STAIRS",Material.COBBLESTONE_STAIRS);
        WEtoM.put("TNT_MINECART",Material.TNT_MINECART);
        WEtoM.put("WATERLILY",Material.LILY_PAD);
        WEtoM.put("WHEAT_SEEDS", Material.WHEAT_SEEDS);
        WEtoM.put("WOODEN_AXE",Material.WOODEN_AXE);
        WEtoM.put("WOODEN_BUTTON",Material.LEGACY_WOOD_BUTTON);
        WEtoM.put("WOODEN_DOOR",Material.LEGACY_WOOD_DOOR);
        WEtoM.put("WOODEN_HOE",Material.WOODEN_HOE);
        WEtoM.put("WOODEN_PICKAXE",Material.WOODEN_PICKAXE);
        WEtoM.put("WOODEN_PRESSURE_PLATE",Material.LEGACY_WOOD_PLATE);
        WEtoM.put("WOODEN_SHOVEL",Material.WOODEN_SHOVEL);
        WEtoM.put("WOODEN_SLAB",Material.LEGACY_WOOD_STEP);
        WEtoM.put("WOODEN_SWORD",Material.WOODEN_SWORD);
        WEtoM.put("MUSHROOM_STEW",Material.MUSHROOM_STEW);
        // Entities
        WEtoME.put("LAVASLIME", EntityType.MAGMA_CUBE);
        WEtoME.put("ENTITYHORSE", EntityType.HORSE);
        WEtoME.put("OZELOT", EntityType.OCELOT);
        WEtoME.put("MUSHROOMCOW", EntityType.MUSHROOM_COW);
        WEtoME.put("MOOSHROOM", EntityType.MUSHROOM_COW); // 1.11 rename
        WEtoME.put("PIGZOMBIE", EntityType.PIGLIN);
        WEtoME.put("ZOMBIE_PIGMAN", EntityType.ZOMBIFIED_PIGLIN); // 1.11 rename
        WEtoME.put("CAVESPIDER", EntityType.CAVE_SPIDER);
        WEtoME.put("XPORB", EntityType.EXPERIENCE_ORB);
        WEtoME.put("XP_ORB", EntityType.EXPERIENCE_ORB); // 1.11 rename
        WEtoME.put("MINECARTRIDEABLE", EntityType.MINECART);
        WEtoME.put("MINECARTHOPPER", EntityType.MINECART_HOPPER);
        WEtoME.put("HOPPER_MINECART", EntityType.MINECART_HOPPER);
        WEtoME.put("MINECARTFURNACE", EntityType.MINECART_FURNACE);
        WEtoME.put("FURNACE_MINECART", EntityType.MINECART_FURNACE);
        WEtoME.put("MINECARTMOBSPAWNER", EntityType.MINECART_MOB_SPAWNER);
        WEtoME.put("SPAWNER_MINECART", EntityType.MINECART_MOB_SPAWNER); // 1.11 rename
        WEtoME.put("MINECARTTNT", EntityType.MINECART_TNT);
        WEtoME.put("TNT_MINECART", EntityType.MINECART_TNT); // 1.11
        WEtoME.put("LEASH_KNOT",EntityType.LEASH_HITCH); // 1.11
        WEtoME.put("MINECARTCHEST", EntityType.MINECART_CHEST);
        WEtoME.put("CHEST_MINECART", EntityType.MINECART_CHEST); //1.11 rename
        WEtoME.put("VILLAGERGOLEM", EntityType.IRON_GOLEM);
        WEtoME.put("ENDERDRAGON", EntityType.ENDER_DRAGON);
        WEtoME.put("PAINTING", EntityType.PAINTING);
        WEtoME.put("ITEMFRAME", EntityType.ITEM_FRAME);
        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7")) {
            WEtoME.put("ENDERCRYSTAL", EntityType.ENDER_CRYSTAL);
            WEtoME.put("ARMORSTAND", EntityType.ARMOR_STAND);
        }
        // 1.10 entities and materials
        if (!Bukkit.getServer().getVersion().contains("(MC: 1.7") && !Bukkit.getServer().getVersion().contains("(MC: 1.8") && !Bukkit.getServer().getVersion().contains("(MC: 1.9")) {
            WEtoME.put("POLARBEAR", EntityType.POLAR_BEAR);
            WEtoM.put("ENDER_CRYSTAL", Material.END_CRYSTAL); // 1.11
        }
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public IslandBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        signText = null;
        banner = null;
        skull = null;
        pot = null;
        spawnerBlockType = null;
    }
    /**
     * @return the type
     */
    public int getTypeId() {
        return typeId;
    }
    /**
     * @param type the type to set
     */
    public void setTypeId(short type) {
        this.typeId = type;
    }
    /**
     * @return the data
     */
    public int getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(byte data) {
        this.data = data;
    }

    /**
     * @return the signText
     */
    public List<String> getSignText() {
        return signText;
    }
    /**
     * @param signText the signText to set
     */
    public void setSignText(List<String> signText) {
        this.signText = signText;
    }

    /**
     * @param s
     * @param b
     */
    public void setBlock(int s, byte b) {
        this.typeId = (short)s;
        this.data = b;
    }

    /**
     * Sets this block up with all the banner data required
     * @param map
     */
    public void setBanner(Map<String, Tag> map) {
        banner = new BannerBlock();
        banner.prep(map);
    }
    /**
     * Sets this block up with all the skull data required
     * @param map
     * @param dataValue
     */
    public void setSkull(Map<String, Tag> map, int dataValue) {
        skull = new SkullBlock();
        skull.prep(map, dataValue);
    }
    public void setFlowerPot(Map<String, Tag> map){
        pot = new PotBlock();
        pot.prep(map);
    }

    /**
     * Sets the spawner type if this block is a spawner
     * @param tileData
     */
    public void setSpawnerType(Map<String, Tag> tileData) {
        //Bukkit.getLogger().info("DEBUG: " + tileData.toString());
        String creatureType = "";
        if (tileData.containsKey("EntityId")) {
            creatureType = ((StringTag) tileData.get("EntityId")).getValue().toUpperCase();
        } else if (tileData.containsKey("SpawnData")) {
            // 1.9 format
            Map<String,Tag> spawnData = ((CompoundTag) tileData.get("SpawnData")).getValue();
            //Bukkit.getLogger().info("DEBUG: " + spawnData.toString());
            if (spawnData.containsKey("id")) {
                creatureType = ((StringTag) spawnData.get("id")).getValue().toUpperCase();
            }
        }
        //Bukkit.getLogger().info("DEBUG: creature type = " + creatureType);
        // The mob type might be prefixed with "Minecraft:"
        if (creatureType.startsWith("MINECRAFT:")) {
            creatureType = creatureType.substring(10);
        }
        if (WEtoME.containsKey(creatureType)) {
            spawnerBlockType = WEtoME.get(creatureType);
        } else {
            try {
                spawnerBlockType = EntityType.valueOf(creatureType);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Spawner setting of " + creatureType + " is unknown for this server. Skipping.");
            }
        }
        //Bukkit.getLogger().info("DEBUG: spawnerblock type = " + spawnerBlockType);
    }

    /**
     * Sets this block's sign data
     * @param tileData
     */
    public void setSign(Map<String, Tag> tileData) {
        signText = new ArrayList<String>();
        List<String> text = new ArrayList<String>();
        for (int i = 1; i < 5; i++) {
            String line = ((StringTag) tileData.get("Text" + String.valueOf(i))).getValue();
            // This value can actually be a string that says null sometimes.
            if (line.equalsIgnoreCase("null")) {
                line = "";
            }
            //System.out.println("DEBUG: line " + i + " = '"+ line + "' of length " + line.length());
            text.add(line);
        }

        // This just removes all the JSON formatting and provides the raw text
        for (int line = 0; line < 4; line++) {
            String lineText = "";
            if (!text.get(line).equals("\"\"") && !text.get(line).isEmpty()) {
                if (text.get(line).startsWith("{")) {
                    try {
                        JsonObject json = parseJsonObject(text.get(line));
                        JsonArray list = json.getJsonArray("extra");
                        if (list != null) {
                            Iterator<JsonValue> iter = list.iterator();
                            while(iter.hasNext()){
                                JsonValue next = iter.next();
                                String format = next.toString();
                                if (format.startsWith("{")) {
                                    JsonObject jsonFormat = parseJsonObject(format);
                                    Iterator<Map.Entry<String, JsonValue>> formatIter = jsonFormat.entrySet().iterator();
                                    while (formatIter.hasNext()) {
                                        Map.Entry<String, JsonValue> entry = formatIter.next();
                                        String key = entry.getKey();
                                        String value = entry.getValue().toString();
                                        if (key.equalsIgnoreCase("color")) {
                                            try {
                                                lineText += ChatColor.valueOf(value.toUpperCase());
                                            } catch (IllegalArgumentException e) {
                                                System.out.println("Unknown color " + value +" in sign when pasting schematic, skipping...");
                                            }
                                        } else if (key.equalsIgnoreCase("text")) {
                                            lineText += value;
                                        } else {
                                            // Handle other formatting options
                                        }
                                    }
                                } else {
                                    if (format.length() > 1) {
                                        lineText += ChatColor.RESET + format.substring(format.indexOf('"') + 1, format.lastIndexOf('"'));
                                    }
                                }
                            }
                        } else {
                            String value = json.getString("text");
                            lineText += value;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (text.get(line).length() > 1) {
                        try {
                            lineText = text.get(line).substring(text.get(line).indexOf('"')+1,text.get(line).lastIndexOf('"'));
                        } catch (Exception e) {
                            lineText = text.get(line);
                        }
                    } else {
                        lineText = text.get(line);
                    }
                }
            }
        }
    }

    public void setBook(Map<String, Tag> tileData) {
        //Bukkit.getLogger().info("DEBUG: Book data ");
        Bukkit.getLogger().info(tileData.toString());
    }

    @SuppressWarnings("deprecation")
    public void setChest(NMSAbstraction nms, Map<String, Tag> tileData) {
        try {
            ListTag chestItems = (ListTag) tileData.get("Items");
            if (chestItems != null) {
                //int number = 0;
                for (Tag item : chestItems.getValue()) {
                    // Format for chest items is:
                    // id = short value of item id
                    // Damage = short value of item damage
                    // Count = the number of items
                    // Slot = the slot in the chest
                    // inventory

                    if (item instanceof CompoundTag) {
                        try {
                            // Id is a number
                            short itemType = (Short) ((CompoundTag) item).getValue().get("id").getValue();
                            short itemDamage = (Short) ((CompoundTag) item).getValue().get("Damage").getValue();
                            byte itemAmount = (Byte) ((CompoundTag) item).getValue().get("Count").getValue();
                            byte itemSlot = (Byte) ((CompoundTag) item).getValue().get("Slot").getValue();
                            ItemStack chestItem = new ItemStack(Material.getMaterial(String.valueOf(itemType)), itemAmount, itemDamage);
                            chestContents.put(itemSlot, chestItem);
                        } catch (ClassCastException ex) {
                            // Id is a material
                            String itemType = (String) ((CompoundTag) item).getValue().get("id").getValue();
                            try {
                                // Get the material
                                if (itemType.startsWith("minecraft:")) {
                                    String material = itemType.substring(10).toUpperCase();
                                    // Special case for non-standard material names
                                    Material itemMaterial;

                                    //Bukkit.getLogger().info("DEBUG: " + material);

                                    if (WEtoM.containsKey(material)) {
                                        //Bukkit.getLogger().info("DEBUG: Found in hashmap");
                                        itemMaterial = WEtoM.get(material);
                                    } else {
                                        //Bukkit.getLogger().info("DEBUG: Not in hashmap");
                                        itemMaterial = Material.valueOf(material);
                                    }
                                    short itemDamage = (Short) ((CompoundTag) item).getValue().get("Damage").getValue();
                                    byte itemAmount = (Byte) ((CompoundTag) item).getValue().get("Count").getValue();
                                    byte itemSlot = (Byte) ((CompoundTag) item).getValue().get("Slot").getValue();
                                    ItemStack chestItem = new ItemStack(itemMaterial, itemAmount, itemDamage);
                                    if (itemMaterial.equals(Material.WRITTEN_BOOK)) {
                                        chestItem = nms.setBook((org.bukkit.Tag) item);
                                    }
                                    // Check for potions
                                    if (itemMaterial.toString().contains("POTION")) {
                                        chestItem = nms.setPotion(itemMaterial, (org.bukkit.Tag) item, chestItem);
                                    }
                                    chestContents.put(itemSlot, chestItem);
                                }
                            } catch (Exception exx) {
                                // Bukkit.getLogger().info(item.toString());
                                // Bukkit.getLogger().info(((CompoundTag)item).getValue().get("id").getName());
                                Bukkit.getLogger().severe(
                                        "Could not parse item [" + itemType.substring(10).toUpperCase() + "] in schematic - skipping!");
                                // Bukkit.getLogger().severe(item.toString());
                                exx.printStackTrace();
                            }

                        }

                        // Bukkit.getLogger().info("Set chest inventory slot "
                        // + itemSlot + " to " +
                        // chestItem.toString());
                    }
                }
                //Bukkit.getLogger().info("Added " + number + " items to chest");
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not parse schematic file item, skipping!");
            // e.printStackTrace();
        }
    }
    private static JsonObject parseJsonObject(String jsonStr) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonStr));
        return jsonReader.readObject();
    }


    /**
     * Paste this block at blockLoc
     * @param nms
     * @param blockLoc
     */
    //@SuppressWarnings("deprecation")
    @SuppressWarnings("deprecation")
    public void paste(NMSAbstraction nms, Location blockLoc, boolean usePhysics, Biome biome) {
        // Only paste air if it is below the sea level and in the overworld
        Block block = new Location(blockLoc.getWorld(), x, y, z).add(blockLoc).getBlock();
        block.setBiome(biome);
        nms.setBlockSuperFast(block, typeId, data, usePhysics);
        if (signText != null) {
            // Sign
            Sign sign = (Sign) block.getState();
            int index = 0;
            for (String line : signText) {
                sign.setLine(index++, line);
            }
            sign.update(true, false);
        } else if (banner != null) {
            banner.set(block);
        } else if (skull != null){
            skull.set(block);
        } else if (pot != null){
            pot.set(nms, block);
        } else if (spawnerBlockType != null) {
            CreatureSpawner cs = (CreatureSpawner)block.getState();
            cs.setSpawnedType(spawnerBlockType);
            //Bukkit.getLogger().info("DEBUG: setting spawner");
            cs.update(true, false);
        } else if (!chestContents.isEmpty()) {
            //Bukkit.getLogger().info("DEBUG: inventory holder "+ block.getType());
            // Check if this is a double chest

            InventoryHolder chestBlock = (InventoryHolder) block.getState();
            //InventoryHolder iH = chestBlock.getInventory().getHolder();
            if (chestBlock instanceof DoubleChest) {
                //Bukkit.getLogger().info("DEBUG: double chest");
                DoubleChest doubleChest = (DoubleChest) chestBlock;
                for (ItemStack chestItem: chestContents.values()) {
                    doubleChest.getInventory().addItem(chestItem);
                }
            } else {
                // Single chest
                for (Map.Entry<Byte, ItemStack> en : chestContents.entrySet()) {
                    //Bukkit.getLogger().info("DEBUG: " + en.getKey() + ","  + en.getValue());
                    chestBlock.getInventory().setItem(en.getKey(), en.getValue());
                }
            }
        }
    }

    /**
     * @return Vector for where this block is in the schematic
     */
    public Vector getVector() {
        return new Vector(x,y,z);
    }
}