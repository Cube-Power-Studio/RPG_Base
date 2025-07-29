package rpg.rpg_base.GUIs.player.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class PlayerInventoryButtons implements Listener {
    @EventHandler
    public void inventoryCraftingButtons(InventoryClickEvent event){
        if (event.getSlot() == 80 || event.getSlot() == 81 || event.getSlot() == 82 || event.getSlot() == 83) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryCancelDragOnCraftingButtons(InventoryDragEvent event){
        int[] craftSlots = {80, 81, 82, 83};
        if (event.getRawSlots().contains(craftSlots)) {
            event.setCancelled(true);
        }
    }
}
