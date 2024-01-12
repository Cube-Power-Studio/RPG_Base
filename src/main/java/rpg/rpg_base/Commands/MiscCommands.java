package rpg.rpg_base.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import rpg.rpg_base.RPG_Base;
public class MiscCommands implements CommandExecutor {
    private final RPG_Base plugin;

    public MiscCommands(RPG_Base plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("RPG_Base.reload")) {
            if (command.getName().equals("RPG")) {
                if (args[0].equals("reload")) {
                    try {
                        plugin.updateConfig();
                        return true;
                    } catch (Error e) {
                        sender.sendMessage("Something went wrong, check console for further details");
                        plugin.getLogger().info("While performing command /RPG reload something went wrong: " + e);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
