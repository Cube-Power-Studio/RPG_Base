package rpg.rpg_base.GUIs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
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
    private static final Map<CPlayer, Long> cooldowns = new HashMap<>();

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

        this.addButton(11, createUpdateButtons(increase, 11));
        this.addButton(12, createUpdateButtons(increase, 12));
        this.addButton(13, createUpdateButtons(increase, 13));
        this.addButton(14, createUpdateButtons(increase, 14));
        this.addButton(15, createUpdateButtons(increase, 15));

        int[] customModelDataValues = {3, 5, 4, 2, 1}; // The CustomModelData values
        int[] slots = {20, 21, 22, 23, 24}; // Corresponding button slots

        for (int i = 0; i < customModelDataValues.length; i++) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setCustomModelData(customModelDataValues[i]);
                item.setItemMeta(meta);
            }

            this.addButton(slots[i], createStatsLevelDisplay(item, slots[i]));
        }

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if(skullMeta!=null){
            skullMeta.setOwningPlayer(player);
            playerHead.setItemMeta(skullMeta);
        }

        this.addButton(45, createSkillPointCounter(playerHead));

        super.decorate(player);
    }

    public InventoryButton filler(){
        return new InventoryButton()
                .creator(player -> new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .consumer(e-> e.setCancelled(true));
    }

    private InventoryButton createUpdateButtons(ItemStack itemStack, int slot){
        return new InventoryButton()
                .creator(player ->{
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    if (slot == 11) {
                        itemMeta.displayName(Component
                                .text("Increase endurance")
                                .color(NamedTextColor.WHITE)
                                .decorate(TextDecoration.BOLD));
                        itemMeta.lore(null);
                        itemStack.setItemMeta(itemMeta);
                    } else if (slot == 12) {
                        itemMeta.displayName(Component
                                .text("Increase strength")
                                .color(NamedTextColor.WHITE)
                                .decorate(TextDecoration.BOLD));
                        itemMeta.lore(null);
                        itemStack.setItemMeta(itemMeta);
                    }
                    return itemStack;
                } )
                .consumer(event ->{
                    event.setCancelled(true);

                    CPlayer player = CPlayer.getPlayerByUUID(event.getWhoClicked().getUniqueId());
                    long currentTime = System.currentTimeMillis();

                    if (isOnCooldown(player)) {
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
                    }, 5);
                    
                    UpdateGui((Player) event.getWhoClicked());
                });
    }
    private InventoryButton createStatsLevelDisplay(ItemStack itemStack, int slot){
        return new InventoryButton()
                .creator(player ->{
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());
                    PlayerSkills playerSkills = cPlayer.playerSkills;

                    if (slot == 20) {
                        Component displayName = Component.text("Endurance Level: " + playerSkills.enduranceLvl)
                                .color(NamedTextColor.WHITE)
                                .decorate(TextDecoration.BOLD);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component
                                .text("~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD)
                                .append(Component
                                        .text(" Health bonus: " + (playerSkills.enduranceLvl * playerSkills.enduranceHealthBoost * 100) + "%")
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component
                                .text("~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD)
                                .append(Component
                                        .text(" Health bonus: " + ((playerSkills.enduranceLvl + 1) * playerSkills.enduranceHealthBoost * 100) + "%")
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 21) {

                        Component displayName = Component.text("Strength level: " + playerSkills.strengthLvl)
                                .color(NamedTextColor.WHITE)
                                .decorate(TextDecoration.BOLD);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component
                                .text("~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD)
                                .append(Component
                                        .text(" Damage Added: " + (playerSkills.strengthLvl * playerSkills.strengthDmgBoost * 100))
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component
                                .text("~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD)
                                .append(Component
                                        .text(" Damage added: " + ((playerSkills.strengthLvl + 1) * playerSkills.strengthDmgBoost * 100))
                                        .color(NamedTextColor.RED)
                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 22) {

                        Component displayName = Component.text("Intelligence level: " + playerSkills.inteligenceLvl)
                                .color(NamedTextColor.WHITE)
                                .decorate(TextDecoration.BOLD);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

//                        lore.add(Component
//                                .text("~")
//                                .color(NamedTextColor.GRAY)
//                                .decorate(TextDecoration.BOLD)
//                                .append(Component
//                                        .text(" Damage Added: " + (playerSkills.strengthLvl * playerSkills.strengthDmgBoost * 100))
//                                        .color(NamedTextColor.RED)
//                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

//                        lore.add(Component
//                                .text("~")
//                                .color(NamedTextColor.GRAY)
//                                .decorate(TextDecoration.BOLD)
//                                .append(Component
//                                        .text(" Damage added: " + ((playerSkills.strengthLvl + 1) * playerSkills.strengthDmgBoost * 100))
//                                        .color(NamedTextColor.RED)
//                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 23) {

                        Component displayName = Component.text("Dexterity level: " + playerSkills.dexterityLvl)
                                .color(NamedTextColor.WHITE)
                                .decorate(TextDecoration.BOLD);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

//                        lore.add(Component
//                                .text("~")
//                                .color(NamedTextColor.GRAY)
//                                .decorate(TextDecoration.BOLD)
//                                .append(Component
//                                        .text(" Damage Added: " + (playerSkills.strengthLvl * playerSkills.strengthDmgBoost * 100))
//                                        .color(NamedTextColor.RED)
//                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

//                        lore.add(Component
//                                .text("~")
//                                .color(NamedTextColor.GRAY)
//                                .decorate(TextDecoration.BOLD)
//                                .append(Component
//                                        .text(" Damage added: " + ((playerSkills.strengthLvl + 1) * playerSkills.strengthDmgBoost * 100))
//                                        .color(NamedTextColor.RED)
//                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 24) {

                        Component displayName = Component.text("Agility level: " + playerSkills.agilityLvl)
                                .color(NamedTextColor.WHITE)
                                .decorate(TextDecoration.BOLD);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

//                        lore.add(Component
//                                .text("~")
//                                .color(NamedTextColor.GRAY)
//                                .decorate(TextDecoration.BOLD)
//                                .append(Component
//                                        .text(" Damage Added: " + (playerSkills.strengthLvl * playerSkills.strengthDmgBoost * 100))
//                                        .color(NamedTextColor.RED)
//                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

//                        lore.add(Component
//                                .text("~")
//                                .color(NamedTextColor.GRAY)
//                                .decorate(TextDecoration.BOLD)
//                                .append(Component
//                                        .text(" Damage added: " + ((playerSkills.strengthLvl + 1) * playerSkills.strengthDmgBoost * 100))
//                                        .color(NamedTextColor.RED)
//                                        .decorate(TextDecoration.BOLD)));

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decorate(TextDecoration.BOLD));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }

                    return itemStack;
                } )
                .consumer(event -> {
                    event.setCancelled(true);
                });
    }
    private InventoryButton createSkillPointCounter(ItemStack itemStack){
        return new InventoryButton()
                .creator(player -> {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());

                    itemMeta.displayName(Component
                            .text(player.getName() + "'s Level: ")
                            .color(NamedTextColor.BLUE)
                            .decorate(TextDecoration.BOLD)
                            .append(Component
                                    .text(cPlayer.level)
                                    .color(NamedTextColor.WHITE)));

                    List<Component> lore = new ArrayList<>();
                    lore.add(Component
                            .text("Skill points: ")
                            .color(NamedTextColor.WHITE)
                            .decorate(TextDecoration.BOLD)
                            .append(Component.text(cPlayer.skillPoints)
                                    .color(NamedTextColor.GREEN)));

                    itemMeta.lore(lore);

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
    private boolean isOnCooldown(CPlayer player) {
        return cooldowns.containsKey(player) && (System.currentTimeMillis() - cooldowns.get(player)) < ((long) 5 * 50);
    }

    private void setCooldown(CPlayer player, long currentTime) {
        cooldowns.put(player, currentTime);
    }

    private void removeCooldown(CPlayer player) {
        cooldowns.remove(player);
    }

}

