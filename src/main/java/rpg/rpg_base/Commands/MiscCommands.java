package rpg.rpg_base.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.Crafting.Recipe;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.Crafting.CraftingHandler;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Shops.ShopsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiscCommands implements CommandExecutor, TabCompleter {
    private final RPG_Base plugin;
    private final ItemManager itemManager;

    public MiscCommands(RPG_Base plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender.hasPermission("RPG_Base.admin")) {
            if (command.getName().equals("RPG")) {
                if (args[0].equals("reload")) {
                    try {
                        plugin.updateConfig();
                        sender.sendMessage("Plugin reloaded successfully!");
                        return true;
                    } catch (Error e) {
                        sender.sendMessage("Something went wrong, check console for further details");
                        plugin.getLogger().info("While performing command /RPG reload something went wrong: " + e);
                        return false;
                    }
                }
                if (args[0].equals("give")){
                    if(args.length != 2) {
                        player = plugin.getServer().getPlayer(args[2]);
                        if (player != null) {

                        } else {
                            player = (Player) sender;
                        }
                        String itemName = args[1];
                        ItemStack customItem = CItem.customItemsByName.get(itemName).getItem();
                        player.getInventory().addItem(customItem);
                    }else{
                        sender.sendMessage("Command usage: /rpg give <item> <player>");
                    }
                    return false;
                }
                if (args[0].equals("levelAdd")){
                    if (args.length < 4) {
                        sender.sendMessage("Usage: /rpg levelAdd <type> <player> <lvl>");
                        return false;
                    }
                    CPlayer target = CPlayer.getPlayerByUUID(Bukkit.getPlayer(args[2]).getUniqueId());
                    int lvlAdded = Integer.parseInt(args[3]);
                    if (args[1].equals("endurance")){
                        target.playerSkills.enduranceLvl += lvlAdded;
                    }
                    if (args[1].equals("strength")){
                        target.playerSkills.strengthLvl += lvlAdded;
                    }
                    if (args[1].equals("general")){
                        target.level += lvlAdded;
                        target.updateStats();
                    }
                    sender.sendMessage("Added " + lvlAdded + " to " + target.getPlayer().getName() + "'s " + args[1]);
                }
                if (args[0].equals("levelRem")){
                    if (args.length < 4) {
                        sender.sendMessage("Usage: /rpg levelRem <type> <player> <lvl>");
                        return false;
                    }
                    CPlayer target = CPlayer.getPlayerByUUID(Bukkit.getPlayer(args[2]).getUniqueId());
                    int lvlRemoved= Integer.parseInt(args[3]);
                    if (args[1].equals("endurance")){
                        target.playerSkills.enduranceLvl -= lvlRemoved;
                    }
                    if (args[1].equals("strength")){
                        target.playerSkills.strengthLvl -= lvlRemoved;
                    }
                    if (args[1].equals("general")){
                        target.level -= lvlRemoved;
                        target.updateStats();
                    }
                    sender.sendMessage("Removed " + lvlRemoved + " from " + target.getPlayer().getName() + "'s " + args[1]);
                }
                if (args[0].equals("recipe")){
                    if (args[1].equals("reload")){
                        List<Recipe> recipesToRemove = new ArrayList<>(CraftingHandler.craftingList);
                        for (Recipe recipe : recipesToRemove) {
                            CraftingHandler.remRecipe(recipe);
                        }

                        RPG_Base.getInstance().loadRecipes();
                    }
                }
                if (args[0].equals("openShop")) {
                    if (args.length > 1 && sender instanceof Player) {
                        Player player1 = (Player) sender;
                        String shopName = args[1].replace("_", " ").trim();  // Replace underscores and trim spaces
                        if (ShopsManager.shopRegister.containsKey(shopName)) {
                            ShopsManager.openShop(player1, shopName);
                            sender.sendMessage("Opened shop: " + shopName);
                        } else {
                            sender.sendMessage("Shop not found: " + shopName);
                        }
                    } else {
                        sender.sendMessage("Usage: /rpg openShop <shop_name>");
                    }
                }
            }
        }
        if(!sender.hasPermission("tradingBanned")){
            if(command.getName().equals("Pay")){
                if(args.length == 2){
                    player = plugin.getServer().getPlayer(args[0]);
                    MoneyManager.payPlayer((Player) sender, player, Integer.parseInt(args[1]));
                }
            }
        }
        if(command.getName().equals("Bal") || command.getName().equals("Balance") || command.getName().equals("Money")){
            player = (Player) sender;
            player.sendMessage(Component.text("Gold: ")
                   .append(Component.text(MoneyManager.getPlayerGold(player), NamedTextColor.GOLD)
                   .appendNewline())
                   .append(Component.text("Runic sigils: "))
                   .append(Component.text(MoneyManager.getPlayerRunicSigils(player), NamedTextColor.LIGHT_PURPLE))
                   .appendNewline()
                   .append(Component.text("Guild medals: "))
                   .append(Component.text(MoneyManager.getPlayerGuildMedals(player), NamedTextColor.BLUE))
            );
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("RPG")) {
            if (args.length == 1) {
                if (commandSender.hasPermission("RPG_Base.admin")) {
                    list.add("reload");
                    list.add("give");
                    list.add("levelAdd");
                    list.add("levelRem");
                    list.add("recipe");
                    list.add("openShop");
                    Collections.sort(list);
                }
                return list;
            }
            if (commandSender.hasPermission("RPG_Base.admin")) {
                if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
                    if (args.length == 2) {
                        list.addAll(CItem.customItemsByName.keySet());
                        Collections.sort(list);
                    } else if (args.length == 3) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            list.add(player.getName());
                        }
                    }
                    return list;
                }
                if (args.length >= 2 && (args[0].equalsIgnoreCase("levelAdd") || args[0].equalsIgnoreCase("levelRem"))) {
                    if (args.length == 2) {
                        list.add("endurance");
                        list.add("strength");
                        list.add("general");
                    } else if (args.length == 3) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            list.add(player.getName());
                        }
                    }
                    Collections.sort(list);
                    return list;
                }
                if (args.length == 2 && args[0].equalsIgnoreCase("recipe")) {
                    list.add("reload");
                }
                if (args.length == 2 && args[0].equalsIgnoreCase("openShop")) {
                    for (String shopName : ShopsManager.shopRegister.keySet()) {
                        list.add(shopName.replace(" ", "_"));  // Add shop names with underscores for spaces
                    }
                }
            }
        }
        if(command.getName().equalsIgnoreCase("Pay")){
            if(args.length == 1){
                for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                    list.add(player.getName());
                }
            }else if(args.length == 2){
                list.add("<amount>");
            }
        }
        return list;
    }
}
