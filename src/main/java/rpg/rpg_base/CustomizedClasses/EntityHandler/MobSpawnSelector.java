package rpg.rpg_base.CustomizedClasses.EntityHandler;

import java.util.*;

public class MobSpawnSelector {
    private final Map<String, Float> mobChances = new HashMap<>();
    private final Random random = new Random();

    public void addMobChance(String entity, float chance) {
        mobChances.put(entity, chance);
    }

    public String selectMob() {
        List<String> mobs = new ArrayList<>();
        for (Map.Entry<String, Float> entry : mobChances.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                mobs.add(entry.getKey());
            }
        }

        if (mobs.isEmpty()) {
            return null; // No blocks defined
        }

        Collections.shuffle(mobs);

        return mobs.get(random.nextInt(mobs.size()));
    }
}
