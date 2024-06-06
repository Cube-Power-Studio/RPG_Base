package rpg.rpg_base.CustomMining;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlockChanceSelector {
    private final Map<Material, Float> blockChances = new HashMap<>();
    private final Map<ItemStack, Integer> dropChances = new HashMap<>();
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
    public void addDropChance(ItemStack item, float chance) {
        int scaledChance = (int) (chance * 10000); // Scale chance by 10000
        dropChances.put(item, scaledChance);
    }

    public ItemStack[] itemDrops() {
        List<ItemStack> drops = new ArrayList<>();
        Random random = new Random();

        // Fill the drops list with "air" items
        int totalNumberOfDrops = random.nextInt(4) + 2; // Random number of drops between 2 and 5
        for (int i = 0; i < totalNumberOfDrops; i++) {
            drops.add(new ItemStack(Material.AIR));
        }

        // Randomly add other items based on their probabilities
        float totalChance = dropChances.values().stream().reduce(0, Integer::sum) / 10000f; // Total probability
        boolean addedGuaranteed = false; // Flag to track if at least one drop is added
        for (int i = 0; i < totalNumberOfDrops; i++) {
            if (!addedGuaranteed && i == totalNumberOfDrops - 1) {
                Optional<Integer> firstChance = dropChances.values().stream().findFirst();
                if (firstChance.isPresent()) {
                    float guaranteedChance = firstChance.get() / 10000f;
                    if (guaranteedChance > 0) {
                        ItemStack guaranteedItem = getRandomItem(dropChances.keySet(), guaranteedChance, random);
                        drops.set(i, guaranteedItem.clone());
                        addedGuaranteed = true;
                    }
                }
            } else {
                // Otherwise, add drops randomly
                float randomChance = random.nextFloat() * totalChance; // Random chance within total probability
                drops.set(i, getRandomItem(dropChances.keySet(), randomChance, random).clone());
            }
        }

        // Convert the drops list to an array and return
        return drops.toArray(new ItemStack[0]);
    }

    private ItemStack getRandomItem(Set<ItemStack> items, float chance, Random random) {
        float cumulativeChance = 0;
        for (ItemStack item : items) {
            int scaledChance = dropChances.get(item);
            float actualChance = scaledChance / 10000f; // Undo scaling
            cumulativeChance += actualChance;
            if (chance < cumulativeChance) {
                return item;
            }
        }
        return new ItemStack(Material.AIR); // Default to air if no item is selected
    }
}