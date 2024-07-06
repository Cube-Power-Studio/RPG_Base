package rpg.rpg_base.CustomMobs;
import rpg.rpg_base.StatManager.HealthManager;

import java.util.HashMap;
import java.util.UUID;

public class MobLevelManager {
    public static HashMap<UUID, Integer> entityLevels = new HashMap<>();

    public static int getEntityLevel(UUID uuid){
        return entityLevels.getOrDefault(uuid,0);
    }
    public static void setEntityLevel(UUID uuid, int levelmin, int  levelmax) {
        int level = (int) (Math.random() * ((levelmax - levelmin) + 1) + levelmin);
        entityLevels.put(uuid, level);
        HealthManager.setEntityMaxHealth(uuid, entityLevels.getOrDefault(uuid, 0)*10 + 100);
        HealthManager.setEntityHealth(uuid, HealthManager.getEntityMaxHealth(uuid));
    }
}
