package rpg.rpg_base.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.EnduranceManager;
public class EnduranceCommands implements CommandExecutor {

    private final RPG_Base plugin;
    private final EnduranceManager enduranceManager;

    public EnduranceCommands(RPG_Base plugin, EnduranceManager enduranceManager) {
        this.plugin = plugin;
        this.enduranceManager = enduranceManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("RPG_Base.SkillManagement")) {
            if (command.getName().equals("EnduranceLVLADD")) {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /EnduranceLVLADD <player> <lvl>");
                    return false;
                }
                String targetPlayerName = args[0];
                Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

                if (targetPlayer == null) {
                    sender.sendMessage("Player " + targetPlayerName + " is not online.");
                    return true;
                }

                int enduranceLevel;
                try {
                    enduranceLevel = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid endurance level. Please provide a valid integer.");
                    return false;
                }

                EnduranceManager.setEndurance_lvl(targetPlayer, EnduranceManager.getEndurance_lvl(targetPlayer) + enduranceLevel);

                EnduranceManager.EnduranceStats(targetPlayer);


                sender.sendMessage("Endurance level increased to " + enduranceLevel + " for player " + targetPlayerName + ".");
            }
            if (command.getName().equals("EnduranceLVLREM")) {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /EnduranceLVLREM <player> <lvl>");
                    return false;
                }
                String targetPlayerName = args[0];
                Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

                if (targetPlayer == null) {
                    sender.sendMessage("Player " + targetPlayerName + " is not online.");
                    return true;
                }

                int enduranceLevel;
                try {
                    enduranceLevel = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid endurance level. Please provide a valid integer.");
                    return false;
                }

                EnduranceManager.setEndurance_lvl(targetPlayer, EnduranceManager.getEndurance_lvl(targetPlayer) - enduranceLevel);

                EnduranceManager.EnduranceStats(targetPlayer);


                sender.sendMessage("Endurance level decreased to " + enduranceLevel + " for player " + targetPlayerName + ".");

            }
        }
        return true;
    }
}
