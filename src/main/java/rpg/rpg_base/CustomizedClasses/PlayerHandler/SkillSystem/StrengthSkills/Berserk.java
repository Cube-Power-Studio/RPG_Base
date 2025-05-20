package rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.StrengthSkills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import rpg.rpg_base.CustomizedClasses.BonusStat;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.RegisterSkill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;

import java.util.ArrayList;
import java.util.List;

@RegisterSkill(name = "Brsrk")
public class Berserk extends Skill {
    public Berserk(){
        maxLevel = 3;
        page = 60;
        slot = 9;

        levelRequirements.put("str", 5);
        levelRequirements.put("end", 2);
        levelRequirements.put("agi", 3);
        levelRequirements.put("gen", 5);
        displayName = "Berserk";
        regName = "Brsrk";

        type = "str";

        description = List.of(
                Component.text("Gain increased damage the lower your HP.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("At 50% HP or less, damage increases by up to ", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text((int) ((1.15 + level * 0.1 - 1.0) * 100), NamedTextColor.RED)
                                .decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("%", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)))
        );
    }

    @Override
    public void activateEffect(CPlayer player) {
        if(!active) {
            List<BonusStat> bonusStatList = player.statBonuses.getOrDefault(CPlayer.stat.damage, new ArrayList<>());


            double bonusAmount = 1.0; // Default (no change)

            double hpPercentage = (double) player.currentHP / player.maxHP;
            double baseMaxBonus = 1.15; // Base max bonus (without level scaling)
            double bonusPerLevel = 0.10; // 10% increase per level
            double maxBonus = baseMaxBonus + (player.level * bonusPerLevel); // Scaled max bonus

            if (hpPercentage <= 0.5) {
                bonusAmount = 1.10 + ((maxBonus - 1.10) * ((0.5 - hpPercentage) / 0.4));
                bonusAmount = Math.min(bonusAmount, maxBonus); // Clamp max based on level
            }
            bonusStats.add(new BonusStat(bonusAmount, BonusStat.bonusType.scale));

            bonusStatList.addAll(bonusStats);
            player.statBonuses.put(CPlayer.stat.damage, bonusStatList);
            active = true;
//            System.out.println("Added damage: " + bonusStat.amount);
        }
    }

    @Override
    public void deactivateEffect(CPlayer player){
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
    public void updateDescription() {
        description = List.of(
                Component.text("Gain increased damage the lower your HP.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("At 50% HP or less, damage increases by up to ", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text((int) Math.round((1.15 + level * 0.1 - 1.0) * 100), NamedTextColor.RED)
                                .decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("%", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)))
        );
    }
}
