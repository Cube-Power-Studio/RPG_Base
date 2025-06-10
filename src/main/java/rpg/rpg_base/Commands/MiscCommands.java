package rpg.rpg_base.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.Crafting.CraftingHandler;
import rpg.rpg_base.Crafting.Recipe;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.SkillRegistry;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Shops.ShopsManager;
import rpg.rpg_base.Utils.PathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Deprecated(since = "Latest dev version", forRemoval = true)
public class MiscCommands implements CommandExecutor, TabCompleter {
    private final RPG_Base plugin;

    public MiscCommands(RPG_Base plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender.hasPermission("RPG_Base.admin")) {
            if (command.getName().equals("RPG")) {
                if (args[0].equalsIgnoreCase("reload")) {

                }
                if (args[0].equalsIgnoreCase("give")){

                }
                if (args[0].equalsIgnoreCase("compareItems")){
                    if(args[1] == null || args[2] == null){
                        sender.sendMessage("YOU NEED TO PROVIDE 2 SLOT NUMBERS [0-8]");
                        return false;
                    }

                    if(!Character.isDigit(args[1].toCharArray()[0]) || !Character.isDigit(args[2].toCharArray()[0])){
                        sender.sendMessage("Numbers you provided dont match!");
                        return false;
                    }

                    int slot1 = Integer.parseInt(args[1]);
                    int slot2 = Integer.parseInt(args[2]);

                    if(0 > slot1 || slot1 > 8 || 0 > slot2 || slot2 > 8){
                        sender.sendMessage("Slots you provided are'nt toolbar slots!!!");
                        return false;
                    }

                    if(sender instanceof Player player1){
                        ItemStack item1 = player1.getInventory().getItem(slot1);
                        ItemStack item2 = player1.getInventory().getItem(slot2);

                        if(item1.getType().equals(Material.AIR) || item2.getType().equals(Material.AIR)){
                            sender.sendMessage("One of the items is AIR");
                            return false;
                        }

                        if(item1.isSimilar(item2)){
                            sender.sendMessage("Items are similar");
                        }else{
                            sender.sendMessage("Items don't match");
                            RPG_Base.getInstance().getLogger().info("ITEM 1: " + item1 + " |||||||||| ITEM 2: " + item2);

                            RPG_Base.getInstance().getLogger().info("ITEM 1 NBT KEYS: ");
                            for(NamespacedKey key : item1.getItemMeta().getPersistentDataContainer().getKeys()){
                                RPG_Base.getInstance().getLogger().info(key.getKey() + ":" + key.getNamespace());
                            }
                            RPG_Base.getInstance().getLogger().info("ITEM 2 NBT KEYS: ");
                            for(NamespacedKey key : item2.getItemMeta().getPersistentDataContainer().getKeys()){
                                RPG_Base.getInstance().getLogger().info(key.getKey() + ":" + key.getNamespace());
                            }
                        }

                        return true;
                    }

                } // @Deprecated
                if (args[0].equalsIgnoreCase("levelAdd")){
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
                } // added
                if (args[0].equalsIgnoreCase("levelRem")){
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
                } // added
                if (args[0].equalsIgnoreCase("recipe")){
                    if (args[1].equals("reload")){
                        List<Recipe> recipesToRemove = new ArrayList<>(CraftingHandler.craftingList);
                        for (Recipe recipe : recipesToRemove) {
                            CraftingHandler.remRecipe(recipe);
                        }

                        RPG_Base.getInstance().loadRecipes();
                    }
                }
                if (args[0].equalsIgnoreCase("openShop")) {
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
                } //added
                if (args[0].equalsIgnoreCase("checkPathTo")){
                    if(sender instanceof Player player1){
                        List<Integer> locationCords = new ArrayList<>();
                        locationCords.add(args.length > 1 ? Integer.parseInt(args[1]) : 0);
                        locationCords.add(args.length > 2 ? Integer.parseInt(args[2]) : 0);
                        locationCords.add(args.length > 3 ? Integer.parseInt(args[3]) : 0);

                        int radius = args.length > 4 ? Integer.parseInt(args[4]) : 0;

                        if( radius > 50){
                            radius = 50;
                            player1.sendMessage("Radius can't be bigger than 50!!! Clamping it down...");
                        }

                        Location targetLocation = new Location(player1.getWorld(), locationCords.get(0), locationCords.get(1), locationCords.get(2));

                        PathFinder pathFinder = new PathFinder(player1.getLocation().getBlock().getLocation(), targetLocation, 10000, false, 5);
                        Location[] path = pathFinder.findPath();

                        if(radius == 0){
                            if(path.length > 0){
                                player1.sendMessage("Path exists! Drawing the path...");
                                Random random = new Random();
                                Color color = Color.fromRGB(random.nextInt(0, 255), random.nextInt(0,255), random.nextInt(0, 255));
                                for(Location location : path){
                                    new BukkitRunnable() {
                                        int elapsedTicks = 0;
                                        final Location particleLocation = location.clone(); // Clone the location to prevent overwriting
                                        @Override
                                        public void run() {
                                            // Stop the task after 20 seconds
                                            if (elapsedTicks >= 20 * 20) {
                                                this.cancel();
                                                return;
                                            }

                                            Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1.0F);
                                            particleLocation.getWorld().spawnParticle(Particle.DUST, particleLocation.clone().add(0.5, 1, 0.5), 50, dustOptions);

                                            elapsedTicks += 5; // Update the elapsed ticks
                                        }
                                    }.runTaskTimer(RPG_Base.getInstance(), 0, 5);
                                }
                            }else{
                                player1.sendMessage("Path doesn't exist.");
                            }
                            return true;
                        }else{
                            if(path.length > 0){
                                player1.sendMessage("Path exists! Drawing the path...");
                                Random random = new Random();
                                Color color = Color.fromRGB(random.nextInt(0, 255), random.nextInt(0,255), random.nextInt(0, 255));
                                for(Location location : path){
                                    new BukkitRunnable() {
                                        int elapsedTicks = 0;
                                        final Location particleLocation = location.clone(); // Clone the location to prevent overwriting
                                        @Override
                                        public void run() {
                                            // Stop the task after 20 seconds
                                            if (elapsedTicks >= 20 * 20) {
                                                this.cancel();
                                                return;
                                            }

                                            Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1.0F);
                                            particleLocation.getWorld().spawnParticle(Particle.DUST, particleLocation.clone().add(0.5, 1, 0.5), 50, dustOptions);

                                            elapsedTicks += 5; // Update the elapsed ticks
                                        }
                                    }.runTaskTimer(RPG_Base.getInstance(), 0, 5);
                                }
                            }else{
                                player1.sendMessage("Path doesn't exist.");
                            }
                        }
                    }

                } // @Deprecated
                if (args[0].equalsIgnoreCase("addSkill")){
                    if(args.length < 2){
                        return false;
                    }

                    if(Bukkit.getServer().getPlayer(args[1]) != null){
                        player = Bukkit.getServer().getPlayer(args[1]);
                        CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());
                        if(SkillRegistry.getSkill(args[2]) != null){
                            Skill addedSkill = SkillRegistry.getSkill(args[2]);
                            addedSkill.clone().level += 1;
                            cPlayer.playerSkills.unlockedSkillList.add(addedSkill);
                        }
                    }

                    return true;
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
                    list.add("compareItems");
                    list.add("checkPathTo");
                    list.add("addSkill");
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
                if (args.length >= 2 &&
                        (args[0].equalsIgnoreCase("levelAdd") ||
                         args[0].equalsIgnoreCase("levelRem"))) {
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
                if (args.length >= 2 && args[0].equalsIgnoreCase("addSkill")){
                    if (args.length == 2) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            list.add(player.getName());
                        }
                    } else if (args.length == 3) {
                        list.addAll(SkillRegistry.registeredSkills.keySet());
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
