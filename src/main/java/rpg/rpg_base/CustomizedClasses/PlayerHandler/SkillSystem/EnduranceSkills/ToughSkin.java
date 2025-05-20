package rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.EnduranceSkills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import rpg.rpg_base.CustomizedClasses.BonusStat;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.RegisterSkill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

@RegisterSkill(name="Tghsk")
public class ToughSkin extends Skill {

    public ToughSkin(){
        maxLevel = 5;
        page = 10;
        slot = 9;

        levelRequirements.put("str", 2);
        levelRequirements.put("end", 5);
        levelRequirements.put("gen", 3);
        displayName = "Tough Skin";
        regName = "Tghsk";

        type = "end";

        description = List.of(
                Component.text("Your skin became tougher.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("You are more resistant to attacks.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("+", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(10 + 5 * level + "❤", NamedTextColor.RED)
                                .decoration(TextDecoration.ITALIC, false)),
                Component.text("+", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(3 + 2 * level + "⛨", NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false))
        );
    }

    @Override
    public void activateEffect(CPlayer player) {
        if(!active) {
            List<BonusStat> healthBonusList = player.statBonuses.getOrDefault(CPlayer.stat.health, new ArrayList<>());
            List<BonusStat> defenseBonusList = player.statBonuses.getOrDefault(CPlayer.stat.armor, new ArrayList<>());

            BonusStat healthBonus = new BonusStat(10+5*level, BonusStat.bonusType.flat);
            BonusStat defenseBonus = new BonusStat(3+2*level, BonusStat.bonusType.flat);

            healthBonusList.add(healthBonus);
            defenseBonusList.add(defenseBonus);

            bonusStats.add(healthBonus);
            bonusStats.add(defenseBonus);

            player.statBonuses.put(CPlayer.stat.health, healthBonusList);
            player.statBonuses.put(CPlayer.stat.armor, defenseBonusList);
            active = true;
        }
    }

    @Override
    public void deactivateEffect(CPlayer player) {
        if(active){
            for(CPlayer.stat stat : CPlayer.stat.values()){
                player.statBonuses.compute(stat, (_, list) -> {
                    if (list == null) list = new ArrayList<>(); // Create a new list if absent
                    list.removeAll(bonusStats); // Remove all matching BonusStat entries
                    return list; // Return the modified list
                });
            }

            bonusStats.clear();
            active = false;
        }
    }

    @Override
    public void updateDescription(){
        description = List.of(
                Component.text("Your skin became tougher.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("You are more resistant to attacks.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("+", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(10 + 5 * level + "❤", NamedTextColor.RED)
                                .decoration(TextDecoration.ITALIC, false)),
                Component.text("+", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(3 + 2 * level + "⛨", NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false))
        );
    }
}
