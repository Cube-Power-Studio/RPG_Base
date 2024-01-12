package rpg.rpg_base.hooks;

import org.bukkit.Bukkit;

public class Dependencies {
    public static boolean getBetonQuestStatus(){
        return Bukkit.getPluginManager().getPlugin("BetonQuest") != null;
    }
}
