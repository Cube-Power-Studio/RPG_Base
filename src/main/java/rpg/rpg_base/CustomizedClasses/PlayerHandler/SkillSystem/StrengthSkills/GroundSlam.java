package rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.StrengthSkills;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.RegisterSkill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;
import rpg.rpg_base.RPG_Base;

import java.util.List;

@RegisterSkill(name="GrdSlm")
public class GroundSlam extends Skill implements Listener {
    private final double range = 5.0;
    private final double damageMultiplier = 1.5;


    public GroundSlam() {
        displayName = "Ground Slam";
        regName = "GrdSlm";
        type = "str";

        maxLevel = 5;
        slot = 10;
        page = 60;

        levelRequirements.put("str", 10);
        levelRequirements.put("dex", 5);
        levelRequirements.put("end", 3);
        levelRequirements.put("gen", 10);

        updateDescription();

        Bukkit.getPluginManager().registerEvents(this, RPG_Base.getInstance());
    }

//    @EventHandler
//    public void onPlayerLand(EntityDamageByEntityEvent event) {
//        if (!(event.getDamager() instanceof Player player)) return;
//        if (!isAirborne(player)) return;
//
//        CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());
//        if (cPlayer.playerSkills.unlockedSkillMap.get(regName) == null) return;
//        if (isOnCooldown) return;
//
//        performSlam(cPlayer, player.getLocation());
//        setCooldown(5, player);
//    }

    private boolean isAirborne(Player player) {
        Location foot = player.getLocation().clone();
        World world = player.getWorld();

        // If crouching, treat as grounded (even if on edge)
        if (player.isSneaking()) return false;

        // Offsets to cover the four corners + center of player's feet
        double[][] offsets = {
                {0, 0}, // center
                {0.2, 0.2}, {-0.2, 0.2}, {0.2, -0.2}, {-0.2, -0.2}
        };

        for (double[] offset : offsets) {
            Location start = foot.clone().add(offset[0], 0.1, offset[1]);
            RayTraceResult trace = world.rayTraceBlocks(
                    start,
                    new Vector(0, -1, 0),
                    0.3,
                    FluidCollisionMode.NEVER,
                    true
            );

            if (trace != null) return false; // Any hit means grounded
        }

        return true; // No ray hit = airborne
    }

    private void performSlam(CPlayer player, Location loc) {
        for (int i = 0; i < 34; i++) {
            double angle = 2 * Math.PI * i / 34;
            double x = 5 * Math.cos(angle);
            double z = 5 * Math.sin(angle);
            Location particleLoc = loc.clone().add(x, 0.1, z); // slight Y offset for visibility
            loc.getWorld().spawnParticle(Particle.EXPLOSION, particleLoc, 1, 0,0,0, 0.2f);
        }
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

        for (Entity entity : loc.getWorld().getNearbyEntities(loc, range, range, range)) {
            if (!(entity instanceof LivingEntity target)) continue;
            if (entity.equals(player.getPlayer())) continue;

            // Apply custom damage
            int damage = (int) (20 + level * damageMultiplier);
            CEntity.getEntityByUUID(entity.getUniqueId()).dealDamage(damage, player.getPlayer());

            // Apply knockback
            Vector knockback = entity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.1);
            knockback.setY(0.3); // Vertical push
            entity.setVelocity(knockback);
        }
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
                Component.text("Leap into the air and slam down,", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("damaging and knocking back enemies in a radius.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Deals ", NamedTextColor.GRAY).append(Component.text(20 + level * damageMultiplier + " damage", NamedTextColor.RED))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Cooldown ", NamedTextColor.GRAY).append(Component.text(5 + " seconds", NamedTextColor.DARK_GRAY))
                        .decoration(TextDecoration.ITALIC, false)
        );
    }
}
