package rpg.rpg_base.Crafting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class CraftingCheckTask extends BukkitRunnable {
    private final Inventory inventory;
    private final CraftingHandler craftingHandler;
    private final Player player;
    List<Integer> craftingSlots; // List of allowed slots
    ItemStack[] craftingGrid = new ItemStack[9];

    public CraftingCheckTask(Inventory inventory, CraftingHandler craftingHandler, Player player){
        this.inventory = inventory;
        this.craftingHandler = craftingHandler;
        this.player = player;
        this.craftingSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30, 34);
    }

    @Override
    public void run() {
        for(int craftingSlot : craftingSlots) {
            int craftSlot = switch (craftingSlot) {
                case 10 -> 0;
                case 11 -> 1;
                case 12 -> 2;
                case 19 -> 3;
                case 20 -> 4;
                case 21 -> 5;
                case 28 -> 6;
                case 29 -> 7;
                case 30 -> 8;
                default -> -1;
            };
            if(craftSlot != -1) {
                if(inventory.getItem(craftingSlot) != null && inventory.getItem(craftingSlot).getType() != Material.AIR) {
                    craftingGrid[craftSlot] = inventory.getItem(craftingSlot);
                }else{
                    craftingGrid[craftSlot] = new ItemStack(Material.AIR);
                }
            }
        }
        craftingHandler.setItems(player, craftingGrid);
        craftingHandler.checkRecipeMatch(player);
        ItemStack craftedItem = craftingHandler.getCraftedItem();
        inventory.setItem(34, craftedItem);

        CraftingGui.craftingGrid = craftingGrid;
    }
}
