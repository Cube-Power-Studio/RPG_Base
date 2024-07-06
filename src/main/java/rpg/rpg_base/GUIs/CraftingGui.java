package rpg.rpg_base.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.GuiHandlers.CraftingHandler;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.InventoryGUI;
import rpg.rpg_base.RPG_Base;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CraftingGui extends InventoryGUI {
    private final RPG_Base plugin;
    CraftingHandler craftingHandler = new CraftingHandler();
    List<Integer> craftingSlots; // List of allowed slots

    public CraftingGui(RPG_Base plugin) {
        this.plugin = plugin;
        this.craftingHandler = new CraftingHandler();
        this.craftingSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30, 34);
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 6 * 9, "Crafting");
    }

    @Override
    public void decorate(Player player) {
        int inventorySize = this.getInventory().getSize();
        Material material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
        for (int i = 0; i < inventorySize; i++) {
            if(!craftingSlots.contains(i)) {
                this.addButton(i, this.createBackGround(material));
            }
        }

        super.decorate(player);
    }

    private InventoryButton createBackGround(Material material) {
        return new InventoryButton(){}
                .creator(player -> new ItemStack(material))
                .consumer(event -> {
                    // Debug output to verify slot numbers
                    InventoryView view = event.getView();
                    Inventory clickedInventory = event.getClickedInventory();

                    // Check if the click was in the chest's top inventory
                    if (clickedInventory == view.getTopInventory()) {
                        if (event.getSlot() < 54 && !craftingSlots.contains(event.getSlot())) {
                            event.setCancelled(true);
                        }
                    }
                });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);

        // Ensure the click event is from a valid player
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        System.out.println("Player clicked inventory. Action: " + event.getAction() + ", ClickType: " + event.getClick());
        System.out.println("Slot clicked: " + event.getSlot() + ", Clicked Item: " + event.getCurrentItem() + ", Cursor Item: " + event.getCursor());

        if(event.isShiftClick() || event.getClick().equals(ClickType.DOUBLE_CLICK)){
            event.setCancelled(true);
            return;
        }

        // Only handle clicks if the inventory matches
        if (event.getInventory().equals(this.getInventory())) {
            int slot = event.getSlot();
            ItemStack clickedItem = event.getCursor();

            // Prevent placing items in the output slot
            if (event.getAction().name().contains("PLACE") && slot == 34) {
                event.setCancelled(true);
                player.sendMessage("You cannot place items in the output slot.");
                return;
            }

            // Handle item placement or removal
            if (event.getAction().name().contains("PLACE")) {
                System.out.println("Handling item placement.");
                handleItemPlacement(player, slot, clickedItem);
            } else if (event.getAction().name().contains("PICKUP")) {
                System.out.println("Handling item removal.");
                handleItemRemoval(player, slot);
            }
        }
    }




    private void handleItemPlacement(Player player, int slot, ItemStack item) {
        System.out.println("handleItemPlacement called with player: " + player.getName() + ", slot: " + slot + ", item: " + item);
        if (item == null || item.getType() == Material.AIR) {
            System.out.println("Item is null or air, skipping placement.");
            return;
        }

        // Handle item placement in specific crafting slots
        if (slot == 10 || slot == 11 || slot == 12 ||
                slot == 19 || slot == 20 || slot == 21 ||
                slot == 28 || slot == 29 || slot == 30) {
            System.out.println("Item placed in slot " + slot);

            ItemStack[] currentItems = craftingHandler.getItems(player);
            if (currentItems == null) {
                System.out.println("Current items are null, initializing new ItemStack array.");
                currentItems = new ItemStack[9];
                craftingHandler.setItems(player, currentItems);
            } else {
                System.out.println("Current items array length: " + currentItems.length);
            }

            int craftSlot = switch (slot) {
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
            if (craftSlot != -1) {
                System.out.println("Storing item in craftSlot " + craftSlot);
                currentItems[craftSlot] = item;  // Store the item in the correct position
                craftingHandler.setItems(player, currentItems);
                craftingHandler.checkRecipeMatch(player);

                // Update the output slot if the recipe matches
                ItemStack craftedItem = craftingHandler.getCraftedItem();
                System.out.println("Crafted item: " + craftedItem);
                this.getInventory().setItem(34, craftedItem);
                this.UpdateGui(player);
            } else {
                System.out.println("Invalid craftSlot calculated from slot " + slot);
            }
            System.out.println(Arrays.toString(craftingHandler.getItems(player)));
        } else {
            System.out.println("Slot " + slot + " is not a valid crafting slot.");
        }
    }



    private void handleItemRemoval(Player player, int slot) {
        System.out.println("handleItemRemoval called with player: " + player.getName() + ", slot: " + slot);

        if (slot == 10 || slot == 11 || slot == 12 ||
                slot == 19 || slot == 20 || slot == 21 ||
                slot == 28 || slot == 29 || slot == 30) {

            System.out.println("Item removed from slot " + slot);

            // Set the slot to air and update the items array
            ItemStack[] currentItems = craftingHandler.getItems(player);
            System.out.println("niggas" + Arrays.toString(currentItems));
            if (currentItems == null) {
                System.out.println("Current items are null, initializing new ItemStack array.");
                currentItems = new ItemStack[9];
            }

            int craftSlot = switch (slot) {
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

            if (craftSlot != -1) {
                System.out.println("Clearing craftSlot " + craftSlot);
                currentItems[craftSlot] = new ItemStack(Material.AIR);  // Clear the slot in the array
                craftingHandler.setItems(player, currentItems);
                craftingHandler.checkRecipeMatch(player);

                // Update the output slot if the recipe matches
                ItemStack craftedItem = craftingHandler.getCraftedItem();
                System.out.println("Crafted item after removal: " + craftedItem);
                this.getInventory().setItem(34, craftedItem);
                this.UpdateGui(player);
            } else {
                System.out.println("Invalid craftSlot calculated from slot " + slot);
            }
        } else if (slot == 34) {
            System.out.println("Item removed from output slot 34");

            // Decrease the amount of each item used in crafting
            ItemStack[] postCraft = craftingHandler.getItems(player);
            if (postCraft != null) {
                for (int i = 0; i < postCraft.length; i++) {
                    if (postCraft[i] != null) {
                        System.out.println("Decreasing amount of item in postCraft[" + i + "]");
                        postCraft[i].setAmount(postCraft[i].getAmount() - 1);
                        if (postCraft[i].getAmount() <= 0) {
                            postCraft[i] = new ItemStack(Material.AIR);
                        }
                    }
                }
                craftingHandler.setItems(player, postCraft);
            } else {
                System.out.println("postCraft is null");
            }

            // Update the items in the crafting slots in the GUI
            for (int craftingSlot : craftingSlots) {
                ItemStack item = this.getInventory().getItem(craftingSlot);
                if (item != null && item.getType() != Material.AIR) {
                    System.out.println("Decreasing amount of item in craftingSlot " + craftingSlot);
                    item.setAmount(item.getAmount() - 1);
                    if (item.getAmount() <= 0) {
                        this.getInventory().setItem(craftingSlot, new ItemStack(Material.AIR));
                    }
                }
            }

            // Optionally clear the output slot or update it with the new crafted item
            ItemStack craftedItem = craftingHandler.getCraftedItem();
            System.out.println("Updated crafted item: " + craftedItem);
            this.getInventory().setItem(34, craftedItem);
            this.UpdateGui(player);
        } else {
            System.out.println("Slot " + slot + " is not a valid crafting or output slot.");
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        System.out.println(Arrays.toString(craftingHandler.getItems((Player) event.getPlayer())));
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        // Debug logging
        plugin.getLogger().info("Inventory close event triggered for player: " + player.getName());

        // Retrieve items and ensure it's not null
        ItemStack[] items = craftingHandler.getItems(player);
        if (items == null) {
            plugin.getLogger().warning("Crafting handler returned null items for player: " + player.getName());
            return;
        }

        // Iterate through items and add them to the player's inventory if not null
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                plugin.getLogger().info("Returning item: " + item.getType() + " to player: " + player.getName());
                player.getInventory().addItem(item.clone());
            }
        }

        // Optionally clear the crafting slots in the handler after returning the items
        craftingHandler.setItems(player, new ItemStack[9]);
    }


    public void UpdateGui(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            decorate(player);
        }, 5L);
    }
}