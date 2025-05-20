package rpg.rpg_base.CustomizedClasses.PlayerHandler;

import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;

import java.util.ArrayList;
import java.util.List;

public class PlayerSkills {
    public int strengthLvl = 0;
    public float strengthDmgBoost = 0.05F;

    public int enduranceLvl = 0;
    public float enduranceHealthBoost = 0.1F;

    public int dexterityLvl = 0;
    public int agilityLvl = 0;
    public int intelligenceLvl = 0;

    public List<Skill> unlockedSkillList = new ArrayList<>();

    public void activateSkills(CPlayer player){
        for(Skill skill : unlockedSkillList){
            skill.activateEffect(player);
        }
    }

    public void reactivateSkills(CPlayer player){
        for(Skill skill : unlockedSkillList){
            skill.reactivate(player);
        }
    }
}
