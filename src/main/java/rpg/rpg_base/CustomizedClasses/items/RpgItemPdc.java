package rpg.rpg_base.CustomizedClasses.items;

import org.bukkit.NamespacedKey;
import rpg.rpg_base.RPG_Base;

public class RpgItemPdc {
    public static NamespacedKey customKey = new NamespacedKey(RPG_Base.getInstance(), "custom");
    public static NamespacedKey regName = new NamespacedKey(RPG_Base.getInstance(), "regName");

    public static NamespacedKey dmgKey = new NamespacedKey(RPG_Base.getInstance(), "damage");
    public static NamespacedKey atsKey = new NamespacedKey(RPG_Base.getInstance(), "attackSpeed");
    public static NamespacedKey hpKey = new NamespacedKey(RPG_Base.getInstance(), "health");
    public static NamespacedKey hpRegenKey = new NamespacedKey(RPG_Base.getInstance(), "healthRegen");
    public static NamespacedKey defKey = new NamespacedKey(RPG_Base.getInstance(), "armor");
    public static NamespacedKey speedKey = new NamespacedKey(RPG_Base.getInstance(), "speed");
    public static NamespacedKey staminaKey = new NamespacedKey(RPG_Base.getInstance(), "stamina");
    public static NamespacedKey manaKey = new NamespacedKey(RPG_Base.getInstance(), "mana");

    public static NamespacedKey strReqKey = new NamespacedKey(RPG_Base.getInstance(), "strReq");
    public static NamespacedKey agiReqKey = new NamespacedKey(RPG_Base.getInstance(), "agiReq");
    public static NamespacedKey intReqKey = new NamespacedKey(RPG_Base.getInstance(), "intReq");
    public static NamespacedKey endReqKey = new NamespacedKey(RPG_Base.getInstance(), "endReq");
    public static NamespacedKey genReqKey = new NamespacedKey(RPG_Base.getInstance(), "genReq");

    public static NamespacedKey rarityKey = new NamespacedKey(RPG_Base.getInstance(), "itemRarity");
    public static NamespacedKey classKey = new NamespacedKey(RPG_Base.getInstance(), "itemClass");

}
