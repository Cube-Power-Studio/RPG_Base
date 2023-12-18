package rpg.rpg_base.GUIs;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.InventoryGUI;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.StatManager.SkillPointHandler;

import java.util.ArrayList;
import java.util.List;

public class SkillGui extends InventoryGUI {
    private RPG_Base plugin;
    private final EnduranceManager enduranceManager;
    private final GUIManager guiManager;
    public SkillGui(RPG_Base plugin, EnduranceManager enduranceManager, GUIManager guiManager) {
        this.guiManager = guiManager;
        this.plugin = plugin;
        this.enduranceManager = enduranceManager;
    }


    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 6*9, "Skills");
    }
    @Override
    public void decorate(Player player){
        int inventorySize = this.getInventory().getSize();
        for (int i = 0; i < inventorySize; i++){
            Material material = Material.WHITE_STAINED_GLASS_PANE;
            this.addButton(i, this.createBackGround(material));

        }
        Material increase = Material.DIAMOND;
        this.addButton(11, createUpdateButtons(increase, 11));
        this.addButton(12, createUpdateButtons(increase, 12));
        this.addButton(13, createUpdateButtons(increase, 13));
        this.addButton(14, createUpdateButtons(increase, 14));
        this.addButton(15, createUpdateButtons(increase, 15));

        Material leveldisplay = Material.PLAYER_HEAD;
        this.addButton(20, createStatsLevelDisplay(leveldisplay, 20));

        Material counter = Material.CHEST;
        this.addButton(45, createSkillPointCounter(counter));

        super.decorate(player);
    }

    private InventoryButton createBackGround(Material material) {
        return new InventoryButton() {
        }
                .creator(player -> new ItemStack(material))
                .consumer(event -> {
                });
    }
    private InventoryButton createUpdateButtons(Material material, int slot){
        return new InventoryButton()
                .creator(player ->{
                    ItemStack itemStack = new ItemStack(material);
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if (slot == 11) {

                        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Increase endurance");
                        itemStack.setItemMeta(itemMeta);
                    }
                    return itemStack;
                } )
                .consumer(event ->{
                    if(SkillPointHandler.SkillPoints!=0) {
                        if (EnduranceManager.Endurance_Lvl < EnduranceManager.Endurance_Lvl_Cap) {
                            if (event.getSlot() == 11) {
                                EnduranceManager.Endurance_Lvl += 1;
                                SkillPointHandler.SkillPoints-=1;
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }
                    UpdateGui((Player) event.getWhoClicked());
                });
    }
    private InventoryButton createStatsLevelDisplay(Material material, int slot){
        return new InventoryButton()
                .creator(player ->{
                    ItemStack itemStack = new ItemStack(material);
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if (slot == 20) {

                        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Endurance Level: " + EnduranceManager.Endurance_Lvl);
                        List<String> lore = new ArrayList<>();

                        if (lore == null) {
                            lore = new ArrayList<>();
                        }
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Current level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Health added: " + EnduranceManager.Endurance_HP);
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Armor added: " + EnduranceManager.Endurance_ARMOR);
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Next level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Health added: " + (EnduranceManager.Endurance_HP + EnduranceManager.HP_per_lvl));
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Armor added: " + (EnduranceManager.Endurance_ARMOR + EnduranceManager.Armor_per_lvl));
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        itemMeta.setLore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    return itemStack;
                } )
                .consumer(event -> {
                    event.setCancelled(true);
                });
    }
    private InventoryButton createSkillPointCounter(Material material){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack itemStack = new ItemStack(material);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + player.getName() + "'s Level: " + ChatColor.WHITE +SkillPointHandler.level);
                    // Initialize lore if null
                    List<String> lore = itemMeta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }

                    lore.add(ChatColor.WHITE +""+ ChatColor.BOLD + "Skill points: " + ChatColor.RESET + "" + ChatColor.GREEN + SkillPointHandler.SkillPoints);
                    itemMeta.setLore(lore);

                    itemStack.setItemMeta(itemMeta);
                    return itemStack;
                })
                .consumer(event ->{
                    event.setCancelled(true);
                });
    }
    public void UpdateGui(Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            decorate(player);
        }, 5L);
    }

}

