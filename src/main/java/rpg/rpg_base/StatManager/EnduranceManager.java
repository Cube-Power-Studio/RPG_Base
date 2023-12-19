package rpg.rpg_base.StatManager;


import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import rpg.rpg_base.RPG_Base;

public class EnduranceManager implements Listener {
    private static RPG_Base plugin;

    public EnduranceManager(RPG_Base plugin) {
        EnduranceManager.plugin = plugin;
    }
    public static int Endurance_Lvl;
    public static int Endurance_Lvl_Cap;
    public static int Endurance_HP;
    public static int Endurance_ARMOR;
    public static int HP_per_lvl = 2;
    public static int Armor_per_lvl = 1;
    public static void EnduranceStats(Player player){

        Endurance_Lvl_Cap = plugin.getConfig().getConfigurationSection("Skills").getInt("Endurance.Max");
        Endurance_ARMOR= Endurance_Lvl*Armor_per_lvl;
        player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(Endurance_ARMOR);
        Endurance_HP = HP_per_lvl*Endurance_Lvl;
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + Endurance_HP);
    }

    @EventHandler
    public void UpdateEnduranceStats(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        EnduranceStats(player);
    }
}
