package rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.MobManager;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.RpgMob;
import rpg.rpg_base.RPG_Base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class SpawningNode {
    private final String nodeId;
    private final Map<String, Float> possibleSpawn = new HashMap<>();

    private RpgMob currentlySpawnedMob = null;
    private Location location;

    private final Random random = new Random();

    public SpawningNode(String nodeId) {
        this.nodeId = nodeId;
    }

    public synchronized void spawnMob(){
        Bukkit.getScheduler().runTaskLater(RPG_Base.getInstance(), () -> {
            if(currentlySpawnedMob != null){
                RPG_Base.getInstance().getComponentLogger().error(Component.text("Something tried to activate spawn on an occupied Spawn Node: " + location.toString() + "!!!"), NamedTextColor.RED);
                return;
            }

            if(possibleSpawn.isEmpty()){
                RPG_Base.getInstance().getComponentLogger().error(Component.text("No possible spawns for node: " + location.toString() + "!!! I dont know how you did this but congrats on finding this rare error message", NamedTextColor.RED));
                return;
            }

            float totalWeight = 0f;

            for(Float entry : possibleSpawn.values()){
                totalWeight += entry;
            }

            float roll = random.nextFloat() * totalWeight;

            float cumulative = 0f;
            for(Entry<String, Float> entry : possibleSpawn.entrySet()){
                cumulative += entry.getValue();
                if(roll <= cumulative){
                    currentlySpawnedMob = MobManager.spawnMob(entry.getKey(), location.clone(), nodeId);
                    return;
                }
            }
        }, 20*5);
    }

    public Map<String, Float> getPossibleSpawns() {
        return possibleSpawn;
    }

    public void setPossibleSpawns(List<String> mobSpawns) {
        possibleSpawn.clear();
        for(String spawn : mobSpawns){
            String[] splitSpawn = spawn.split(",");
            possibleSpawn.put(splitSpawn[0],  Float.parseFloat(splitSpawn[1]));
        }
    }

    public void setPossibleSpawn(String mobId, Float chance){
        possibleSpawn.put(mobId, chance);
    }

    public void removePossibleSpawn(String mobId){
        possibleSpawn.remove(mobId);
    }

    public void removeCurrentSpawnedMob(){
        currentlySpawnedMob = null;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getNodeId() {
        return nodeId;
    }
}
