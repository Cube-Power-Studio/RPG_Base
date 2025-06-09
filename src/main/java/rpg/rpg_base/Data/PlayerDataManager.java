package rpg.rpg_base.Data;

import org.bukkit.entity.Player;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.SkillRegistry;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;

import java.util.Objects;


public class PlayerDataManager {

    public static void savePlayerData(Player targetPlayer) {
        CPlayer player = CPlayer.getPlayerByUUID(targetPlayer.getUniqueId());

        DataBaseManager.addColumnValueToUserTable("USERNAME", player.getPlayer().getName(), player);
        DataBaseManager.addColumnValueToUserTable("LVL", player.level, player);
        DataBaseManager.addColumnValueToUserTable("XP", player.xp, player);
        DataBaseManager.addColumnValueToUserTable("TOTALXP", player.totalXp, player);
        DataBaseManager.addColumnValueToUserTable("ELVL", player.playerSkills.enduranceLvl, player);
        DataBaseManager.addColumnValueToUserTable("SLVL", player.playerSkills.strengthLvl, player);
        DataBaseManager.addColumnValueToUserTable("DLVL", player.playerSkills.dexterityLvl, player);
        DataBaseManager.addColumnValueToUserTable("ALVL", player.playerSkills.agilityLvl, player);
        DataBaseManager.addColumnValueToUserTable("ILVL", player.playerSkills.intelligenceLvl, player);
        DataBaseManager.addColumnValueToUserTable("GOLD", MoneyManager.getPlayerGold(player.getPlayer()), player);
        DataBaseManager.addColumnValueToUserTable("RUNICSIGILS", MoneyManager.getPlayerRunicSigils(player.getPlayer()), player);
        DataBaseManager.addColumnValueToUserTable("GUILDMEDALS", MoneyManager.getPlayerGuildMedals(player.getPlayer()), player);
        DataBaseManager.addColumnValueToUserTable("SPENTSKILLPOINTS", player.spentSkillPoints, player);
        DataBaseManager.addColumnValueToUserTable("SPENTABILITYPOINTS", player.spentAbilityPoints, player);
        DataBaseManager.addColumnValueToUserTable("UNLOCKEDABILITIES", String.join(";", player.playerSkills.unlockedSkillList.stream()
                .map(skill -> skill.regName + "," + skill.level)
                .toList()), player);
    }

    public static void loadPlayerData(Player targetPlayer){
        CPlayer player = CPlayer.getPlayerByUUID(targetPlayer.getUniqueId());

        player.level = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("LVL", player));
        player.xp = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("XP", player));
        player.totalXp = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("TOTALXP", player));
        player.playerSkills.enduranceLvl = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("ELVL", player));
        player.playerSkills.strengthLvl = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("SLVL", player));
        player.playerSkills.dexterityLvl = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("DLVL", player));
        player.playerSkills.agilityLvl = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("ALVL", player));
        player.playerSkills.intelligenceLvl = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("ILVL", player));
        MoneyManager.setPlayerGold(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("GOLD", player)));
        MoneyManager.setPlayerRunicSigils(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("RUNICSIGILS", player)));
        MoneyManager.setPlayerGuildMedals(player.getPlayer(), Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("GUILDMEDALS", player)));
        player.spentSkillPoints = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("SPENTSKILLPOINTS", player));
        player.spentAbilityPoints = Integer.parseInt(DataBaseManager.getValueOfCellInUserTable("SPENTABILITYPOINTS", player));

        String unlockedAbilities = DataBaseManager.getValueOfCellInUserTable("UNLOCKEDABILITIES", player);
        if(!Objects.equals(unlockedAbilities, "0")){
            for(String str : unlockedAbilities.split(";")){
                if(str.isBlank()) continue;
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