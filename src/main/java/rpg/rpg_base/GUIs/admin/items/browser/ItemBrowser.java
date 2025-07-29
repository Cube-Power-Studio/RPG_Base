package rpg.rpg_base.GUIs.admin.items.browser;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomizedClasses.items.ItemManager;
import rpg.rpg_base.CustomizedClasses.items.RpgItem;
import rpg.rpg_base.GUIs.admin.items.edition.ItemEditionMenu;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.MultiPageInventoryGUI;
import rpg.rpg_base.RPG_Base;

import java.util.Collections;
import java.util.List;

public class ItemBrowser extends MultiPageInventoryGUI {
    public ItemBrowser(String invName) {
        super(invName);
    }

    @Override
    protected Inventory createInventory(String invName) {
        return Bukkit.createInventory(null, 6*9, invName);
    }

    @Override
    public void decorate(Player player){
        for(int i = 0; i < 9; i++){
            addButton(i, -1, filler());
        }
        for(int i = 45; i < 54; i++){
            addButton(i, -1, filler());
        }

        if(pageMap.get(currentPage - 1) != null) {
            this.addButton(47, prevPage());
        }
        if(pageMap.get(currentPage + 1) != null) {
            this.addButton(51, nextPage());
        }

        addButton(49, createNewItemButton());

        int i = 9;
        int page = 1;

        for(RpgItem rpgItem : ItemManager.getItemRegistry().values()){
            addButton(i, page, itemButton(rpgItem));
            if (i == 53) {
                i = 9;
                page++;
            }
            i++;
        }


        super.decorate(player);
    }

    private InventoryButton itemButton(RpgItem item){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = item.getItem().clone();
                    display.getItemMeta().lore().add(Component.text("Left click to edit", NamedTextColor.GREEN));
                    return display;
                })
                .consumer(e -> {
                    if(e.getClick().equals(ClickType.LEFT)){
                        RPG_Base.getInstance().guiManager.openGui(new ItemEditionMenu("Editor", item), (Player) e.getWhoClicked());
                    }
                    e.setCancelled(true);
                });
    }

    private InventoryButton createNewItemButton(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.WRITABLE_BOOK);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Create new Item", NamedTextColor.GREEN));
                    return display;
                })
                .consumer(e -> {
                    new AnvilGUI.Builder()
                            .onClick((slot, stateSnapshot) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) {
                                    return Collections.emptyList();
                                }

                                String input = stateSnapshot.getText();
                                Player player = stateSnapshot.getPlayer();
                                RPG_Base.getInstance().guiManager.openGui(new ItemEditionMenu("Editor", new ItemStack(Material.DIRT), input), player);
                                return List.of(AnvilGUI.ResponseAction.close());
                            })
                            .text("Enter value") // Starting text in input field
                            .title("Provide item name (unique)") // GUI title
                            .plugin(RPG_Base.getInstance()) // Your plugin instance
                            .open((Player) e.getWhoClicked());
                    e.setCancelled(true);
                });
    }

    private InventoryButton prevPage(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.ARROW);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Previous Page"));
                    return display;
                })
                .consumer(e -> {
                    currentPage --;
                    decorate((Player) e.getWhoClicked());
                });
    }

    private InventoryButton nextPage(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.ARROW);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Next Page"));
                    return display;
                })
                .consumer(e -> {
                    currentPage ++;
                    decorate((Player) e.getWhoClicked());
                });
    }

    private InventoryButton filler(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text(""));
                    return display;
                })
                .consumer(e -> e.setCancelled(true));
    }
}
