package rpg.rpg_base.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.SkillPointHandler;

public class LevelCommands implements CommandExecutor {
    private final RPG_Base plugin;
    private final SkillPointHandler skillPointHandler;

    public LevelCommands(RPG_Base plugin, SkillPointHandler skillPointHandler) {
        this.plugin = plugin;
        this.skillPointHandler = skillPointHandler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("LevelAdd")) {
            if (args.length < 2) {
                sender.sendMessage("Usage: /LevelAdd <player> <lvl>");
                return false;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

            if (targetPlayer == null) {
                targetPlayer = (Player) sender;
                int Level;
                try {
                    Level = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid level. Please provide a valid integer.");
                    return false;
                }

                SkillPointHandler.level += Level;

                SkillPointHandler.UpdateSkillPoints((Player) sender);

                sender.sendMessage("Level increased by " + Level + " for player " + sender + ".");

                return true;
            }

            int Level;
            try {
                Level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid level. Please provide a valid integer.");
                return false;
            }

            SkillPointHandler.level += Level;

            SkillPointHandler.UpdateSkillPoints((Player) sender);

            sender.sendMessage("Level increased by " + Level + " for player " + targetPlayerName + ".");
        }
        return true;
    }
}
