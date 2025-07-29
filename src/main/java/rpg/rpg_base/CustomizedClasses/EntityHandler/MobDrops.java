package rpg.rpg_base.CustomizedClasses.EntityHandler;

import java.util.*;
@Deprecated(forRemoval = true)
public class MobDrops {

    private final Map<Object, Object[]> dropChances = new HashMap<>();
    private final Random random = new Random();

    public void addDropChance(CItem item, double chance, int minDrop, int maxDrop) {

        Object[] container = new Object[3];
        container[0] = chance;
        container[1] = minDrop;
        container[2] = maxDrop;

        dropChances.put(item, container);
    }

    public CItem[] itemDrops() {
        List<CItem> drops = new ArrayList<>();

        for(Object item : dropChances.keySet()){
            double chance = random.nextDouble() * 100;
            double itemChance = (double) dropChances.get(item)[0];
            if(chance <= itemChance){
                int minimumDrops = (int) dropChances.get(item)[1];
                int maximumDrops = (int) dropChances.get(item)[2];

                int finalDrops = random.nextInt((maximumDrops - minimumDrops) + 1) + minimumDrops;
                if(finalDrops > 0){
                    for (int i = 0; i < finalDrops; i++){
                        drops.add((CItem) item);
                    }
                }
            }
        }
        // Convert the drops list to an array and return
        return drops.toArray(new CItem[0]);
    }
}

