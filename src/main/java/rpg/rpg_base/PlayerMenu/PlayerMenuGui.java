package rpg.rpg_base.PlayerMenu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import rpg.rpg_base.Crafting.CraftingGui;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.GUIs.RecipeGui;
import rpg.rpg_base.GUIs.SkillGui;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.InventoryGUI;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.RPG_Base;

import java.util.ArrayList;
import java.util.List;

public class PlayerMenuGui extends InventoryGUI {
    private static GUIManager guiManager;

    public PlayerMenuGui(GUIManager guiManager){
        super("menu");
        PlayerMenuGui.guiManager = guiManager;
    }

    @Override
    protected Inventory createInventory(String invName) {
        return Bukkit.createInventory(null, 6*9, "Menu");
    }

    @Override
    public void decorate(Player player){
        addButton(21, skillsButton());
        addButton(22, playerStatButton());
        addButton(23, recipeButton());
        addButton(24, questsButton());
        addButton(31, craftingButton());

        super.decorate(player);
    }

    private static InventoryButton playerStatButton(){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                    CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());

                    SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                    skullMeta.displayName(Component
                            .text(player.getName() + " [" + cPlayer.level + "LV]")
                            .decoration(TextDecoration.ITALIC, false));

                    List<Component> lore = new ArrayList<>();

                    lore.add(Component
                            .text("✵" + cPlayer.xp + "/" + cPlayer.xpToNextLvl +" Xp")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.BLUE));

                    lore.add(Component
                            .text("\uD83E\uDE99" + MoneyManager.getPlayerGold(player) + " Gold")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.GOLD));

                    skullMeta.lore(lore);

                    playerHead.setItemMeta(skullMeta);

                    return playerHead;
                })
                .consumer(event -> event.setCancelled(true));
    }

    private static InventoryButton skillsButton(){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack display = new ItemStack(Material.POTION);

                    ItemMeta displayMeta = display.getItemMeta();

                    displayMeta.displayName(Component
                            .text("Skills")
                            .decoration(TextDecoration.ITALIC, false));

                    displayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

                    display.setItemMeta(displayMeta);


                    return display;
                })
                .consumer(event -> {
                    guiManager.openGui(new SkillGui(RPG_Base.getInstance()), (Player) event.getWhoClicked());
                    event.setCancelled(true);
                });
    }

    private static InventoryButton recipeButton(){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack display = new ItemStack(Material.BOOK);

                    ItemMeta displayMeta = display.getItemMeta();

                    displayMeta.displayName(Component
                            .text("Recipes")
                            .decoration(TextDecoration.ITALIC, false));

                    List<Component> lore = new ArrayList<>();

                    displayMeta.lore(lore);

                    display.setItemMeta(displayMeta);

                    return display;
                })
                .consumer(event -> {
                    guiManager.openGui(new RecipeGui("Recipes", guiManager), (Player) event.getWhoClicked());
                    event.setCancelled(true);
                });
    }

    private static InventoryButton questsButton(){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack display = new ItemStack(Material.WRITABLE_BOOK);

                    ItemMeta displayMeta = display.getItemMeta();

                    displayMeta.displayName(Component
                            .text("Quests")
                            .decoration(TextDecoration.ITALIC, false));

                    List<Component> lore = new ArrayList<>();

                    displayMeta.lore(lore);

                    display.setItemMeta(displayMeta);

                    return display;
                })
                .consumer(event -> {
                    Bukkit.dispatchCommand(event.getWhoClicked(), "questmenu");
                    event.setCancelled(true);
                });
    }

    //TYMCZASOWE PRZYCISKI

    private static InventoryButton craftingButton(){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack display = new ItemStack(Material.CRAFTING_TABLE);

                    ItemMeta displayMeta = display.getItemMeta();

                    displayMeta.displayName(Component
                            .text("Crafting")
                            .decoration(TextDecoration.ITALIC, false));

                    List<Component> lore = new ArrayList<>();

                    lore.add(Component
                            .text("THIS OPTION IS MOST LIKELY TEMPORARY")
                            .color(TextColor.color(Integer.parseInt("757575" , 16))));

                    displayMeta.lore(lore);

                    display.setItemMeta(displayMeta);

                    return display;
                })
                .consumer(event -> {
                    guiManager.openGui(new CraftingGui(RPG_Base.getInstance()), (Player) event.getWhoClicked());
                    event.setCancelled(true);
                });
    }
}
