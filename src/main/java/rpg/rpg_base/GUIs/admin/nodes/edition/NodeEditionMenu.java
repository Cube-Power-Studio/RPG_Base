package rpg.rpg_base.GUIs.admin.nodes.edition;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawnManager;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawningNode;
import rpg.rpg_base.GUIs.admin.nodes.browser.NodeBrowser;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.MultiPageInventoryGUI;
import rpg.rpg_base.RPG_Base;

public class NodeEditionMenu extends MultiPageInventoryGUI {
    private final SpawningNode spawningNode;
    public NodeEditionMenu(String invName, SpawningNode node) {
        super(invName);
        spawningNode = node;
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

        addButton(49, -1, removeNode());

        addButton(10, 1, openMainMenu());

        addButton(47, -1, accept());
        addButton(51, -1, deny());

        super.decorate(player);
    }

    private InventoryButton openMainMenu(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.RECOVERY_COMPASS);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Mob settings"));
                    return display;
                })
                .consumer(e -> {
                    RPG_Base.getInstance().guiManager.openGui(new NodeMobSettings("Mob settings", spawningNode), (Player) e.getWhoClicked());
                    e.setCancelled(true);
                });
    }

    private InventoryButton accept(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Accept", NamedTextColor.GREEN));
                    return display;
                })
                .consumer(e -> {
                    SpawnManager.setNode(spawningNode.getNodeId(), spawningNode);
                    e.getWhoClicked().closeInventory();
                    e.setCancelled(true);
                });
    }

    private InventoryButton removeNode(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.BARRIER);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Remove Node", NamedTextColor.RED));
                    return display;
                })
                .consumer(e -> {
                    SpawnManager.removeNode(spawningNode);
                    RPG_Base.getInstance().guiManager.openGui(new NodeBrowser("Node Browser"), (Player) e.getWhoClicked());
                    e.setCancelled(true);
                });
    }

    private InventoryButton deny(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Cancel", NamedTextColor.RED));
                    return display;
                })
                .consumer(e -> {
                    e.getWhoClicked().closeInventory();
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
