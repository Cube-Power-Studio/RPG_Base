package rpg.rpg_base.GuiHandlers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CraftingHandler {
    public static final Map<Player, ItemStack[]> craftingItems = new HashMap<>();
    public static final Map<ItemStack[], ItemStack> craftingList = new HashMap<>();
    public ItemStack craftedItem;

    public void checkRecipeMatch(Player player) {
        ItemStack[] items = craftingItems.get(player);

        for (ItemStack[] craftingRecipe : craftingList.keySet()) {
            if (Arrays.equals(craftingRecipe, items)) {
                craftedItem = craftingList.get(craftingRecipe);
            }else{
                craftedItem  = new ItemStack(Material.AIR);
            }
        }
    }
    public ItemStack getCraftedItem(){
        return craftedItem;
    }
    public ItemStack getItem(Player player, int slot) {
        ItemStack[] items = craftingItems.get(player);
        if (items == null || slot >= items.length) {
            return new ItemStack(Material.AIR);
        }
        return items[slot];
    }

    public ItemStack[] getItems(Player player){
        return craftingItems.get(player);
    }
    public void setItems(Player player, ItemStack[] itemStacks){
        craftingItems.put(player, itemStacks);
    }
    public void addRecipe(ItemStack[] recipe, ItemStack item){
        craftingList.put(recipe,item);
    }
    public void remRecipe(ItemStack[] recipe){
        craftingList.remove(recipe);
    }
}
