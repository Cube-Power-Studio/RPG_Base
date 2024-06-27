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
    public static int HP_per_lvl = 15;
    public static int Armor_per_lvl = 1;
    public static int HP_regen_per_lvl = 5;
    private static final Map<Player, Integer> endurance_lvl = new HashMap<>();
    private static final Map<Player, Integer> endurance_HP = new HashMap<>();
    private static final Map<Player, Integer> endurance_Armor = new HashMap<>();
    private static final Map<Player, Integer> endurance_HP_Regen = new HashMap<>();

    public static void enduranceStats(Player player){
        setEndurance_armor(player , Armor_per_lvl*getEndurance_lvl(player ));
        setEndurance_hp(player , HP_per_lvl * getEndurance_lvl(player ));
        setEndurance_HP_Regen(player , HP_regen_per_lvl*getEndurance_lvl(player ));
        player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(getEndurance_armor(player ));
    }

    public static int getEndurance_lvl(Player Player){
        return endurance_lvl.getOrDefault(Player, 0);
    }
    public static void setEndurance_lvl(Player Player, int Endurance_Level){
        endurance_lvl.put(Player, Endurance_Level);
    }
    public static void addEndurance_lvl(Player Player, int lvlsAdded){
        endurance_lvl.put(Player, getEndurance_lvl(Player) + lvlsAdded);
    }
    public static void remEndurance_lvl(Player player, int lvlsRemoved){
        endurance_lvl.put(player, getEndurance_lvl(player) - lvlsRemoved);
    }
    public static int getEndurance_hp(Player Player){
        return endurance_HP.getOrDefault(Player, 0);
    }
    public static void setEndurance_hp(Player Player, int HP){
        endurance_HP.put(Player, HP);
    }
    public static int getEndurance_armor(Player Player){
        return endurance_Armor.getOrDefault(Player, 0);
    }
    public static void setEndurance_armor(Player Player, int armor){
        endurance_Armor.put(Player, armor);
    }
    public static int getEndurance_HP_Regen(Player Player){
        return endurance_HP_Regen.getOrDefault(Player, 0);
    }
    public static void setEndurance_HP_Regen(Player Player, int i){
        endurance_HP_Regen.put(Player, i);
    }

    public static void updateEnduranceRules(){
        Endurance_Lvl_Cap = plugin.getConfig().getConfigurationSection("Skills").getInt("Endurance.Max");
    }

}
