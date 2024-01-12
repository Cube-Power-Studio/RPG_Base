package rpg.rpg_base.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.LevelManager;

public class GiveCommand implements CommandExecutor {
    private static RPG_Base plugin;
    private final ItemHandlers itemHandlers;
    public GiveCommand(RPG_Base plugin, ItemHandlers itemHandlers) {
        this.itemHandlers = itemHandlers;
        GiveCommand.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = plugin.getServer().getPlayer(args[0]);
        String itemName = args[1];
        ItemStack customItem = itemHandlers.getCustomItemByName(itemName);
        player.getInventory().addItem(customItem);
        return false;
    }
}
