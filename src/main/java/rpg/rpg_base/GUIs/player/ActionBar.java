package rpg.rpg_base.GUIs.player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;

public class ActionBar {
    public static void statisticBar(CPlayer player) {
        player.getPlayer().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(ChatColor.RED + "❤"
                        + player.currentHP + "/"
                        + player.maxHP
                        + ChatColor.GRAY + " | ⛨"
                        + player.armor
                        + ChatColor.BLUE + " | ✵"
                        + player.xp + "/"
                        + player.xpToNextLvl
                        + ChatColor.GOLD + " | ☆"
                        + player.level
                ));
    }
}
