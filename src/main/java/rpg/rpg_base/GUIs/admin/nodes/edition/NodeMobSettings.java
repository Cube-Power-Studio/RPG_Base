package rpg.rpg_base.GUIs.admin.nodes.edition;

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
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawningNode;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.MultiPageInventoryGUI;
import rpg.rpg_base.RPG_Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeMobSettings extends MultiPageInventoryGUI {
    SpawningNode spawningNode;
    public NodeMobSettings(String invName, SpawningNode node) {
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

        addButton(47, -1, accept());
        addButton(49, -1, mobBrowser());

        int i = 9;
        int page = 1;
        for(String mob : spawningNode.getPossibleSpawns().keySet()){
            if(i == 54){
                i = 9;
                page++;
            }
            addButton(i, page, mobButton(mob));
            i++;
        }

        super.decorate(player);
    }

    private InventoryButton mobButton(String mobId){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.ZOMBIE_HEAD);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text(mobId));

                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text(spawningNode.getPossibleSpawns().get(mobId) + "%"));
                    display.lore(lore);

                    return display;
                })
                .consumer(e -> {
                    if(e.getClick().equals(ClickType.LEFT)){

                            new AnvilGUI.Builder()
                                    .onClick((slot, stateSnapshot) -> {
                                        if(slot != AnvilGUI.Slot.OUTPUT) {
                                            return Collections.emptyList();
                                        }

                                        String input = stateSnapshot.getText();
                                        Player player = stateSnapshot.getPlayer();

                                        try {
                                            float value = Float.parseFloat(input);
                                            spawningNode.setPossibleSpawn(mobId, value);
                                            RPG_Base.getInstance().guiManager.openGui(new NodeMobSettings("Mob settings", spawningNode), player);
                                            return List.of(AnvilGUI.ResponseAction.close());
                                        } catch (NumberFormatException ex) {
                                            RPG_Base.getInstance().guiManager.openGui(new NodeMobSettings("Mob settings", spawningNode), player);
                                            return List.of(AnvilGUI.ResponseAction.replaceInputText("Not a number"));
                                        }
                                    })
                                    .text("Enter value") // Starting text in input field
                                    .title("Set Spawn Chance") // GUI title
                                    .plugin(RPG_Base.getInstance()) // Your plugin instance
                                    .open((Player) e.getWhoClicked());
                    }

                    if(e.getClick().equals(ClickType.RIGHT)){
                        this.buttonMap.clear();
                        spawningNode.removePossibleSpawn(mobId);
                    }
                    decorate((Player) e.getWhoClicked());
                    e.setCancelled(true);
                });
    }

    private InventoryButton mobBrowser(){
        return new InventoryButton()
                .creator(p -> {
                    ItemStack display = new ItemStack(Material.BOOK);
                    display.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Browse"));
                    return display;
                })
                .consumer(e -> {
                    RPG_Base.getInstance().guiManager.openGui(new NodeMobBrowser("Mob Browser", spawningNode), (Player) e.getWhoClicked());
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
