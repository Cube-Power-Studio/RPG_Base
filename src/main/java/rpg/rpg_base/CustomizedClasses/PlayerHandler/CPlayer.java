package rpg.rpg_base.CustomizedClasses.PlayerHandler;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomizedClasses.BonusStat;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.GUIs.ActionBar;
import rpg.rpg_base.RPG_Base;

import java.util.*;

public class CPlayer {
    public final Player player;

    public int level = 0;
    public int skillPoints = 0;
    public int spentSkillPoints = 0;
    public int totalSkillPoints = 0;
    public int abilityPoints = 0;
    public int spentAbilityPoints = 0;
    public int totalAbilityPoints = 0;


    public int xp = 0;
    public int xpToNextLvl = 0;
    public int totalXp = 0;

    public PlayerSkills playerSkills = new PlayerSkills();

    public Map<stat, List<BonusStat>> statBonuses = new HashMap<>();
    public int baseMaxHP = 100;
    public int maxHP = 0;
    public int currentHP = 0;
    public float baseHealthRegen = 0.5F;
    public float healthRegen = 1;
    public int armor = 0;
    public int baseDamage = 1;
    public int damage = 0;
    public int sanity = 0;
    public int maxSanity = 0;

    public Object killer;

    public PlayerRegenTask regenTask = new PlayerRegenTask(this);

    public static HashMap<UUID, CPlayer> customPlayer = new HashMap<>();

    public CPlayer(Player player) {
        this.player = player;

        BukkitRunnable actionBar = new BukkitRunnable() {
            @Override
            public void run() {
                ActionBar.statisticBar(CPlayer.this);
            }
        };

        regenTask.runTaskTimer(RPG_Base.getInstance(), 0L, 20L);
        actionBar.runTaskTimer(RPG_Base.getInstance(), 0L, 1L);
        playerSkills.activateSkills(this);
    }

    public void updateStats(){

        if(xp >= xpToNextLvl){
            xpToNextLvl = Math.toIntExact(Math.round(100.0 * Math.pow(2.0, level / 4.0)));

            if(xp >= xpToNextLvl) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                xp -= xpToNextLvl;
                xpToNextLvl = Math.toIntExact(Math.round(100.0 * Math.pow(2.0, level / 4.0)));
                level++;
            }
        }

        totalSkillPoints = level*2;
        skillPoints = totalSkillPoints - spentSkillPoints;

        totalAbilityPoints = level/3;
        abilityPoints = totalAbilityPoints - spentAbilityPoints;

        ItemManager.updateItems(player.getInventory());

        int hpFromItems = 0;
        int dmgFromItems = 0;
        int armorFromItems = 0;
        int hpRegenFromItems = 0;

        for(ItemStack item : player.getInventory().getArmorContents()){
            hpFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemHealth, item));
            dmgFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemDamage, item));
            armorFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemArmor, item));
            hpRegenFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemHealthRegen, item));
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        hpFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemHealth, itemInHand));
        dmgFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemDamage, itemInHand));
        armorFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemArmor, itemInHand));
        hpRegenFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemHealthRegen, itemInHand));

        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        hpFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemHealth, itemInOffHand));
        dmgFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemDamage, itemInOffHand));
        armorFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemArmor, itemInOffHand));
        hpRegenFromItems += Integer.parseInt((String) CItem.getTagValue(CItem.itemHealthRegen, itemInOffHand));

        maxHP = (int) (baseMaxHP + hpFromItems + ((baseMaxHP + hpFromItems) * playerSkills.enduranceLvl * playerSkills.enduranceHealthBoost));
        healthRegen = (int) (baseHealthRegen + hpRegenFromItems);
        damage = (int) ((baseDamage + dmgFromItems +((baseDamage + dmgFromItems) * playerSkills.strengthLvl * playerSkills.strengthDmgBoost)));
        armor = armorFromItems;

        maxHP += addStatBonus(maxHP, stat.health);
        healthRegen += addStatBonus((int) healthRegen, stat.healthRegen);
        damage += addStatBonus(damage, stat.damage);
        armor += addStatBonus(armor, stat.armor);

        playerSkills.reactivateSkills(this);
    }

    public void dealDamage(int damage){
        regenTask.cooldown = 5;
        if(damage-armor > currentHP){
            player.setHealth(0);
            currentHP = maxHP;
        }else{
            currentHP -= damage - armor;
        }
        updateStats();
    }

    public void dealDamage(int damage, CEntity damager){
        regenTask.cooldown = 5;
        if(damage-armor >= currentHP){
            player.setHealth(0);
            currentHP = maxHP;
            killer = damager;
        }else{
            currentHP -= Math.max(damage - armor,0);
        }
        updateStats();
    }

    public void dealDamage(int damage, CPlayer damager){
        regenTask.cooldown = 5;
        if(damage-armor >= currentHP){
            player.setHealth(0);
            currentHP = maxHP;
            killer = damager;
        }else{
            currentHP -= Math.max(damage - armor,0);
        }
        updateStats();
    }

    public void heal(int amount){
        currentHP += amount;
    }

    public void regenHp() {
        currentHP = (int) Math.min(currentHP + healthRegen, maxHP);
    }

    public static CPlayer getPlayerByUUID(UUID uuid){
        return customPlayer.get(uuid);
    }

    public Player getPlayer() {
        return player;
    }

    public int addStatBonus(int amount, stat stat){
        List<BonusStat> activeBonuses = statBonuses.getOrDefault(stat, new ArrayList<>());

        return (int) Math.round(activeBonuses.stream()
                .sorted(Comparator.comparing(bonusStat -> bonusStat.type))
                .mapToDouble(bonusStat -> {
                    switch (bonusStat.type) {
                        case flat -> {
                            return bonusStat.amount; // Add directly
                        }
                        case scale -> {
                            return amount * (bonusStat.amount - 1); // Adjust by removing the base 1
                        }
                        default -> {
                            return 0; // Just in case, prevent errors
                        }
                    }
                })
                .sum());
    }

    public enum stat{
        health,
        maxHealth,
        healthRegen,
        armor,

        damage,

        xpgainBonus
    }
}
