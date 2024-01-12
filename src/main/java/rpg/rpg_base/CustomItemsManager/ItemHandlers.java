package rpg.rpg_base.CustomItemsManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rpg.rpg_base.RPG_Base;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class ItemHandlers {
    private final RPG_Base plugin;

    public ItemHandlers(RPG_Base plugin) {
        this.plugin = plugin;
    }
    private static final HashMap<ItemStack, Integer> enduranceLvlReq = new HashMap<>();
    private static final HashMap<ItemStack, Integer> strengthLvlReq = new HashMap<>();
    private static final HashMap<ItemStack, Integer> agilityLvlReq = new HashMap<>();
    private static final HashMap<ItemStack, Integer> intelligenceLvlReq = new HashMap<>();
    private static final HashMap<String, ItemStack> customItemsByName = new HashMap<>();
    private static final HashMap<ItemStack, Integer> itemDamage = new HashMap<>();

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
        return customItemsByName.getOrDefault(itemName,new ItemStack(Material.STONE));
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
                ItemStack loadedItem = null;
                if (material != null) {
                    loadedItem = new ItemStack(material);
                    ItemMeta itemMeta = loadedItem.getItemMeta();

                    // Set display name and lore
                    if (itemMeta != null) {
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                        itemMeta.setLore(lore);
                        loadedItem.setItemMeta(itemMeta);

                        setItemDamage(loadedItem, config.getInt(key + ".damage"));
                        setRequiredAgilityLvl(loadedItem, config.getInt(key + ".requirements.agility"));
                        setRequiredIntelligenceLvl(loadedItem, config.getInt(key + ".requirements.intelligence"));
                        setRequiredEnduranceLvl(loadedItem, config.getInt(key + ".requirements.endurance"));
                        setRequiredStrengthLvl(loadedItem, config.getInt(key + ".requirements.strength"));
                    }
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
        Integer damage = itemDamage.get(itemStack);
        return (damage != null) ? damage : 0;
    }
    public static void setItemDamage(ItemStack itemStack, int i){
        itemDamage.put(itemStack, i);
    }
    public int getRequiredEnduranceLvl(ItemStack itemStack){
        return enduranceLvlReq.get(itemStack);
    }
    public static void setRequiredEnduranceLvl(ItemStack itemStack, int Lvl){
        enduranceLvlReq.put(itemStack, Lvl);
    }
    public int getRequiredStrengthLvl(ItemStack itemStack){
        return strengthLvlReq.get(itemStack);
    }
    public static void setRequiredStrengthLvl(ItemStack itemStack, int Lvl){
        strengthLvlReq.put(itemStack, Lvl);
    }
    public int getRequiredAgilityLvl(ItemStack itemStack){
        return agilityLvlReq.get(itemStack);
    }
    public static void setRequiredAgilityLvl(ItemStack itemStack, int Lvl){
        agilityLvlReq.put(itemStack, Lvl);
    }
    public int getRequiredIntelligenceLvl(ItemStack itemStack){
        return intelligenceLvlReq.get(itemStack);
    }
    public static void setRequiredIntelligenceLvl(ItemStack itemStack, int Lvl){
        intelligenceLvlReq.put(itemStack, Lvl);
    }
}
