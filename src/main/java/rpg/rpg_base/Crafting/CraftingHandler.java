package rpg.rpg_base.Crafting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingHandler {
    public static final Map<Player, ItemStack[]> craftingItems = new HashMap<>();
    public static final List<Recipe> craftingList = new ArrayList<>();
    private ItemStack craftedItem;

    public void checkRecipeMatch(Player player) {
        ItemStack[] items = craftingItems.get(player);

        for (Recipe entry : craftingList) {
            ItemStack[] craftingRecipe = entry.recipe;
            if (areItemStacksEqual(craftingRecipe, items)) {
                craftedItem = entry.result;
                return;
            }
        }
        craftedItem = new ItemStack(Material.AIR);
    }

    public ItemStack getCraftedItem() {
        return craftedItem;
    }

    public ItemStack getItem(Player player, int slot) {
        ItemStack[] items = craftingItems.get(player);
        if (items == null || slot >= items.length) {
            return new ItemStack(Material.AIR);
        }
        return items[slot];
    }

    public ItemStack[] getItems(Player player) {
        return craftingItems.get(player);
    }

    public void setItems(Player player, ItemStack[] itemStacks) {
        craftingItems.put(player, itemStacks);
    }

    public static void addRecipe(Recipe recipe) {
        craftingList.add(recipe);
    }

    public static void remRecipe(Recipe recipe) {
        craftingList.remove(recipe);
    }

    private boolean areItemStacksEqual(ItemStack[] array1, ItemStack[] array2) {
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (!areItemsEqual(array1[i], array2[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean areItemsEqual(ItemStack item1, ItemStack item2) {
        if (item1 == null && item2 == null) {
            return true;
        }
        if (item1 == null || item2 == null) {
            return false;
        }
        if (item1.getType() != item2.getType()) {
            return false;
        }
        if (item1.hasItemMeta() || item2.hasItemMeta()) {
            if (!item1.getItemMeta().equals(item2.getItemMeta())) {
                return false;
            }
        }
        return true;
    }

}
