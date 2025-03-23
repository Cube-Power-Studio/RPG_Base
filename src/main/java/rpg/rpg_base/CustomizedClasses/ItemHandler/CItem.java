package rpg.rpg_base.CustomizedClasses.ItemHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.Utils.Util;
import rpg.rpg_base.RPG_Base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CItem implements Cloneable{
    private ItemStack item;
    private final RPG_Base plugin;
    private final Util util;

    public static NamespacedKey customItemConfig = new NamespacedKey(RPG_Base.getInstance(), "customItemConfig");

    public static NamespacedKey itemDamage = new NamespacedKey(RPG_Base.getInstance(), "damage");
    public static NamespacedKey itemHealth = new NamespacedKey(RPG_Base.getInstance(), "health");
    public static NamespacedKey itemHealthRegen = new NamespacedKey(RPG_Base.getInstance(), "healthRegen");
    public static NamespacedKey itemArmor = new NamespacedKey(RPG_Base.getInstance(), "armor");

    public static NamespacedKey itemPerDamage = new NamespacedKey(RPG_Base.getInstance(), "perDamage");
    public static NamespacedKey itemPerHealth = new NamespacedKey(RPG_Base.getInstance(), "perHealth");
    public static NamespacedKey itemPerArmor = new NamespacedKey(RPG_Base.getInstance(), "perArmor");

    public static NamespacedKey itemStrReq = new NamespacedKey(RPG_Base.getInstance(), "strReq");
    public static NamespacedKey itemAgiReq = new NamespacedKey(RPG_Base.getInstance(), "agiReq");
    public static NamespacedKey itemIntReq = new NamespacedKey(RPG_Base.getInstance(), "intReq");
    public static NamespacedKey itemEndReq = new NamespacedKey(RPG_Base.getInstance(), "endReq");

    public static NamespacedKey itemRarity = new NamespacedKey(RPG_Base.getInstance(), "itemRarity");
    public static NamespacedKey itemClass = new NamespacedKey(RPG_Base.getInstance(), "itemClass");

    public static HashMap<String, CItem> customItemsByName = new HashMap<>();
    
    public CItem(RPG_Base plugin, Util util) {
        this.plugin = plugin;
        this.util = util;
        item = new ItemStack(Material.DIRT);
    }
    
    public void loadItem(ConfigurationSection config) {
        String materialName = config.getString(".material");
        String displayName = config.getString(".display_name");
        // Reconstruct the ItemStack
        Material material = null;
        if (materialName != null) {
            material = Material.matchMaterial(materialName);
        }
        ItemStack loadedItem = new ItemStack(Objects.requireNonNullElse(material, Material.DIRT));

        ItemMeta itemMeta = loadedItem.getItemMeta();

        // Set display name and lore
        if (itemMeta != null) {
            List<Component> descParts = new ArrayList<>();

            if (displayName != null) {
                itemMeta.displayName(Component.text(displayName).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            } else {
                displayName = "NULL";
                itemMeta.displayName(Component.text(displayName));
            }

            for (String loreLine : config.getStringList(".lore")) {
                Component line = util.formatYmlString(loreLine);
                descParts.add(line);
            }

            if (config.contains(".damage") || config.contains(".health") || config.contains(".armor")) {
                descParts.add(Component.text(""));

                if (config.contains(".damage")) {
                    String damageValue = config.getString(".damage");
                    if (damageValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemDamage, PersistentDataType.STRING, damageValue);
                        Component statLine = Component.text("Damage: " + damageValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statLine);
                    }
                }

                if (config.contains(".health")) {
                    String healthValue = config.getString(".health");
                    if (healthValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemHealth, PersistentDataType.STRING, healthValue);
                        Component statLine = Component.text("Health: " + healthValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statLine);
                    }
                }

                if (config.contains(".healthRegen")) {
                    String healthRegenValue = config.getString(".healthRegen");
                    if (healthRegenValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemHealthRegen, PersistentDataType.STRING, healthRegenValue);
                        Component statLine = Component.text("Health Regeneration: " + healthRegenValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statLine);
                    }
                }

                if (config.contains(".armor")) {
                    String armorValue = config.getString(".armor");
                    if (armorValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemArmor, PersistentDataType.STRING, armorValue);
                        Component statLine = Component.text("Armor: " + armorValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statLine);
                    }
                }
            }


            if (config.contains(".requirements")) {
                descParts.add(Component.text(" "));

                if (config.contains(".requirements.agility")) {
                    String agilityValue = config.getString(".requirements.agility");
                    if (agilityValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemAgiReq, PersistentDataType.STRING, agilityValue);
                        Component statReqLine = Component.text("Required agility: " + agilityValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statReqLine);
                        //System.out.println("Added agility: " + agilityValue);
                    }
                }

                if (config.contains(".requirements.intelligence")) {
                    String intelligenceValue = config.getString(".requirements.intelligence");
                    if (intelligenceValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemIntReq, PersistentDataType.STRING, intelligenceValue);
                        Component statReqLine = Component.text("Required intelligence: " + intelligenceValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statReqLine);
                        //System.out.println("Added intelligence: " + intelligenceValue);
                    }
                }

                if (config.contains(".requirements.endurance")) {
                    String enduranceValue = config.getString(".requirements.endurance");
                    if (enduranceValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemEndReq, PersistentDataType.STRING, enduranceValue);
                        Component statReqLine = Component.text("Required endurance: " + enduranceValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statReqLine);
                        //System.out.println("Added endurance: " + enduranceValue);
                    }
                }

                if (config.contains(".requirements.strength")) {
                    String strengthValue = config.getString(".requirements.strength");
                    if (strengthValue != null) {
                        itemMeta.getPersistentDataContainer().set(itemStrReq, PersistentDataType.STRING, strengthValue);
                        Component statReqLine = Component.text("Required strength: " + strengthValue)
                                .color(NamedTextColor.WHITE);
                        descParts.add(statReqLine);
                        //System.out.println("Added strength: " + strengthValue);
                    }
                }
            }

            if (config.contains(".rarity")) {
                descParts.add(Component.text(""));

                ItemRarity rarity = ItemRarity.valueOf(config.getString(".rarity").toUpperCase());

                itemMeta.getPersistentDataContainer().set(itemRarity, PersistentDataType.STRING, rarity.toString());

                // Create the base display name component
                Component displayNameComponent = Component.text(displayName);

                TextColor rarityColor = NamedTextColor.WHITE;

                // Handle special cases like the "glitch" rarity
                if (Objects.equals(rarity, ItemRarity.GLITCH)) {
                    displayNameComponent = displayNameComponent.decorate(TextDecoration.OBFUSCATED);
                } else if (rarity.getColor() == null) {
                    displayNameComponent = displayNameComponent.color(TextColor.color(Integer.parseInt("383838", 16))).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                } else {
                    rarityColor = rarity.getColor();
                    displayNameComponent = displayNameComponent.color(rarityColor).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                }

                // Set the display name to the itemMeta directly as a Component
                itemMeta.displayName(displayNameComponent);

                // Add rarity information to the lore
                Component completedRarity = Component.text("")
                        .append(Component.text("Rarity: ").color(NamedTextColor.WHITE))
                        .append(Component.text(rarity.toString().toLowerCase()).color(rarityColor));
                descParts.add(completedRarity);
            }

            if(config.contains(".class")){
                ItemClass classOfItem = ItemClass.valueOf(config.getString(".class").toUpperCase());

                itemMeta.getPersistentDataContainer().set(itemClass, PersistentDataType.STRING, classOfItem.toString());
            }


            for (Component comp : descParts) {
                descParts.set(descParts.indexOf(comp), comp.decorations().get(TextDecoration.ITALIC).equals(TextDecoration.State.NOT_SET) ? comp.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE) : comp);
            }

            itemMeta.lore(descParts);

            itemMeta.getPersistentDataContainer().set(customItemConfig, PersistentDataType.STRING, config.getName());

            loadedItem.setItemMeta(itemMeta);

            item = loadedItem;

            customItemsByName.put(config.getName(), this);
        }
    }

    public static Object getTagValue(NamespacedKey key, ItemStack item) {
        if (item == null || !item.hasItemMeta() || item.getItemMeta() == null) {
            return "0"; // Return default value if item is null or has no ItemMeta
        }
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null
                ? item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING)
                : "0";
    }


    public ItemStack getItem() {
        return item.clone();
    }

    public CItem getItemFromName(String name){
        return customItemsByName.getOrDefault(name, new CItem(plugin, util));
    }


    @Override
    public CItem clone() {
        try {
            CItem clone = (CItem) super.clone();
            clone.item = this.item != null ? this.item.clone() : null; // Deep copy ItemStack
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
