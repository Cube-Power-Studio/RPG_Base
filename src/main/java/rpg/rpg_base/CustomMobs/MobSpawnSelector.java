package rpg.rpg_base.CustomMobs;

import java.util.*;

public class MobSpawnSelector {
    private final Map<String, Float> mobChances = new HashMap<>();
    private final Random random = new Random();

    public void addMobChance(String name, float chance) {
        mobChances.put(name, chance);
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
