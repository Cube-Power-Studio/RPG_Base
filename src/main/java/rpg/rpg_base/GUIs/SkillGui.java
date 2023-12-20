package rpg.rpg_base.GUIs;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.InventoryGUI;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.EnduranceManager;
import rpg.rpg_base.StatManager.LevelManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillGui extends InventoryGUI {
    private RPG_Base plugin;

    public SkillGui(RPG_Base plugin) {
        this.plugin = plugin;
    }
    private static final Map<Player, Long> cooldowns = new HashMap<>();

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 6*9, "Skills");
    }
    @Override
    public void decorate(Player player){
        int inventorySize = this.getInventory().getSize();
        for (int i = 0; i < inventorySize; i++){
            Material material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
            this.addButton(i, this.createBackGround(material));

        }
        Material increase = Material.DIAMOND;
        this.addButton(11, createUpdateButtons(increase, 11, 10));
        this.addButton(12, createUpdateButtons(increase, 12, 10));
        this.addButton(13, createUpdateButtons(increase, 13, 10));
        this.addButton(14, createUpdateButtons(increase, 14, 10));
        this.addButton(15, createUpdateButtons(increase, 15, 10));

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
    private InventoryButton createUpdateButtons(Material material, int slot, long cooldownTicks){
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
                    event.setCancelled(true);

                    Player player = (Player) event.getWhoClicked();
                    String playerName = player.getName();
                    long currentTime = System.currentTimeMillis();

                    if (isOnCooldown(player, cooldownTicks)) {
                        player.sendMessage(ChatColor.RED + "Slow down, you are going too fast!");
                        return;
                    }

                    setCooldown(player, currentTime);

                    if(LevelManager.getPlayerCurrentSkillPoints((Player) event.getWhoClicked())!=0) {
                        if (event.getSlot() == 11) {
                            if (EnduranceManager.getEndurance_lvl(player) < EnduranceManager.Endurance_Lvl_Cap) {
                                EnduranceManager.setEndurance_lvl(player,EnduranceManager.getEndurance_lvl(player)+1);
                                LevelManager.setPlayerSpentSkillPoints((Player) event.getWhoClicked(), LevelManager.getPlayerSpentSkillPoints(((Player) event.getWhoClicked())) +1);
                                LevelManager.UpdateLevel(((Player) event.getWhoClicked()).getPlayer());
                            }
                        }
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        removeCooldown(playerName);
                    }, cooldownTicks);
                    
                    UpdateGui((Player) event.getWhoClicked());
                });
    }
    private InventoryButton createStatsLevelDisplay(Material material, int slot){
        return new InventoryButton()
                .creator(player ->{
                    ItemStack itemStack = new ItemStack(material);
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if (slot == 20) {

                        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Endurance Level: " + EnduranceManager.getEndurance_lvl(player));
                        List<String> lore = new ArrayList<>();

                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Current level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Health added: " + EnduranceManager.getEndurance_hp(player));
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Armor added: " + EnduranceManager.getEndurance_armor(player));
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Next level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Health added: " + (EnduranceManager.getEndurance_hp(player) + EnduranceManager.HP_per_lvl));
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Armor added: " + (EnduranceManager.getEndurance_armor(player) + EnduranceManager.Armor_per_lvl));
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
                    itemMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + player.getName() + "'s Level: " + ChatColor.WHITE + LevelManager.getPlayerLevel(player));
                    // Initialize lore if null
                    List<String> lore = itemMeta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }

                    lore.add(ChatColor.WHITE +""+ ChatColor.BOLD + "Skill points: " + ChatColor.RESET + "" + ChatColor.GREEN + LevelManager.getPlayerCurrentSkillPoints(player));
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
    private boolean isOnCooldown(Player player, long cooldownTicks) {
        return cooldowns.containsKey(player) && (System.currentTimeMillis() - cooldowns.get(player)) < (cooldownTicks * 50);
    }

    private void setCooldown(Player player, long currentTime) {
        cooldowns.put(player, currentTime);
    }

    private void removeCooldown(String playerName) {
        cooldowns.remove(playerName);
    }
}

