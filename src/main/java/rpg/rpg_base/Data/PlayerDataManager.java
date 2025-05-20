package rpg.rpg_base.Data;

import org.bukkit.entity.Player;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.SkillRegistry;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;

import java.io.File;
import java.util.Objects;


public class PlayerDataManager {

    public static void savePlayerData(Player targetPlayer) {
        CPlayer player = CPlayer.getPlayerByUUID(targetPlayer.getUniqueId());

        DataBaseManager.addColumnValue(DataBaseColumn.USERNAME, player.getPlayer().getName(), player);
        DataBaseManager.addColumnValue(DataBaseColumn.LVL, player.level, player);
        DataBaseManager.addColumnValue(DataBaseColumn.XP, player.xp, player);
        DataBaseManager.addColumnValue(DataBaseColumn.TOTALXP, player.totalXp, player);
        DataBaseManager.addColumnValue(DataBaseColumn.ELVL, player.playerSkills.enduranceLvl, player);
        DataBaseManager.addColumnValue(DataBaseColumn.SLVL, player.playerSkills.strengthLvl, player);
        DataBaseManager.addColumnValue(DataBaseColumn.DLVL, player.playerSkills.dexterityLvl, player);
        DataBaseManager.addColumnValue(DataBaseColumn.ALVL, player.playerSkills.agilityLvl, player);
        DataBaseManager.addColumnValue(DataBaseColumn.ILVL, player.playerSkills.intelligenceLvl, player);
        DataBaseManager.addColumnValue(DataBaseColumn.GOLD, MoneyManager.getPlayerGold(player.getPlayer()), player);
        DataBaseManager.addColumnValue(DataBaseColumn.RUNICSIGILS, MoneyManager.getPlayerRunicSigils(player.getPlayer()), player);
        DataBaseManager.addColumnValue(DataBaseColumn.GUILDMEDALS, MoneyManager.getPlayerGuildMedals(player.getPlayer()), player);
        DataBaseManager.addColumnValue(DataBaseColumn.SPENTSKILLPOINTS, player.spentSkillPoints, player);
        DataBaseManager.addColumnValue(DataBaseColumn.SPENTABILITYPOINTS, player.spentAbilityPoints, player);
        DataBaseManager.addColumnValue(DataBaseColumn.UNLOCKEDABILITIES, String.join(";", player.playerSkills.unlockedSkillList.stream()
                .map(skill -> skill.regName + "," + skill.level)
                .toList()), player);
    }

    public static void loadPlayerData(Player targetPlayer){
        CPlayer player = CPlayer.getPlayerByUUID(targetPlayer.getUniqueId());

        player.level = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.LVL, player));
        player.xp = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.XP, player));
        player.totalXp = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.TOTALXP, player));
        player.playerSkills.enduranceLvl = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.ELVL, player));
        player.playerSkills.strengthLvl = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.SLVL, player));
        player.playerSkills.dexterityLvl = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.DLVL, player));
        player.playerSkills.agilityLvl = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.ALVL, player));
        player.playerSkills.intelligenceLvl = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.ILVL, player));
        MoneyManager.setPlayerGold(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.GOLD, player)));
        MoneyManager.setPlayerRunicSigils(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.RUNICSIGILS, player)));
        MoneyManager.setPlayerGuildMedals(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.GUILDMEDALS, player)));
        player.spentSkillPoints = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.SPENTSKILLPOINTS, player));
        player.spentAbilityPoints = Integer.parseInt(DataBaseManager.getValueOfCell(DataBaseColumn.SPENTABILITYPOINTS, player));

        String unlockedAbilities = DataBaseManager.getValueOfCell(DataBaseColumn.UNLOCKEDABILITIES, player);
        if(!Objects.equals(unlockedAbilities, "0")){
            for(String str : unlockedAbilities.split(";")){
                String[] parts = str.split(",");  // Split once and reuse
                Skill skillToAdd = SkillRegistry.getSkill(parts[0]).clone();
                skillToAdd.level = Integer.parseInt(parts[1]);
                player.playerSkills.unlockedSkillList.add(skillToAdd);
            }
        }

        player.updateStats();
        player.currentHP = player.maxHP;
    }
}