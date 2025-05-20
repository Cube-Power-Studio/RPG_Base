package rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import rpg.rpg_base.CustomizedClasses.BonusStat;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.RPG_Base;

import java.util.*;

public abstract class Skill implements Cloneable {
    public static NamespacedKey skillKey = new NamespacedKey(RPG_Base.getInstance(), "skillKey");

    public Map<String, Integer> levelRequirements = new HashMap<>();

    public int level = 0;
    public int maxLevel = 0;
    public String displayName = "";
    public String regName = "";

    protected boolean active = false;
    protected boolean isOnCooldown;
    protected List<BonusStat> bonusStats = new ArrayList<>();

    public int slot;
    public int page;
    public String type;
    public List<Component> description;

    public Skill() {
        levelRequirements.put("str", 0);
        levelRequirements.put("end", 0);
        levelRequirements.put("dex", 0);
        levelRequirements.put("int", 0);
        levelRequirements.put("agi", 0);
        levelRequirements.put("gen", 0);
    }

    public abstract void activateEffect(CPlayer player);

    public abstract void deactivateEffect(CPlayer player);

    public abstract void updateDescription();

    public void reactivate(CPlayer player) {
        deactivateEffect(player);
        activateEffect(player);
    }

    public boolean meetsRequirements(CPlayer player) {
        return (levelRequirements.getOrDefault("str", 0) <= player.playerSkills.strengthLvl &&
                levelRequirements.getOrDefault("end", 0) <= player.playerSkills.enduranceLvl &&
                levelRequirements.getOrDefault("dex", 0) <= player.playerSkills.dexterityLvl &&
                levelRequirements.getOrDefault("int", 0) <= player.playerSkills.intelligenceLvl &&
                levelRequirements.getOrDefault("agi", 0) <= player.playerSkills.agilityLvl &&
                levelRequirements.getOrDefault("gen", 0) <= player.level);
    }
    protected void setCooldown(int seconds, Player player){
        isOnCooldown = true;

        Bukkit.getScheduler().runTaskLater(RPG_Base.getInstance(), () -> {
            isOnCooldown = false;
            player.sendMessage("§a" + displayName + " ready!");
        }, 20L * seconds);
    }

    @Override
    public Skill clone() {
        try {
            Skill clone = (Skill) super.clone();

            // Deep copy of levelRequirements to prevent reference issues
            clone.levelRequirements = new HashMap<>(this.levelRequirements);

            clone.regName = this.regName;
            clone.displayName = this.displayName;
            clone.level = this.level;
            clone.maxLevel = this.maxLevel;
            clone.active = this.active;
            clone.slot = this.slot;
            clone.page = this.page;
            clone.type = this.type;

            // Clone bonusStat if necessary (assuming BonusStat is cloneable)
            if (this.bonusStats != null) {
                clone.bonusStats = new ArrayList<>();
                for (BonusStat stat : this.bonusStats) {
                    clone.bonusStats.add(stat.clone()); // Make sure BonusStat implements clone() properly
                }
            } else {
                clone.bonusStats = null;
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should never happen since we're Cloneable
        }
    }

}
