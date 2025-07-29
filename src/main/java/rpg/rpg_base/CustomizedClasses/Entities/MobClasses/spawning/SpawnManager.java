package rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import rpg.rpg_base.Data.DataBaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnManager {
    //Possible bottleneck, if possible add multithreading to process nodes in batch of 250 per thread up to 1000 as max!!!
    private static final Map<String, SpawningNode> nodeMap = new HashMap<>();

    public static void setNode(String nodeId, SpawningNode node){
        nodeMap.put(nodeId, node);
        DataBaseManager.addSpawningNodeToDb(node);
    }

    public static void removeNode(SpawningNode node) {
        nodeMap.remove(node.getNodeId());
        DataBaseManager.removeSpawningNodeFromDb(node);
    }

    public static void loadNodes(List<RawSpawningNode> nodeList){
        clearNodes();
        for(RawSpawningNode node : nodeList){
            String nodeId = node.nodeId;
            String rawLocation = node.location;
            String rawMobSpawns = node.mobSpawns;

            Location location;
            if(rawLocation != null && !rawLocation.isEmpty() && !rawLocation.isBlank()){
                String[] rawLocationSplit = rawLocation.split(";");
                location = new Location(Bukkit.getWorld(rawLocationSplit[0]),
                        Integer.parseInt(rawLocationSplit[1]),
                        Integer.parseInt(rawLocationSplit[2]),
                        Integer.parseInt(rawLocationSplit[3]));
            }else{
                location = new Location(Bukkit.getWorlds().getFirst(), 0,0,0);
            }

            List<String> mobSpawns = new ArrayList<>();
            if(rawMobSpawns != null && !rawMobSpawns.isEmpty() && !rawMobSpawns.isBlank()){
                String[] placeholderMobSpawns = rawMobSpawns.split(";");

                for(String mobSpawn : placeholderMobSpawns){
                    if(mobSpawn.contains(",")){
                        mobSpawns.add(mobSpawn);
                    }
                }
            }

            SpawningNode spawningNode = new SpawningNode(nodeId);
            spawningNode.setLocation(location);
            spawningNode.setPossibleSpawns(mobSpawns);

            nodeMap.put(nodeId, spawningNode);
        }
    }

    public static void clearNodes(){
        nodeMap.clear();
    }

    public static void spawnMobs(){
        for(SpawningNode node : nodeMap.values()){
            node.spawnMob();
        }
    }

    public static void activateSpawningForNode(String nodeId) {
        nodeMap.get(nodeId).spawnMob();
    }

    public static SpawningNode getSpawningNode(String nodeId) {
        return nodeMap.get(nodeId);
    }

    public static List<SpawningNode> getSpawningNodes() {
        return nodeMap.values().stream().toList();
    }
}
