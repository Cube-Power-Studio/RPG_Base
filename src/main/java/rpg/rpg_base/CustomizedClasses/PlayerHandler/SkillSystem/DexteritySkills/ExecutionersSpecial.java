package rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.DexteritySkills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.RegisterSkill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;
import rpg.rpg_base.RPG_Base;

import java.util.List;

@RegisterSkill(name="ExSpc")
public class ExecutionersSpecial extends Skill implements Listener {
    public ExecutionersSpecial(){
        maxLevel = 1;
        page = 160;
        slot = 9;

        levelRequirements.put("str", 15);
        levelRequirements.put("dex", 10);
        levelRequirements.put("gen", 15);

        displayName = "Executioner's Special";
        regName = "ExSpc";
        type = "dex";

        updateDescription();

        Bukkit.getPluginManager().registerEvents(this, RPG_Base.getInstance());
    }

    @EventHandler
    public void onMobHit(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player) return;
        if(!(e.getDamager() instanceof Player player)) return;
        if(CEntity.getEntityByUUID(e.getEntity().getUniqueId())==null) return;

        CPlayer damagerPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());
        if (damagerPlayer.playerSkills.unlockedSkillMap.get(regName) == null) return;
        if (isOnCooldown) return;

        CEntity damagedEntity = CEntity.getEntityByUUID(e.getEntity().getUniqueId());

        float damagedEntityHPpercentage = ((float) (damagedEntity.currentHP - damagerPlayer.damage) / damagedEntity.maxHP) * 100f;

        if(damagedEntityHPpercentage <= 10f) damagedEntity.killEntity();

        setCooldown(60, player);
    }

    @Override
    public void activateEffect(CPlayer player) {
        active = true;
    }

    @Override
    public void deactivateEffect(CPlayer player) {
        active = false;
    }

    @Override
    public void updateDescription() {
        description = List.of(
                Component.text("Finish the weakliings.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("!!!Works only on mobs and NPC's!!!", NamedTextColor.DARK_RED)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Executes mobs below 10% HP.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
    }
}
