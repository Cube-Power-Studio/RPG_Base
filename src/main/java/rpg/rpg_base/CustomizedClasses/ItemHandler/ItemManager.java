package rpg.rpg_base.CustomizedClasses.ItemHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Data.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ItemManager {
    private static RPG_Base plugin;
    private static Util util;

    public ItemManager(RPG_Base plugin, Util util) {
        ItemManager.plugin = plugin;
        ItemManager.util = util;
    }

    public static void loadCustomItems() {
        File customItemsFolder = getCustomItemsFolder();

        // Create the directory if it doesn't exist
        if (!customItemsFolder.exists()) {
            customItemsFolder.mkdirs();
        }

        for (File file : Objects.requireNonNull(customItemsFolder.listFiles())){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.getKeys(false).forEach(key -> {
                ConfigurationSection section = config.getConfigurationSection(key);
                CItem item = new CItem(plugin, util);
                item.loadItem(section);
            });
        }

    }

    public static void updateItems(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.hasItemMeta()) {
                String customKey = item.getItemMeta().getPersistentDataContainer().get(CItem.customItemName, PersistentDataType.STRING);
                if (customKey != null && CItem.customItemsByName.containsKey(customKey)) {
                    CItem cItem = CItem.customItemsByName.get(customKey); // Clone the item
                    ItemStack updatedItem = cItem.getItem();
                    updatedItem.setAmount(item.getAmount());
                    inventory.setItem(i, updatedItem); // Set the updated item back into the inventory
                }
            }
        }
    }

    private static File getCustomItemsFolder() {
        File dataFolder = plugin.getDataFolder();
        return new File(dataFolder, "custom_items");
    }
}
