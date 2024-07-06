package rpg.rpg_base.GUIs;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import rpg.rpg_base.StatManager.HealthManager;
import rpg.rpg_base.StatManager.LevelManager;

public class ActionBar {
    public static void statisticBar(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColor.RED + "❤"
                            + HealthManager.getPlayerHealth(player) + "/"
                            + HealthManager.getPlayerMaxHealth(player)
                            + ChatColor.GRAY + " | ⛨"
                            + player.getAttribute(Attribute.GENERIC_ARMOR).getValue()
                            + ChatColor.BLUE + " | ✵"
                            + LevelManager.getXP(player) + "/"
                            + LevelManager.getXpToNextLvl(player)
                            + ChatColor.GOLD + " | ☆"
                            + LevelManager.getPlayerLevel(player)
                    ));
        }
    }
}
