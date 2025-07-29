package rpg.rpg_base.CustomizedClasses.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomizedClasses.items.enums.ItemRarity;
import rpg.rpg_base.Data.DataBaseManager;
import rpg.rpg_base.Utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {
    private static final Map<String, RpgItem> customItemsByName = new HashMap<>();

    public static void registerItem(String regName, String rawItem){
        loadItem(regName, rawItem);
        DataBaseManager.addItemToDb(regName, rawItem);
    }

    public static void loadItems(HashMap<String, String> itemMap){
        for(Map.Entry<String, String> entry : itemMap.entrySet()){
            loadItem(entry.getKey(), entry.getValue());
        }
    }

    public static void loadItem(String regName, String rawItem){
        Map<String, String> itemParameters = new HashMap<>();
        String[] itemParamsProc = rawItem.split("///");

        for(String param : itemParamsProc){
            String[] paramSplit = param.split("---");
            itemParameters.put(paramSplit[0], paramSplit[1]);
        }

        ItemStack coreItem = new ItemStack(Material.matchMaterial(itemParameters.getOrDefault("material", "BARRIER").toUpperCase()));

        coreItem.editMeta(meta -> {
            meta.getPersistentDataContainer().set(RpgItemPdc.regName, PersistentDataType.STRING, regName);

            meta.displayName(
                    Component.text(
                            itemParameters.getOrDefault("displayName", "ERROR"),
                            ItemRarity.matchRarity(itemParameters.getOrDefault("rarity", "error")).getColor()
                            )
                            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );

            List<Component> loreComponents = new ArrayList<>();

            List<String> listOfStats = List.of("damage", "attackSpeed", "health", "healthReg", "defence", "speed", "mana", "stamina");
            if(listOfStats.stream().anyMatch(itemParameters::containsKey)){
                loreComponents.add(Component.text(""));
            }

            if(itemParameters.containsKey("damage")){
                loreComponents.add(Component.text("Dmg: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("damage")), NamedTextColor.RED))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.dmgKey, PersistentDataType.STRING, itemParameters.get("damage"));
            }

            if(itemParameters.containsKey("attackSpeed")){
                loreComponents.add(Component.text("At Speed: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("attackSpeed")), NamedTextColor.GRAY))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.dmgKey, PersistentDataType.STRING, itemParameters.get("damage"));
            }

            if(itemParameters.containsKey("health")){
                loreComponents.add(Component.text("Hp: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("health")), NamedTextColor.GREEN))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.hpKey, PersistentDataType.STRING, itemParameters.get("health"));
            }


            if(itemParameters.containsKey("healthReg")){
                loreComponents.add(Component.text("Hp Regen: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("healthReg")), NamedTextColor.DARK_GREEN))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.hpRegenKey, PersistentDataType.STRING, itemParameters.get("healthReg"));
            }


            if(itemParameters.containsKey("defence")){
                loreComponents.add(Component.text("Def: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("defence")), NamedTextColor.GRAY))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.defKey, PersistentDataType.STRING, itemParameters.get("defence"));
            }


            if(itemParameters.containsKey("speed")){
                loreComponents.add(Component.text("Spd: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("speed")), NamedTextColor.YELLOW))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.speedKey, PersistentDataType.STRING, itemParameters.get("speed"));
            }


            if(itemParameters.containsKey("mana")){
                loreComponents.add(Component.text("San: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("mana")), NamedTextColor.BLUE))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.manaKey, PersistentDataType.STRING, itemParameters.get("mana"));
            }


            if(itemParameters.containsKey("stamina")){
                loreComponents.add(Component.text("Stm: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("stamina")), NamedTextColor.GREEN))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.staminaKey, PersistentDataType.STRING, itemParameters.get("stamina"));
            }

            List<String> listOfReq = List.of("lvlGen");
            if(listOfReq.stream().anyMatch(itemParameters::containsKey)){
                loreComponents.add(Component.text(""));
                loreComponents.add(Component.text("Requirements: ").decoration(TextDecoration.ITALIC, false));
            }

            if(itemParameters.containsKey("lvlGen")){
                loreComponents.add(Component.text("Level: ")
                        .append(Component.text(Integer.parseInt(itemParameters.get("lvlGen")), NamedTextColor.GREEN))
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.getPersistentDataContainer().set(RpgItemPdc.genReqKey, PersistentDataType.STRING, itemParameters.get("lvlGen"));
            }

            for(String line : itemParameters.getOrDefault("lore", "").split("\n")){
                loreComponents.add(Util.formatYmlString(line));
            }

            loreComponents.add(Component.text(
                    itemParameters.getOrDefault("rarity", "error"),
                    ItemRarity.matchRarity(itemParameters.getOrDefault("rarity", "error")).getColor())
            );

            loreComponents.add(Component.text(
                    itemParameters.getOrDefault("type", "Tool")
                ).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            );

            meta.lore(loreComponents);

            ToolComponent toolComponent = meta.getTool();
            toolComponent.setDefaultMiningSpeed(Float.parseFloat(itemParameters.getOrDefault("miningSpeed", "0")));
            meta.setTool(toolComponent);

            meta.getPersistentDataContainer().set(RpgItemPdc.customKey, PersistentDataType.BOOLEAN, true);
        });

        RpgItem rpgItem = new RpgItem(coreItem, regName);
        rpgItem.itemParams = itemParameters;
        customItemsByName.put(regName, rpgItem);
    }

    public static String serializeItem(RpgItem item){
        List<String> itemSerial = new ArrayList<>();

        for(Map.Entry<String, String> entry : item.itemParams.entrySet()){
            itemSerial.add(entry.getKey() + "---" + entry.getValue());
        }

        return String.join("///", itemSerial);
    }

    public static Map<String, RpgItem> getItemRegistry() {
        return customItemsByName;
    }

    public static RpgItem getItemFromRegistry(String regName){
        return  customItemsByName.getOrDefault(regName, new RpgItem(new ItemStack(Material.BARRIER), "error"));
    }

    public static void updateItems(Inventory inventory){
        for(int i = 0; i < inventory.getSize(); i++){
            ItemStack item = inventory.getItem(i);
            if(Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(RpgItemPdc.customKey, PersistentDataType.BOOLEAN))){
                RpgItem rpgItem = getItemFromRegistry(item.getItemMeta().getPersistentDataContainer().get(RpgItemPdc.regName, PersistentDataType.STRING));
                ItemStack updatedItem = rpgItem.getItem();
                updatedItem.setAmount(item.getAmount());
                inventory.setItem(i, updatedItem);
            }
        }
    }
}
