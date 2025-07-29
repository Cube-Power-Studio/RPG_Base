package rpg.rpg_base.CustomizedClasses.Entities.MobClasses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rpg.rpg_base.CustomizedClasses.Entities.PlayerClasses.PlayerManager;
import rpg.rpg_base.CustomizedClasses.Entities.RpgEntity;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Utils.PathFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RpgMob extends RpgEntity implements Cloneable {
    public static NamespacedKey mobTypeKey = new NamespacedKey(RPG_Base.getInstance(), "mobType");
    public static NamespacedKey nodeId = new NamespacedKey(RPG_Base.getInstance(), "nodeId");
    public static NamespacedKey customMobKey = new NamespacedKey(RPG_Base.getInstance(), "customMob");

    public String mcMobType = "";
    public String mobType = "";
    Mob entity;

    public int minLevel = 0;
    public int maxLevel = 0;

    int hpPerLevel = 0;
    int dmgPerLevel = 0;

    String displayName;
    MobClasses mobClass;

    List<RpgMobDrop> mobDrops = new ArrayList<>();
    int xpDropped = 0;
    int goldDropped = 0;

    Location spawnLocation;

    Location currentStrollLocation;
    List<Location> walkableBlocksInRadius;
    int strollRange = 5;
    int seeRange = 10;
    int trackingRange = 15;
    BukkitRunnable aiTask;

    public RpgMob(LivingEntity entity) {
        this.entity = (Mob) entity;
        entity.setKiller(null);
    }

    public RpgMob(){}

    public static RpgMob load(ConfigurationSection mobConfig){
        RpgMob entity = new RpgMob();

        entity.baseMaxHp = mobConfig.getInt(".health");
        entity.baseDmg = mobConfig.getInt(".damage");
        entity.hpPerLevel = mobConfig.getInt(".hpScalePerLevel");
        entity.dmgPerLevel = mobConfig.getInt(".dmgScalePerLevel");

        entity.minLevel = mobConfig.getInt(".minLvl");
        entity.maxLevel = mobConfig.getInt(".maxLvl");

        entity.xpDropped = mobConfig.getInt(".droppedXp");
        entity.goldDropped = mobConfig.getInt(".droppedGold");

        for(String drop : mobConfig.getStringList(".drops")){
            String[] splitDrop = drop.split(",");

            if(splitDrop.length < 3){
                RPG_Base.getInstance().getComponentLogger().error(Component.text("Incorrect drop format for item in mob: " + mobConfig.getName() + "!!!", NamedTextColor.RED));
                continue;
            }

            CItem item = CItem.getItemFromName(splitDrop[0]);
            if(item == null){
                RPG_Base.getInstance().getComponentLogger().error(Component.text("No such item found: " + splitDrop[0] + "!!!", NamedTextColor.RED));
                continue;
            }

            float chance = Float.parseFloat(splitDrop[1]);

            int minDrop;
            int maxDrop;
            String[] quantities = splitDrop[2].split("-");

            if(quantities.length == 1){
                minDrop = Integer.parseInt(quantities[0]);
                maxDrop = Integer.parseInt(quantities[0]);
            }else{
                minDrop = Integer.parseInt(quantities[0]);
                maxDrop = Integer.parseInt(quantities[1]);
            }



            entity.mobDrops.add(new RpgMobDrop(item, minDrop, maxDrop, chance));
        }

        entity.mobType = mobConfig.getName();
        entity.mcMobType = mobConfig.getString(".type");
        entity.displayName = mobConfig.getString(".name");

        entity.strollRange = mobConfig.contains(".strollRange") ? mobConfig.getInt(".strollRange") : 5;
        entity.trackingRange = mobConfig.contains(".trackRange") ? mobConfig.getInt(".trackRange") : 15;

        return entity;
    }

    @Override
    public void update() {
        dmg = baseDmg + dmgPerLevel * (level - minLevel);
        maxHp = baseMaxHp + hpPerLevel * (level - minLevel);

        entity.setCustomNameVisible(true);
        Component levelComp = Component.text("[" + level + " LvL] - ", NamedTextColor.GOLD);
        Component nameComp = Component.text(displayName, NamedTextColor.WHITE);
        Component healthComp = Component.text(" " + hp + "/" + maxHp + "❤", NamedTextColor.RED);

        Component entityDisplayName = Component.text().append(levelComp).append(nameComp).append(healthComp).build();

        entity.customName(entityDisplayName);
    }

    public void startAi(){
        RPG_Base plugin = RPG_Base.getInstance();
        spawnLocation = spawnLocation.toBlockLocation();
        PathFinder pathFinder = new PathFinder(spawnLocation, 500, true, 2, strollRange, true);
        walkableBlocksInRadius = pathFinder.getWalkableBlocksInRadius();

        final int[] destinationCooldown = {100};
        int destinationCooldownMax = 80;

        entity.setAggressive(false);

        aiTask = new BukkitRunnable() {
            @Override
            public void run() {
                List<? extends Player> sortedPlayersByDistance = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getWorld().equals(entity.getWorld()))
                        .filter(player -> player.getGameMode().equals(GameMode.SURVIVAL))
                        .sorted((p1, p2) -> Double.compare(p1.getLocation().distanceSquared(entity.getLocation()), p2.getLocation().distanceSquared(entity.getLocation())))
                        .toList();

                if (entity.getTarget() == null) {
                    //plugin.getLogger().info("no target");
                    destinationCooldown[0]++;
                    for (Player player : sortedPlayersByDistance) {
                        if (player.getLocation().distanceSquared(entity.getLocation()) <= seeRange * seeRange
                                && player.getLocation().distanceSquared(spawnLocation) <= trackingRange * trackingRange) {
                            entity.setTarget(player);
                            destinationCooldown[0] = 0;
                            break; // Once a player is found, exit the loop
                        }
                    }
                    //plugin.getLogger().info("No target found");
                    if(destinationCooldown[0] >= destinationCooldownMax){
                        //plugin.getLogger().info("choosing next destination");
                        Random random = new Random();

                        if (walkableBlocksInRadius.isEmpty()) {
                            //plugin.getLogger().warning("No walkable blocks found for entity " + entity.getName());
                            killEntity();
                            aiTask.cancel();
                            aiTask = null;
                            return;
                        }

                        currentStrollLocation = walkableBlocksInRadius.get(random.nextInt(walkableBlocksInRadius.size()));

//                        while(!Util.isLocationInRegion(currentStrollLocation, region)){
//                            walkableBlocksInRadius.remove(currentStrollLocation); // THIS LINE MAKES IT SO THAT IF WALKABLE BLOCKS CONTAIN ANY LOCATION THAT IS OUTSIDE THE REGION IT GETS REMOVED, DON'T REMOVE IT
//                            currentStrollLocation = walkableBlocksInRadius.get(random.nextInt(walkableBlocksInRadius.size()));
//                        }
                        //I decided to unlock the location and i will just decrease the wandering location of the mobs themself

                        entity.getPathfinder().moveTo(currentStrollLocation, 0.9);

                        //System.out.println("Next walk location: " + currentStrollLocation);

                        destinationCooldown[0] = 0;
                    }else{
                        if(currentStrollLocation == null){
                            destinationCooldown[0] = 999999;
                        }else{
                            entity.getPathfinder().moveTo(currentStrollLocation, 0.9);
                            destinationCooldown[0] += 1;
                        }
                    }
                } else {
                    if (entity.getTarget().getLocation().distanceSquared(entity.getLocation()) > seeRange * seeRange
                            || ((Player) entity.getTarget()).getGameMode().equals(GameMode.CREATIVE)
                            || entity.getLocation().distance(spawnLocation) > trackingRange) {
                        entity.setTarget(null);
                    }
                }
            }
        };

        aiTask.runTaskTimer(plugin, 0, 10L);
    }

    @Override
    public void dealDamage(int amount, Entity damager) {
        if(hp - (amount - def) <= 0){
            entity.setCustomNameVisible(false);
            hp = 0;
            entity.setHealth(0);

            if(damager instanceof Player player){
                PlayerManager.getPlayer(player.getUniqueId()).addXp(xpDropped);
                MoneyManager.addPlayerGold(player, goldDropped);
            }
        }else{
            hp -= amount - def;
        }
    }

    /**
     * Kills the entity and sets the custom drops (randomized)
     *
     * @param event passes the event into RpgMob class for further processing
     */

    public void killEntity(EntityDeathEvent event){
        if(entity.getKiller() != null){
            event.getDrops().clear();
            event.setDroppedExp(0);

            Random random = new Random();
            for(RpgMobDrop drop : mobDrops){
                float chance = random.nextFloat() * 100;
                float itemChance = drop.getChance();
                if(chance <= itemChance){
                    int minimumDrops = drop.getMinAmount();
                    int maximumDrops = drop.getMaxAmount();

                    int finalDrops = random.nextInt((maximumDrops - minimumDrops) + 1) + minimumDrops;
                    if(finalDrops > 0){
                        for (int i = 0; i < finalDrops; i++){
                            event.getDrops().add(drop.getItem().getItem());
                        }
                    }
                }
            }

            entity.setHealth(0);
            MobManager.removeMob(this);
        }
    }

    public void killEntity(){
        MobManager.removeMob(this);
        entity.remove();
    }


    public Mob getEntity(){
        return this.entity;
    }

    public List<RpgMobDrop> getMobDrops() {
        return mobDrops;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public RpgMob clone() {
        try {
            RpgMob clone = (RpgMob) super.clone();

            if (this.mobDrops != null) {
                clone.mobDrops = new ArrayList<>();
                for (RpgMobDrop drop : this.mobDrops) {
                    clone.mobDrops.add(drop.clone()); // Ensure RpgMobDrop also implements Cloneable
                }
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
