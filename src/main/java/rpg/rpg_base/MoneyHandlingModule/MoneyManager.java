package rpg.rpg_base.MoneyHandlingModule;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class MoneyManager {
    public static HashMap<Player, Integer> playerGoldRegistry = new HashMap<>();
    public static HashMap<Player, Integer> playerRunicSigilsRegistry = new HashMap<>();
    public static HashMap<Player, Integer> playerGuildMedalsRegistry = new HashMap<>();


    //Gold management

    public static void payPlayer(Player sender, Player target, int amount){
        if(amount > 0) {
            if (amount <= getPlayerGold(sender)) {
                remPlayerGold(sender, amount);
                addPlayerGold(target, amount);
            }
        }
    }


    public static void setPlayerGold(Player player, int amount){
        playerGoldRegistry.put(player, amount);
    }
    public static int getPlayerGold(Player player){
        return playerGoldRegistry.getOrDefault(player, 0);
    }
    public static void addPlayerGold(Player player, int amount){
        playerGoldRegistry.put(player, getPlayerGold(player) + amount);
    }
    public static void remPlayerGold(Player player, int amount){
        playerGoldRegistry.put(player, getPlayerGold(player) - amount);
    }

    //RunicSigils management

    public static void setPlayerRunicSigils(Player player, int amount){
        playerRunicSigilsRegistry.put(player, amount);
    }
    public static int getPlayerRunicSigils(Player player){
        return playerRunicSigilsRegistry.getOrDefault(player, 0);
    }
    public static void addPlayerRunicSigils(Player player, int amount){
        playerRunicSigilsRegistry.put(player, getPlayerRunicSigils(player) + amount);
    }
    public static void remPlayerRunicSigils(Player player, int amount){
        playerRunicSigilsRegistry.put(player, getPlayerRunicSigils(player) - amount);
    }

    //GuildMedals management

    public static void setPlayerGuildMedals(Player player, int amount){
        playerGuildMedalsRegistry.put(player, amount);
    }
    public static int getPlayerGuildMedals(Player player){
        return playerGuildMedalsRegistry.getOrDefault(player, 0);
    }
    public static void addPlayerGuildMedals(Player player, int amount){
        playerGuildMedalsRegistry.put(player, getPlayerGuildMedals(player) + amount);
    }
    public static void remPlayerGuildMedals(Player player, int amount){
        playerGuildMedalsRegistry.put(player, getPlayerGuildMedals(player) - amount);
    }
}
