package rpg.rpg_base.CustomizedClasses.PlayerHandler;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.GUIs.ActionBar;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.UUID;

public class CPlayer {
    public final Player player;

    public int level = 0;
    public int skillPoints = 0;
    public int spentSkillPoints = 0;
    public int totalSkillPoints = 0;

    public int xp = 0;
    public int xpToNextLvl = 0;
    public int totalXp = 0;

    public PlayerSkills playerSkills = new PlayerSkills();

    public int baseMaxHP = 100;
    public int maxHP = 0;
    public int currentHP = 0;
    public float baseHealthRegen = 0.5F;
    public float healthRegen = 0;
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

        actionBar.runTaskTimer(RPG_Base.getInstance(), 0L, 20L);
    }

    public void updateStats(){

        if(xp >= xpToNextLvl){
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
            xp -= xpToNextLvl;
            level++;

            totalSkillPoints += 2;
        }

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

        maxHP = (int) (baseMaxHP + ((baseMaxHP + hpFromItems) * playerSkills.enduranceLvl * playerSkills.enduranceHealthBoost));
        healthRegen = (int) (baseHealthRegen + hpRegenFromItems);
        damage = (int) (baseDamage +((baseDamage + dmgFromItems) * playerSkills.strengthLvl * playerSkills.strengthDmgBoost));
        armor = armorFromItems;
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
        if(damage-armor > currentHP){
            player.setHealth(0);
            currentHP = maxHP;
            killer = damager;
        }else{
            currentHP -= damage - armor;
        }
        updateStats();
    }

    public void dealDamage(int damage, CPlayer damager){
        regenTask.cooldown = 5;
        if(damage-armor > currentHP){
            player.setHealth(0);
            currentHP = maxHP;
            killer = damager;
        }else{
            currentHP -= damage - armor;
        }
        updateStats();
    }

    public void regenHp(){
        currentHP = (int) Math.clamp(currentHP + healthRegen, 0, maxHP);
    }

    public static CPlayer getPlayerByUUID(UUID uuid){
        return customPlayer.get(uuid);
    }

    public Player getPlayer() {
        return player;
    }
}
