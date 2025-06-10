package rpg.rpg_base.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.Data.DataBaseManager;
import rpg.rpg_base.GUIs.SkillGui;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Shops.ShopsManager;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class RegisteredCommands {
    public List<LiteralCommandNode> commandBuilders = new ArrayList<>();

    public void register(){
        List<String> items = new ArrayList<>();
        List<String> players = new ArrayList<>();
        List<String> skillTypes = List.of("general", "strength", "endurance", "intelligence", "dexterity", "agility");

        LiteralCommandNode<CommandSourceStack> command = Commands.literal("rpg")
                .requires(sender -> sender.getSender().hasPermission("rpg_base.admin"))
                .then(Commands.literal("debug")
                    .then(Commands.literal("saveItemInHand")
                            .executes(ctx ->{
                                Player player = (Player)ctx.getSource().getSender();
                                player.updateCommands();
                                DataBaseManager.addColumnValueToItemTable(
                                        "itemSerial",
                                        Base64.getEncoder().encodeToString(ItemStack.serializeItemsAsBytes(new ItemStack[]{player.getInventory().getItemInMainHand()})),
                                        CPlayer.getPlayerByUUID(player.getUniqueId())
                                );
                                DataBaseManager.addColumnValueToItemTable(
                                        "playerUUID",
                                        String.valueOf(player.getUniqueId()),
                                        CPlayer.getPlayerByUUID(player.getUniqueId())
                                );
                                DataBaseManager.addColumnValueToItemTable(
                                        "place",
                                        "inventory",
                                        CPlayer.getPlayerByUUID(player.getUniqueId())
                                );
                                return 0;
                            }))
                    .then(Commands.literal("loadItemToHand")
                            .executes(ctx ->{
                                Player player = (Player)ctx.getSource().getSender();
                                String serial = DataBaseManager.getValueOfCellInItemTable(
                                        "itemSerial",
                                        CPlayer.getPlayerByUUID(player.getUniqueId())
                                );
                                player.getInventory().addItem(ItemStack.deserializeItemsFromBytes(Base64.getDecoder().decode(serial)));
                                return 0;
                            }))
                )
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            Player sender = (Player) ctx.getSource().getSender();
                            try {
                                RPG_Base.getInstance().updateConfig();
                                sender.sendMessage("Plugin reloaded successfully!");
                                return 1;
                            } catch (Error e) {
                                sender.sendMessage("Something went wrong, check console for further details");
                                RPG_Base.getInstance().getLogger().info("While performing command /RPG reload something went wrong: " + e);
                                return 0;
                            }
                        })
                )
                .then(Commands.literal("give")
                        .then(Commands.argument("item", StringArgumentType.word())
                                .suggests((_, builder) -> {
                                    items.addAll(CItem.customItemsByName.keySet());
                                    items.stream()
                                            .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(builder::suggest);

                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    Player sender = (Player) ctx.getSource().getSender();

                                    ItemStack customItem = CItem.customItemsByName.get(ctx.getArgument("item", String.class)).getItem();
                                    sender.getInventory().addItem(customItem);

                                    return 1;
                                })
                                .then(Commands.argument("target", StringArgumentType.word())
                                        .suggests((_, builder) -> {
                                            for(Player player : Bukkit.getOnlinePlayers()){players.add(player.getName());}
                                            players.stream()
                                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    .forEach(builder::suggest);

                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> {
                                            Player sender = (Player) ctx.getSource().getSender();
                                            Player player = RPG_Base.getInstance().getServer().getPlayer(ctx.getArgument("target", String.class));
                                            if (player == null) {
                                                sender.sendMessage(Component.text("Invalid player!").color(NamedTextColor.RED));
                                                return 0;
                                            }

                                            ItemStack customItem = CItem.customItemsByName.get(ctx.getArgument("item", String.class)).getItem();
                                            player.getInventory().addItem(customItem);

                                            return 1;
                                        })
                                ))
                )
                .then(Commands.literal("level")
                        .then(Commands.literal("add")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((_, builder) -> {
                                            skillTypes.stream()
                                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    .forEach(builder::suggest);

                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .suggests((_, builder) -> {
                                                    builder.suggest(1);
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> {
                                                    CPlayer player = CPlayer.getPlayerByUUID(((Player) ctx.getSource().getSender()).getUniqueId());

                                                    switch (ctx.getArgument("type", String.class)) {
                                                        case "general":
                                                            player.level += ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "strength":
                                                            player.playerSkills.strengthLvl += ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "endurance":
                                                            player.playerSkills.enduranceLvl += ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "intelligence":
                                                            player.playerSkills.intelligenceLvl += ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "dexterity":
                                                            player.playerSkills.dexterityLvl += ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "agility":
                                                            player.playerSkills.agilityLvl += ctx.getArgument("amount", int.class);
                                                            break;
                                                        default:
                                                            ctx.getSource().getSender().sendMessage("Invalid level type!!!");
                                                            return 0;
                                                    }

                                                    player.updateStats();

                                                    return 1;
                                                })
                                                .then(Commands.argument("target", StringArgumentType.word())
                                                        .suggests((_, builder) -> {
                                                            for (Player player : Bukkit.getOnlinePlayers()) {players.add(player.getName());}
                                                            players.stream()
                                                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                                    .forEach(builder::suggest);

                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ctx -> {
                                                            CPlayer player = CPlayer.getPlayerByUUID(Bukkit.getPlayerUniqueId(ctx.getArgument("target", String.class)));

                                                            switch (ctx.getArgument("type", String.class)) {
                                                                case "general":
                                                                    player.level += ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "strength":
                                                                    player.playerSkills.strengthLvl += ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "endurance":
                                                                    player.playerSkills.enduranceLvl += ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "intelligence":
                                                                    player.playerSkills.intelligenceLvl += ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "dexterity":
                                                                    player.playerSkills.dexterityLvl += ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "agility":
                                                                    player.playerSkills.agilityLvl += ctx.getArgument("amount", int.class);
                                                                    break;
                                                                default:
                                                                    ctx.getSource().getSender().sendMessage("Invalid level type!!!");
                                                                    return 0;
                                                            }

                                                            player.updateStats();

                                                            return 1;
                                                        })
                                                ))))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((_, builder) -> {
                                            skillTypes.stream()
                                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    .forEach(builder::suggest);

                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .suggests((_, builder)->{
                                                    builder.suggest(1);
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> {
                                                    CPlayer player = CPlayer.getPlayerByUUID(((Player)ctx.getSource().getSender()).getUniqueId());

                                                    switch(ctx.getArgument("type", String.class)){
                                                        case "general":
                                                            player.level -= ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "strength":
                                                            player.playerSkills.strengthLvl -= ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "endurance":
                                                            player.playerSkills.enduranceLvl -= ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "intelligence":
                                                            player.playerSkills.intelligenceLvl -= ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "dexterity":
                                                            player.playerSkills.dexterityLvl -= ctx.getArgument("amount", int.class);
                                                            break;
                                                        case "agility":
                                                            player.playerSkills.agilityLvl -= ctx.getArgument("amount", int.class);
                                                            break;
                                                        default:
                                                            ctx.getSource().getSender().sendMessage("Invalid level type!!!");
                                                            return 0;
                                                    }

                                                    player.updateStats();

                                                    return 1;
                                                })
                                                .then(Commands.argument("target", StringArgumentType.word())
                                                        .suggests((_, builder) -> {
                                                            for(Player player : Bukkit.getOnlinePlayers()){players.add(player.getName());}
                                                            players.stream()
                                                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                                    .forEach(builder::suggest);

                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ctx -> {
                                                            CPlayer player = CPlayer.getPlayerByUUID(Bukkit.getPlayerUniqueId(ctx.getArgument("target", String.class)));

                                                            switch(ctx.getArgument("type", String.class)){
                                                                case "general":
                                                                    player.level -= ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "strength":
                                                                    player.playerSkills.strengthLvl -= ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "endurance":
                                                                    player.playerSkills.enduranceLvl -= ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "intelligence":
                                                                    player.playerSkills.intelligenceLvl -= ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "dexterity":
                                                                    player.playerSkills.dexterityLvl -= ctx.getArgument("amount", int.class);
                                                                    break;
                                                                case "agility":
                                                                    player.playerSkills.agilityLvl -= ctx.getArgument("amount", int.class);
                                                                    break;
                                                                default:
                                                                    ctx.getSource().getSender().sendMessage("Invalid level type!!!");
                                                                    return 0;
                                                            }

                                                            player.updateStats();

                                                            return 1;
                                                        })
                                                )))
                        )
                )
                .then(Commands.literal("openShop")
                        .then(Commands.argument("shop", StringArgumentType.word())
                                .suggests((_, builder) -> {
                                    for(String str : ShopsManager.shopRegister.keySet()){
                                        builder.suggest(str);
                                    }

                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    if(ctx.getSource().getSender() instanceof Player player){
                                        String shopName = ctx.getArgument("shop", String.class);

                                        if(ShopsManager.shopRegister.containsKey(shopName)){
                                            ShopsManager.openShop(player, shopName);
                                        }else{
                                            ctx.getSource().getSender().sendMessage("Shop doesn't exist! Check syntax");
                                        }
                                    }else{
                                        ctx.getSource().getSender().sendMessage("You can only use this command from chat command");
                                    }
                                    return 1;
                                })))

                .build();

        commandBuilders.add(command);

        LiteralCommandNode<CommandSourceStack> skillsCommand = Commands.literal("skills")
                .executes(ctx -> {
                    RPG_Base.getInstance().guiManager.openGui(new SkillGui(RPG_Base.getInstance()), (Player) ctx.getSource().getSender());

                    return 0;
                })
                .build();

        commandBuilders.add(skillsCommand);
    }
}
