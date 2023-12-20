package rpg.rpg_base.StatManager;


import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.Map;

public class EnduranceManager implements Listener {
    private static RPG_Base plugin;

    public EnduranceManager(RPG_Base plugin) {
        EnduranceManager.plugin = plugin;
    }
    public static int Endurance_Lvl_Cap;
    public static int HP_per_lvl = 2;
    public static int Armor_per_lvl = 1;
    private static final Map<Player, Integer> endurance_lvl = new HashMap<>();
    private static final Map<Player, Integer> endurance_HP = new HashMap<>();
    private static final Map<Player, Integer> endurance_Armor = new HashMap<>();
    public static void EnduranceStats(Player player){

        setEndurance_armor(player, Armor_per_lvl*getEndurance_lvl(player));
        setEndurance_hp(player, HP_per_lvl * getEndurance_lvl(player));
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getEndurance_hp(player));
        player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(getEndurance_armor(player));
    }

    public static int getEndurance_lvl(Player player){
        return endurance_lvl.getOrDefault(player, 0);
    }
    public static void setEndurance_lvl(Player player, int Endurance_Level){
        endurance_lvl.put(player, Endurance_Level);
    }
    public static int getEndurance_hp(Player player){
        return endurance_HP.getOrDefault(player, 0);
    }
    public static void setEndurance_hp(Player player, int HP){
        endurance_HP.put(player, HP);
    }
    public static int getEndurance_armor(Player player){
        return endurance_Armor.getOrDefault(player, 0);
    }
    public static void setEndurance_armor(Player player, int armor){
        endurance_Armor.put(player, armor);
    }

    public static void updateEnduranceRules(){
        Endurance_Lvl_Cap = plugin.getConfig().getConfigurationSection("Skills").getInt("Endurance.Max");
    }

}
