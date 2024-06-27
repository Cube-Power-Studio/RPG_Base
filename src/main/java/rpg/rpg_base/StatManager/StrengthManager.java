package rpg.rpg_base.StatManager;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.Map;

public class StrengthManager {
    private static RPG_Base plugin;

    public StrengthManager(RPG_Base plugin) {
        StrengthManager.plugin = plugin;
    }
    public static int Strength_Lvl_Cap;
    public static int DMG_per_lvl = 5;
    public static int knockback_resistance_per_lvl = 1;
    private static final Map<Player, Integer> strength_lvl = new HashMap<>();
    private static final Map<Player, Integer> strength_dmg = new HashMap<>();
    private static final Map<Player, Integer> strength_knockback_resistance = new HashMap<>();
    public static void strengthStats(Player player){

        setStrength_knockback_resistance(player, knockback_resistance_per_lvl*getStrength_lvl(player));
        setStrength_dmg(player, DMG_per_lvl * getStrength_lvl(player));
        player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(getStrength_knockback_resistance(player));
    }

    public static int getStrength_lvl(Player player){
        return strength_lvl.getOrDefault(player, 0);
    }
    public static void setStrength_lvl(Player player, int strength_Level){
        strength_lvl.put(player, strength_Level);
    }
    public static void addStrength_lvl(Player player, int lvlsAdded){
        strength_lvl.put(player, getStrength_lvl(player) + lvlsAdded);
    }
    public static void remStrength_lvl(Player player, int lvlsRemoved){
        strength_lvl.put(player, getStrength_lvl(player) - lvlsRemoved);
    }
    public static int getStrength_dmg(Player player){
        return strength_dmg.getOrDefault(player, 0);
    }
    public static void setStrength_dmg(Player player, int dmg){
        strength_dmg.put(player, dmg);
    }
    public static int getStrength_knockback_resistance(Player player){
        return strength_knockback_resistance.getOrDefault(player, 0);
    }
    public static void setStrength_knockback_resistance(Player player, int knockback_resistance){
        strength_knockback_resistance.put(player, knockback_resistance);
    }

    public static void updateStrengthRules(){
        Strength_Lvl_Cap = plugin.getConfig().getConfigurationSection("Skills").getInt("Strength.Max");
    }
}
