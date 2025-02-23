package rpg.rpg_base.GUIs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rpg.rpg_base.Crafting.CraftingHandler;
import rpg.rpg_base.Crafting.Recipe;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.GuiHandlers.HeadsHandlers;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.MultiPageInventoryGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static rpg.rpg_base.GuiHandlers.HeadsList.getLeftScrollButton;
import static rpg.rpg_base.GuiHandlers.HeadsList.getRightScrollButton;

public class RecipePageGui extends MultiPageInventoryGUI {
    private GUIManager guiManager;
    private final List<Recipe> recipeList;
    List<Integer> craftingSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30, 34);

    public RecipePageGui(String invName, List<Recipe> recipeList, GUIManager guiManager) {
        super(invName);
        this.recipeList = recipeList;
        this.guiManager = guiManager;
    }
    @Override
    protected Inventory createInventory(String invName) {
        return Bukkit.createInventory(null, 6*9, invName);
    }

    @Override
    public void decorate(Player player){
        recipeButtons();

        int inventorySize = this.getInventory().getSize();

        for (int i = 0; i < inventorySize; i++) {
            if(!craftingSlots.contains(i)) {
                this.addButton(i, filler());
            }
        }

        this.addButton(45, -1, returnButton());
        this.addButton(46, -1, scrollButtons(46));
        this.addButton(52, -1, scrollButtons(52));

        super.decorate(player);
    }

    public InventoryButton filler(){
        return new InventoryButton()
                .creator(player -> new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .consumer(e-> e.setCancelled(true));
    }

    public InventoryButton returnButton(){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack display = new ItemStack(Material.RED_DYE);

                    ItemMeta displayMeta = display.getItemMeta();

                    displayMeta.displayName(Component
                            .text("Return")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.RED));

                    display.setItemMeta(displayMeta);

                    return display;
                })
                .consumer(event -> {
                    guiManager.openGui(new RecipeGui("Recipes", guiManager), (Player) event.getWhoClicked());
                    event.setCancelled(true);
                });
    }

    public InventoryButton scrollButtons(int slot) {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack display;
                    switch (slot) {
                        case 46: {
                            display = new ItemStack(HeadsHandlers.getHead(getLeftScrollButton()));
                            ItemMeta meta = display.getItemMeta();
                            meta.displayName(Component
                                    .text("Previous")
                                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                            display.setItemMeta(meta);
                            break;
                        }
                        case 52: {
                            display = new ItemStack(HeadsHandlers.getHead(getRightScrollButton()));
                            ItemMeta meta = display.getItemMeta();
                            meta.displayName(Component
                                    .text("Next")
                                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                            display.setItemMeta(meta);
                            break;
                        }
                        default: {
                            display = new ItemStack(Material.DIRT);
                        }
                    }
                    return display;
                })
                .consumer(e -> {
                    if (slot == 46) {
                        if (pageMap.get(currentPage - 1) != null) {
                            currentPage--;
                        }
                    }
                    if (slot == 52) {
                        if (pageMap.get(currentPage + 1) != null) {
                            currentPage++;
                        }
                    }
                    decorate((Player) e.getWhoClicked());
                    e.setCancelled(true);
                });
    }

    public void recipeButtons() {
        int currentPage = 1;
        for (Recipe recipe : recipeList) {
            for (int i = 0; i < recipe.recipe.length; i++) {
                int finalI = i;
                this.addButton(craftingSlots.get(i), currentPage, new InventoryButton()
                        .creator(player ->
                            recipe.recipe()[finalI])
                        .consumer(event -> event.setCancelled(true)));
            }
            this.addButton(34, currentPage, new InventoryButton()
                    .creator(player -> recipe.result())
                    .consumer(event -> event.setCancelled(true)));
            currentPage++;
        }
    }

}
