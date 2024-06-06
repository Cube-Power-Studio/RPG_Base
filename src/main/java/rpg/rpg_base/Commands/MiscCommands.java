package rpg.rpg_base.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomItemsManager.ItemHandlers;
import rpg.rpg_base.CustomMobs.MobSpawningTask;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.StatManager.LevelManager;
import rpg.rpg_base.StatManager.StrengthManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiscCommands implements CommandExecutor, TabCompleter {
    private final RPG_Base plugin;
    private final ItemHandlers itemHandlers;

    public MiscCommands(RPG_Base plugin, ItemHandlers itemHandlers) {
        this.plugin = plugin;
        this.itemHandlers = itemHandlers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender.hasPermission("RPG_Base.admin")) {
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
                if (args[0].equals("spawnMobs")){
                    new MobSpawningTask(plugin).run();
                }
                if (args[0].equals("give")){
                    if(args.length != 3) {
                        player = plugin.getServer().getPlayer(args[1]);
                        String itemName = args[2];
                        if (Material.matchMaterial(itemName) != null) {
                            sender.sendMessage("Default items are not supported!!!");
                        } else {
                            ItemStack customItem = ItemHandlers.getCustomItemByName(itemName);
                            player.getInventory().addItem(customItem);
                        }
                    }else{
                        sender.sendMessage("Command usage: /rpg give <player> <item>");
                    }
                    return false;
                }
                if (args[0].equals("levelAdd")){
                    if (args.length < 4) {
                        sender.sendMessage("Usage: /rpg levelAdd <type> <player> <lvl>");
                        return false;
                    }
                    Player target = Bukkit.getPlayer(args[2]);
                    int lvlAdded = Integer.parseInt(args[3]);
                    if (args[1].equals("endurance")){
                        EnduranceManager.addEndurance_lvl(target, lvlAdded);
                    }
                    if (args[1].equals("strength")){
                        StrengthManager.addStrength_lvl(target, lvlAdded);
                    }
                    if (args[1].equals("general")){
                        LevelManager.addPlayer_lvl(target, lvlAdded);
                        LevelManager.updateSkillPoints(target);
                    }
                }
                if (args[0].equals("levelRem")){
                    if (args.length < 4) {
                        sender.sendMessage("Usage: /rpg levelRem <type> <player> <lvl>");
                        return false;
                    }
                    Player target = Bukkit.getPlayer(args[2]);
                    int lvlRemoved= Integer.parseInt(args[3]);
                    if (args[1].equals("endurance")){
                        EnduranceManager.remEndurance_lvl(target, lvlRemoved);
                    }
                    if (args[1].equals("strength")){
                        StrengthManager.remStrength_lvl(target, lvlRemoved);
                    }
                    if (args[1].equals("general")){
                        LevelManager.remPlayer_lvl(target, lvlRemoved);
                        LevelManager.updateSkillPoints(target);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        if(command.getName().equals("RPG")){
            if(args.length == 1){
                list.add("reload");
                list.add("spawnMobs");
                list.add("give");
                Collections.sort(list);
            }
            if(args.length == 2){
                if(args[1].equals("give")){
                    list.addAll(ItemHandlers.getCustomItemsName());
                    Collections.sort(list);
                }
            }
        }
        return list;
    }
}
