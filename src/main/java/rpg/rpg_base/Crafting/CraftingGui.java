package rpg.rpg_base.Crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.InventoryGUI;
import rpg.rpg_base.RPG_Base;

import java.util.*;

public class CraftingGui extends InventoryGUI {
    private final RPG_Base plugin;
    CraftingHandler craftingHandler = new CraftingHandler();
    List<Integer> craftingSlots; // List of allowed slots
    public static ItemStack[] craftingGrid = new ItemStack[9];
    BukkitTask task;

    public CraftingGui(RPG_Base plugin) {
        super("Crafting");
        this.plugin = plugin;
        this.craftingHandler = new CraftingHandler();
        this.craftingSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30, 34);
    }

    @Override
    protected Inventory createInventory(String arg) {
        return Bukkit.createInventory(null, 6 * 9, "Crafting");
    }

    @Override
    public void decorate(Player player) {
        int inventorySize = this.getInventory().getSize();

        for (int i = 0; i < inventorySize; i++) {
            if(!craftingSlots.contains(i)) {
                this.addButton(i, this.filler());
            }
        }

        super.decorate(player);
    }

    public InventoryButton filler(){
        return new InventoryButton()
                .creator(player -> new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .consumer(event -> {
                    // Debug output to verify slot numbers
                    InventoryView view = event.getView();
                    Inventory clickedInventory = event.getClickedInventory();

                    // Check if the click was in the chest's top inventory
                    if (clickedInventory == view.getTopInventory()) {
                        if (event.getSlot() < 54 && !craftingSlots.contains(event.getSlot()) && event.getSlot() != 34) {
                            event.setCancelled(true);
                        }
                    }
                });
    }

    @Override
    public void onOpen(InventoryOpenEvent event){
        if(event.getInventory() == this.getInventory()){
           task = new CraftingCheckTask(this.getInventory(), craftingHandler, (Player) event.getPlayer()).runTaskTimer(plugin, 0, 1);
           decorate((Player) event.getPlayer());
        }
    }
    @Override
    public void onDrag(InventoryDragEvent event) {
        super.onDrag(event);

        if (event.getInventory().equals(this.getInventory())) {
            for (int slot : event.getRawSlots()) {
                // Check if the slot is outside of the custom inventory or if it is the output slot
                if (slot == 34 || (!craftingSlots.contains(slot) && event.getInventorySlots().contains(slot))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);

        // Ensure the click event is from a valid player
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.isShiftClick() || event.getClick().equals(ClickType.DOUBLE_CLICK)) {
            event.setCancelled(true);
            return;
        }

        // Only handle clicks if the inventory matches
        if (event.getInventory().equals(this.getInventory())) {
            int slot = event.getSlot();

            // Check if the action is placing or swapping items in slot 34
            if (slot == 34 && (
                    event.getAction() == InventoryAction.PLACE_ALL ||
                            event.getAction() == InventoryAction.PLACE_SOME ||
                            event.getAction() == InventoryAction.PLACE_ONE ||
                            event.getAction() == InventoryAction.SWAP_WITH_CURSOR
            )) {
                ItemStack cursorItem = event.getCursor();
                ItemStack slotItem = event.getCurrentItem();

                // Cancel the event if cursor item is not null and slot is empty
                if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    event.setCancelled(true);
                    return;
                }

                // Ensure there is no item replacement issue
                if (slotItem != null && slotItem.getType() != Material.AIR) {
                    // Check if items are similar and placing them together exceeds max stack size
                    if (cursorItem.isSimilar(slotItem) && cursorItem.getAmount() + slotItem.getAmount() > cursorItem.getMaxStackSize()) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if (slot == 34 && event.getInventory().getItem(34) != null && event.getInventory().getItem(34).getType() != Material.AIR) {
                for (int craftingSlot : craftingSlots) {
                    if (craftingSlot != 34) {
                        if (this.getInventory().getItem(craftingSlot) != null) {
                            ItemStack item = this.getInventory().getItem(craftingSlot);

                            if (item != null && !item.getType().equals(Material.AIR)) {
                                int newAmount = item.getAmount() - 1;

                                if (newAmount > 0) {
                                    item.setAmount(newAmount);
                                } else {
                                    this.getInventory().setItem(craftingSlot, new ItemStack(Material.AIR));
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        task.cancel();

        for(ItemStack item : craftingGrid) {
            if(item != null &&  !item.getType().equals(Material.AIR)) {
                player.getWorld().dropItem(player.getLocation(), item).getItemStack().setAmount(item.getAmount());
                item.setType(Material.AIR);
                item.setAmount(1);
            }
        }

        // Optionally clear the crafting slots in the handler after returning the items
        craftingHandler.setItems(player, new ItemStack[9]);
    }
}