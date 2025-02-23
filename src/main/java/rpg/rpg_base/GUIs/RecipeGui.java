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
import rpg.rpg_base.PlayerMenu.PlayerMenuGui;
import rpg.rpg_base.RPG_Base;

import java.util.*;
import java.util.stream.Collectors;

import static rpg.rpg_base.GuiHandlers.HeadsList.getLeftScrollButton;
import static rpg.rpg_base.GuiHandlers.HeadsList.getRightScrollButton;

public class RecipeGui extends MultiPageInventoryGUI {
    private GUIManager guiManager;
    public RecipeGui(String invName, GUIManager guiManager) {
        super(invName);
        this.guiManager = guiManager;
    }

    @Override
    protected Inventory createInventory(String invName) {
        return Bukkit.createInventory(null, 6*9, invName);
    }

    @Override
    public void decorate(Player player){
        recipeButtons();

        for(int i = 0; i <= 8; i++) {
            this.addButton(i, -1, filler());
        }

        for(int i = 45; i <= 53; i++) {
            this.addButton(i,-1, filler());
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
                    guiManager.openGui(new PlayerMenuGui(guiManager), (Player) event.getWhoClicked());
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

    public void recipeButtons(){
        final int[] currentSlot = {9};
        final int[] currentPage = {1};

        List<Recipe> preGroupRecipes = CraftingHandler.craftingList;

        Map<String, List<Recipe>> groupedMap = new HashMap<>();

        for (Recipe recipe : preGroupRecipes) {
            // Use a unique key for grouping based on the result's material and display name
            String key = recipe.result().getType() +
                    (recipe.result().getItemMeta().hasDisplayName() ?
                            recipe.result().getItemMeta().getDisplayName() : "");

            List<Recipe> tempList = groupedMap.getOrDefault(key, new ArrayList<>());
            tempList.add(recipe);
            groupedMap.put(key, tempList);
        }

        groupedMap.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<Recipe>> entry) ->
                                entry.getValue().getFirst().category()) // Sort by the category of the first recipe
                        .thenComparing(entry -> entry.getValue().getFirst().name())) // Then sort by the name
                .forEach(entry -> {
            Recipe firstRecipe = entry.getValue().getFirst(); // Get the first recipe for the display

            this.addButton(currentSlot[0], currentPage[0], new InventoryButton()
                    .creator(player -> {
                        ItemStack display = firstRecipe.result().clone(); // Use the first recipe's result as the display

                        display.setAmount(1);

                        ItemMeta displayMeta = display.getItemMeta();

                        display.setItemMeta(displayMeta);

                        return display;
                    })
                    .consumer(event -> {
                        guiManager.openGui(new RecipePageGui(firstRecipe.name(), entry.getValue(), guiManager), (Player) event.getWhoClicked());
                        event.setCancelled(true);
                    }));

            currentSlot[0]++;
            if (currentSlot[0] == 45) {
                currentSlot[0] = 9;
                currentPage[0]++;
            }
        });

    }
}
