package rpg.rpg_base.CustomizedClasses.MiningHandler;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlockChanceSelector {
    private final Map<Material, Float> blockChances = new HashMap<>();
    private final Map<ItemStack, Object[]> dropChances = new HashMap<>();
    private final Random random = new Random();

    public void addBlockChance(Material material, float chance) {
        blockChances.put(material, chance);
    }

    public String selectBlock() {
        List<Material> materials = new ArrayList<>();
        for (Map.Entry<Material, Float> entry : blockChances.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                materials.add(entry.getKey());
            }
        }
        if (materials.isEmpty()) {
            return null; // No blocks defined
        }
        Material selectedMaterial = materials.get(random.nextInt(materials.size()));
        return selectedMaterial.name();
    }

    public void addDropChance(ItemStack item, double chance, int minDrop, int maxDrop) {

        Object[] container = new Object[3];
        container[0] = chance;
        container[1] = minDrop;
        container[2] = maxDrop;

        dropChances.put(item, container);
    }

    public ItemStack[] itemDrops() {
        List<ItemStack> drops = new ArrayList<>();

        for(ItemStack item : dropChances.keySet()){
            double chance = random.nextDouble() * 100;
            double itemChance = (double) dropChances.get(item)[0];
            if(chance <= itemChance){
                int minimumDrops = (int) dropChances.get(item)[1];
                int maximumDrops = (int) dropChances.get(item)[2];
                int finalDrops = random.nextInt((maximumDrops - minimumDrops) + 1) + minimumDrops;
                if(finalDrops > 0){
                    for (int i = 0; i < finalDrops; i++){
                        drops.add(item.clone());
                    }
                }
            }
        }
        // Convert the drops list to an array and return
        return drops.toArray(new ItemStack[0]);
    }
}