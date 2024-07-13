package rpg.rpg_base.CustomItemsManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import rpg.rpg_base.RPG_Base;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class ItemHandlers{
    private final RPG_Base plugin;

    public ItemHandlers(RPG_Base plugin) {
        this.plugin = plugin;
    }
    private static final HashMap<ItemStack, String> customItems = new HashMap<>();
    public static final String splitter = ",";
    private static final HashMap<String, ItemStack> customItemsByName = new HashMap<>();
    private static final HashMap<UUID, ItemStack[]> entityItemList = new HashMap<>();

    public void loadCustomItems() {
        File customItemsFolder = getCustomItemsFolder();

        // Create the directory if it doesn't exist
        if (!customItemsFolder.exists()) {
            customItemsFolder.mkdirs();
        }

        // Check if the folder is empty
        String[] files = customItemsFolder.list();
        if (files == null || files.length == 0) {
            createDefaultItemFile(customItemsFolder);
        }

        // Load items from existing files
        for (File file : Objects.requireNonNull(customItemsFolder.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                loadItemFromFile(file);
            }
        }
    }
    private void createDefaultItemFile(File customItemsFolder) {
        File defaultItemFile = new File(customItemsFolder, "default_item.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(defaultItemFile);

        // Create default ItemStack with custom values
        ItemStack defaultItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = defaultItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Custom Sword");

        // Adding custom lore
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This is a default item.");
        itemMeta.setLore(lore);

        // Set the item meta to the ItemStack
        defaultItem.setItemMeta(itemMeta);

        // Save the relevant item information to the config
        config.set("default_item.material", defaultItem.getType().toString());
        config.set("default_item.display_name", ChatColor.stripColor(itemMeta.getDisplayName()));
        config.set("default_item.damage", 10);
        config.set("default_item.health", 10);

        // Save lore to the config
        config.set("default_item.lore", lore);

        // Save the configuration
        try {
            config.save(defaultItemFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save default item configuration.");
            e.printStackTrace();
        }
    }
    public static ItemStack getCustomItemByName(String itemName) {
        return customItemsByName.getOrDefault(itemName,new ItemStack(Material.AIR));
    }
    public static Set<String> getCustomItemsName(){
        return customItemsByName.keySet();
    }


    private void loadItemFromFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Get all keys in the configuration section
        ConfigurationSection itemsSection = config.getConfigurationSection("");
        if (itemsSection != null) {
            Set<String> itemKeys = itemsSection.getKeys(false);

            for (String key : itemKeys) {
                // Load relevant item information from the file
                String materialName = config.getString(key + ".material");
                String displayName = config.getString(key + ".display_name");
                List<String> lore = config.getStringList(key + ".lore");

                // Reconstruct the ItemStack
                Material material = Material.matchMaterial(materialName);
                ItemStack loadedItem;
                if (material != null) {
                    loadedItem = new ItemStack(material);
                } else {
                    loadedItem = new ItemStack(Material.DIRT);
                }
                ItemMeta itemMeta = loadedItem.getItemMeta();
                String itemAttrib = "";

                // Set display name and lore
                if (itemMeta != null) {
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                    itemMeta.setLore(lore);
                    loadedItem.setItemMeta(itemMeta);

                    if (config.contains(key + ".damage")) {
                        String damageValue = config.getString(key + ".damage");
                        if (damageValue != null) {
                            itemAttrib += "damage" + splitter + damageValue + splitter;
                            //System.out.println("Added damage: " + damageValue);
                        }
                    }

                    if (config.contains(key + ".health")){
                        String healthValue = config.getString(key + ".health");
                        if(healthValue != null){
                            itemAttrib += "health" + splitter + healthValue + splitter;
                        }
                    }

                    if (config.contains(key + ".requirements.agility")) {
                        String agilityValue = config.getString(key + ".requirements.agility");
                        if (agilityValue != null) {
                            itemAttrib += "agility" + splitter + agilityValue + splitter;
                            //System.out.println("Added agility: " + agilityValue);
                        }
                    }

                    if (config.contains(key + ".requirements.intelligence")) {
                        String intelligenceValue = config.getString(key + ".requirements.intelligence");
                        if (intelligenceValue != null) {
                            itemAttrib += "intelligence" + splitter + intelligenceValue + splitter;
                            //System.out.println("Added intelligence: " + intelligenceValue);
                        }
                    }

                    if (config.contains(key + ".requirements.endurance")) {
                        String enduranceValue = config.getString(key + ".requirements.endurance");
                        if (enduranceValue != null) {
                            itemAttrib += "endurance" + splitter + enduranceValue + splitter;
                            //System.out.println("Added endurance: " + enduranceValue);
                        }
                    }

                    if (config.contains(key + ".requirements.strength")) {
                        String strengthValue = config.getString(key + ".requirements.strength");
                        if (strengthValue != null) {
                            itemAttrib += "strength" + splitter + strengthValue + splitter;
                            //System.out.println("Added strength: " + strengthValue);
                        }
                    }


                    //System.out.println("Final Item Attributes: " + itemAttrib);
                    customItems.put(loadedItem, itemAttrib);
                }
                customItemsByName.put(key, loadedItem);
            }
        }
    }

    private File getCustomItemsFolder() {
        File dataFolder = plugin.getDataFolder();
        return new File(dataFolder, "custom_items");
    }

    public static int getItemDamage(ItemStack itemStack) {
        if (itemStack != null) { // Null check added here
            String dmgValue = ItemUtil.getAttributeValue(itemStack, "damage");
            if (!dmgValue.isEmpty()) {
                return Integer.parseInt(dmgValue);
            } else {
                return 0;
            }
        } else {
            return 0; // Return 0 if itemStack is null
        }
    }
    public static int getItemHealth(ItemStack itemStack){
        if(itemStack != null){
            String healthValue = ItemUtil.getAttributeValue(itemStack, "health");
            if(!healthValue.isEmpty()){
                return Integer.parseInt(healthValue);
            }else{
                return 0;
            }
        }else{
            return 0;
        }
    }
    public static String getCustomItemsAttrib(ItemStack itemStack) {
        String attrib = customItems.get(itemStack);
//        if(customItems.containsKey(itemStack)){
//            System.out.println("not found");
//        }else{
//            System.out.println("test");
//            System.out.println("l1" + customItems);
//            System.out.println("l2" + customItemsByName);
//        }
        if (attrib == null) {
            // Item not found in map, log a warning message
//            System.out.println("Warning: Item attributes not found for ItemStack: " + itemStack);
        }
        return attrib;
    }

    public static ItemStack[] getEntityItem(UUID uuid){return entityItemList.get(uuid);}
    public static void setEntityItem(UUID uuid, ItemStack[] itemList){entityItemList.put(uuid, itemList);}
}
