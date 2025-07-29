package rpg.rpg_base.GUIs.admin.nodes.browser;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawnManager;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawningNode;
import rpg.rpg_base.GUIs.admin.nodes.edition.NodeEditionMenu;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.MultiPageInventoryGUI;
import rpg.rpg_base.RPG_Base;

import java.util.ArrayList;
import java.util.List;

public class NodeBrowser extends MultiPageInventoryGUI {
    public NodeBrowser(String invName) {
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

        int i = 9;
        int page = 1;

        for(SpawningNode node : SpawnManager.getSpawningNodes()){
            addButton(i, page, nodeButton(node));
            if (i == 53) {
                i = 9;
                page++;
            }
            i++;
        }


        super.decorate(player);
    }

    private InventoryButton nodeButton(SpawningNode spawningNode){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.BEACON);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text(spawningNode.getNodeId()));

                    List<Component> lore = new ArrayList<>();

                    display.lore(lore);

                    return display;
                })
                .consumer(e -> {
                    RPG_Base.getInstance().guiManager.openGui(new NodeEditionMenu("Editor", spawningNode), (Player) e.getWhoClicked());
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
