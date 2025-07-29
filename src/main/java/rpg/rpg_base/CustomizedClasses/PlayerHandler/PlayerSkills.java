package rpg.rpg_base.CustomizedClasses.PlayerHandler;

import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;

import java.util.HashMap;

public class PlayerSkills {
    public int strengthLvl = 0;
    public float strengthDmgBoost = 0.05F;

    public int enduranceLvl = 0;
    public float enduranceHealthBoost = 0.1F;

    public int dexterityLvl = 0;
    public int agilityLvl = 0;
    public int intelligenceLvl = 0;

    public HashMap<String, Skill> unlockedSkillMap = new HashMap<>();

    public void activateSkills(CPlayer player){
        for(Skill skill : unlockedSkillMap.values()){
            skill.activateEffect(player);
        }
    }

    public void reactivateSkills(CPlayer player){
        for(Skill skill : unlockedSkillMap.values()){
            skill.reactivate(player);
        }
    }
}
