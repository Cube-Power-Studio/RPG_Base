package rpg.rpg_base.Data;

import org.bukkit.entity.Player;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;

import java.io.File;


public class PlayerDataManager {

    public static void savePlayerData(Player targetPlayer){
        CPlayer player = CPlayer.getPlayerByUUID(targetPlayer.getUniqueId());

        DataBaseManager.addColumnValue(DataBaseColumn.LVL, player.level, player.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.ELVL, player.playerSkills.enduranceLvl, player.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.SLVL, player.playerSkills.strengthLvl, player.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.XP, player.xp, player.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.TOTALXP, player.totalXp, player.getPlayer().getUniqueId().toString());
        DataBaseManager.addColumnValue(DataBaseColumn.USERNAME, player.getPlayer().getName(), player.getPlayer().getUniqueId().toString());
    }
}