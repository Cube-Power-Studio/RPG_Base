package rpg.rpg_base.GUIs;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.PlayerSkills;
import rpg.rpg_base.GuiHandlers.HeadsHandlers;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.InventoryGUI;
import rpg.rpg_base.RPG_Base;

import java.util.*;

import static rpg.rpg_base.GuiHandlers.HeadsList.*;

public class SkillGui extends InventoryGUI {
    private final RPG_Base plugin;

    public SkillGui(RPG_Base plugin) {
        super("Skills");
        this.plugin = plugin;
    }
    private static final Map<CPlayer, Long> cooldowns = new HashMap<CPlayer, Long>();

    @Override
    protected Inventory createInventory(String arg) {
        return Bukkit.createInventory(null, 6*9, "Skills");
    }
    @Override
    public void decorate(Player player){
        int inventorySize = this.getInventory().getSize();
        for (int i = 0; i < inventorySize; i++){
            this.addButton(i, filler());

        }
        ItemStack increase = HeadsHandlers.getHead(getUpgradeButton());

        this.addButton(11, createUpdateButtons(increase, 11, 5));
        this.addButton(12, createUpdateButtons(increase, 12, 5));
        this.addButton(13, createUpdateButtons(increase, 13, 5));
        this.addButton(14, createUpdateButtons(increase, 14, 5));
        this.addButton(15, createUpdateButtons(increase, 15, 5));

        Material levelDisplay = Material.ACACIA_LOG;
        this.addButton(20, createStatsLevelDisplay(levelDisplay, 20));
        this.addButton(21, createStatsLevelDisplay(levelDisplay, 21));
        Material counter = Material.CHEST;
        this.addButton(45, createSkillPointCounter(counter));

        super.decorate(player);
    }

    public InventoryButton filler(){
        return new InventoryButton()
                .creator(player -> new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .consumer(e-> e.setCancelled(true));
    }

    private InventoryButton createUpdateButtons(ItemStack itemStack, int slot, long cooldownTicks){
        return new InventoryButton()
                .creator(player ->{
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if (slot == 11) {

                        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Increase endurance");
                        itemMeta.setLore(null);
                        itemStack.setItemMeta(itemMeta);
                    } else if (slot == 12) {
                        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Increase strength");
                        itemMeta.setLore(null);
                        itemStack.setItemMeta(itemMeta);
                    }
                    return itemStack;
                } )
                .consumer(event ->{
                    event.setCancelled(true);

                    CPlayer player = CPlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                    long currentTime = System.currentTimeMillis();

                    if (isOnCooldown(player, cooldownTicks)) {
                        player.getPlayer().sendMessage(ChatColor.RED + "Slow down, you are going too fast!");
                        return;
                    }

                    setCooldown(player, currentTime);

                    if(player.skillPoints > 0) {
                        if (event.getSlot() == 11) {
                            player.playerSkills.enduranceLvl += 1;
                        } else if (event.getSlot() == 12) {
                            player.playerSkills.strengthLvl += 1;
                        }
                        player.spentSkillPoints++;
                        player.updateStats();
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        removeCooldown(player);
                    }, cooldownTicks);
                    
                    UpdateGui((Player) event.getWhoClicked());
                });
    }
    private InventoryButton createStatsLevelDisplay(Material material, int slot){
        return new InventoryButton()
                .creator(player ->{
                    ItemStack itemStack = new ItemStack(material);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());
                    PlayerSkills playerSkills = cPlayer.playerSkills;

                    if (slot == 20) {

                        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Endurance Level: " + playerSkills.enduranceLvl);
                        List<String> lore = new ArrayList<>();

                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Current level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Health bonus: " + (playerSkills.enduranceLvl * playerSkills.enduranceHealthBoost * 100) + "%");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Next level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Health added: " + ((playerSkills.enduranceLvl + 1) * playerSkills.enduranceHealthBoost * 100) + "%");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        itemMeta.setLore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 21) {

                        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Strength level: " + playerSkills.strengthLvl);
                        List<String> lore = new ArrayList<>();

                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Current level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Damage Added: " + (playerSkills.strengthLvl * playerSkills.strengthDmgBoost * 100));
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Next level bonuses:");
                        lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "~" + ChatColor.RESET + ChatColor.RED + "" + ChatColor.BOLD + "Damage added: " + ((playerSkills.strengthLvl + 1) * playerSkills.strengthDmgBoost * 100));
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
                    CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());
                    itemMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + player.getName() + "'s Level: " + ChatColor.WHITE + cPlayer.level);
                    // Initialize lore if null
                    List<String> lore = itemMeta.getLore();
                    if (lore == null) {
                        lore = new ArrayList<>();
                    }

                    lore.add(ChatColor.WHITE +""+ ChatColor.BOLD + "Skill points: " + ChatColor.RESET + "" + ChatColor.GREEN + cPlayer.skillPoints);
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
    private boolean isOnCooldown(CPlayer player, long cooldownTicks) {
        return cooldowns.containsKey(player) && (System.currentTimeMillis() - cooldowns.get(player)) < (cooldownTicks * 50);
    }

    private void setCooldown(CPlayer player, long currentTime) {
        cooldowns.put(player, currentTime);
    }

    private void removeCooldown(CPlayer player) {
        cooldowns.remove(player);
    }

}

