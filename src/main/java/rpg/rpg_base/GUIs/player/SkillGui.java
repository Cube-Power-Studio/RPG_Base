package rpg.rpg_base.GUIs.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.PlayerSkills;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.Skill;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.SkillSystem.SkillRegistry;
import rpg.rpg_base.GuiHandlers.HeadsHandlers;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.MultiPageInventoryGUI;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Utils.Util;

import java.time.Duration;
import java.util.*;

import static rpg.rpg_base.GuiHandlers.HeadsList.getUpgradeButton;

public class SkillGui extends MultiPageInventoryGUI {
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
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        createMainPage();
        createSkillPages();

        if(skullMeta!=null){
            skullMeta.setOwningPlayer(player);
            playerHead.setItemMeta(skullMeta);
        }

        this.addButton(45, -1 ,createSkillPointCounter(playerHead));

        if(currentPage != 1){
            this.addButton(53, currentPage, toSkillMenu());
        }

        super.decorate(player);
    }

    public InventoryButton filler(){
        return new InventoryButton()
                .creator(player -> new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .consumer(e-> e.setCancelled(true));
    }
    private  InventoryButton toSkillMenu(){
        return new InventoryButton()
                .creator(player -> {
                    ItemStack item = new ItemStack(Material.RED_DYE);
                    ItemMeta meta = item.getItemMeta();

                    meta.displayName(Component
                            .text("Go back")
                            .decoration(TextDecoration.ITALIC, false));

                    item.setItemMeta(meta);

                    return item;
                })
                .consumer(e -> {
                    currentPage = 1;
                    e.setCancelled(true);
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
                            .decoration(TextDecoration.ITALIC, false)
                            .append(Component
                                    .text(cPlayer.level)
                                    .color(NamedTextColor.WHITE)));

                    List<Component> lore = new ArrayList<>();
                    lore.add(Component
                            .text("Skill points: ")
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(cPlayer.skillPoints)
                                    .color(NamedTextColor.GREEN)));

                    lore.add(Component
                            .text("Ability points: ")
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(cPlayer.abilityPoints)
                                    .color(NamedTextColor.BLUE)));

                    itemMeta.lore(lore);

                    itemStack.setItemMeta(itemMeta);
                    return itemStack;
                })
                .consumer(event ->{
                    event.setCancelled(true);
                });
    }

    private void createMainPage(){
        int inventorySize = this.getInventory().getSize();
        for (int i = 0; i < inventorySize; i++){
            this.addButton(i, 1, filler());
        }

        ItemStack increase = HeadsHandlers.getHead(getUpgradeButton());

        this.addButton(11,1, createUpdateButtons(increase, 11));
        this.addButton(12,1, createUpdateButtons(increase, 12));
        this.addButton(13,1, createUpdateButtons(increase, 13));
        this.addButton(14,1, createUpdateButtons(increase, 14));
        this.addButton(15,1, createUpdateButtons(increase, 15));

        List<String> customModelDataValues = new ArrayList<>(Arrays.asList("skillEnd", "skillStr", "skillInt", "skillDex", "skillAgi"));
        int[] slots = {20, 21, 22, 23, 24}; // Corresponding button slots

        for (int i = 0; i < customModelDataValues.size(); i++) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                CustomModelDataComponent component = meta.getCustomModelDataComponent();
                component.setStrings(List.of(customModelDataValues.get(i)));
                meta.setCustomModelDataComponent(component);
                item.setItemMeta(meta);
            }

            this.addButton(slots[i],1, createStatsLevelDisplay(item, slots[i]));
        }
    }

    private String slotName(int slot){
        String str = "";
        switch(slot) {
            case 11: {
                str = "Increase endurance";
                break;
            }
            case 12: {
                str = "Increase strength";
                break;
            }
            case 13: {
                str = "Increase intelligence";
                break;
            }
            case 14: {
                str = "Increase dexterity";
                break;
            }
            case 15: {
                str = "Increase agility";
                break;
            }
        }
        return str;
    }
    private InventoryButton createUpdateButtons(ItemStack itemStack, int slot){
        return new InventoryButton()
                .creator(player ->{
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    itemMeta.displayName(Component
                            .text(slotName(slot))
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false));
                    itemMeta.lore(null);
                    itemStack.setItemMeta(itemMeta);
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
                        switch(event.getSlot()){
                            case 11:
                                player.playerSkills.enduranceLvl += 1;
                                break;
                            case 12:
                                player.playerSkills.strengthLvl += 1;
                                break;
                            case 13:
                                player.playerSkills.intelligenceLvl += 1;
                                break;
                            case 14:
                                player.playerSkills.dexterityLvl += 1;
                                break;
                            case 15:
                                player.playerSkills.agilityLvl += 1;
                                break;
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
                                .decoration(TextDecoration.ITALIC, false);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

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
                                .decoration(TextDecoration.ITALIC, false));

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
                                .decoration(TextDecoration.ITALIC, false));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 21) {

                        Component displayName = Component.text("Strength level: " + playerSkills.strengthLvl)
                                .color(NamedTextColor.WHITE)
                                .decoration(TextDecoration.ITALIC, false);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

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
                                .decoration(TextDecoration.ITALIC, false));

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
                                .decoration(TextDecoration.ITALIC, false));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 22) {

                        Component displayName = Component.text("Intelligence level: " + playerSkills.intelligenceLvl)
                                .color(NamedTextColor.WHITE)
                                .decoration(TextDecoration.ITALIC, false);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));


                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 23) {

                        Component displayName = Component.text("Dexterity level: " + playerSkills.dexterityLvl)
                                .color(NamedTextColor.WHITE)
                                .decoration(TextDecoration.ITALIC, false);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));


                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }
                    if (slot == 24) {

                        Component displayName = Component.text("Agility level: " + playerSkills.agilityLvl)
                                .color(NamedTextColor.WHITE)
                                .decoration(TextDecoration.ITALIC, false);

                        itemMeta.displayName(displayName);

                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~Current level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~Next level bonuses:")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        lore.add(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false));

                        itemMeta.lore(lore);

                        itemStack.setItemMeta(itemMeta);
                    }

                    return itemStack;
                } )
                .consumer(event -> {
                    if (slot == 20) {currentPage = 10;}

                    if (slot == 21) {currentPage = 60;}

                    if (slot == 22) {currentPage = 110;}

                    if (slot == 23) {currentPage = 160;}

                    if (slot == 24) {currentPage = 210;}

                    event.setCancelled(true);
                });
    }

    private void createSkillPages(){
        Map<String, List<Skill>> skillMap = new HashMap<>();
        skillMap.put("str", SkillRegistry.registeredSkills.values().stream().filter(sk -> Objects.equals(sk.type, "str")).toList());
        skillMap.put("end", SkillRegistry.registeredSkills.values().stream().filter(sk -> Objects.equals(sk.type, "end")).toList());
        skillMap.put("dex", SkillRegistry.registeredSkills.values().stream().filter(sk -> Objects.equals(sk.type, "dex")).toList());
        skillMap.put("int", SkillRegistry.registeredSkills.values().stream().filter(sk -> Objects.equals(sk.type, "int")).toList());
        skillMap.put("agi", SkillRegistry.registeredSkills.values().stream().filter(sk -> Objects.equals(sk.type, "agi")).toList());

        for (int i = 0; i <= 8; i++) {
            this.addButton(i, -1, filler());
        }
        for (int i = 46; i <= 53; i++) {
            this.addButton(i, -1, filler());
        }

        for(String key : skillMap.keySet()){
            for(Skill skill : skillMap.get(key)){
                addButton(skill.slot, skill.page, createSkillButtons(skill));
            }
        }
    }

    private InventoryButton createSkillButtons(Skill skill) {
        return new InventoryButton()
                .creator(player -> {
                    CPlayer cPlayer = CPlayer.getPlayerByUUID(player.getUniqueId());
                    ItemStack display;
                    ItemMeta displayMeta;

                    Skill playerSkill = cPlayer.playerSkills.unlockedSkillMap.get(skill.regName);

                    if (playerSkill != null) {
                        display = new ItemStack(Material.ENCHANTED_BOOK);
                        displayMeta = display.getItemMeta();
                        displayMeta.displayName(Component
                                .text(skill.displayName + " " + Util.numberToRoman(playerSkill.level))
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component
                                                .text(" Unlocked")
                                                .decoration(TextDecoration.ITALIC, false)
                                                .color(NamedTextColor.GREEN)
                                        )
                        );

                        List<Component> lore = displayMeta.lore();
                        if (lore == null) {
                            lore = new ArrayList<>();
                        }

                        playerSkill.updateDescription();
                        lore.addAll(playerSkill.description);

                        if(playerSkill.level < playerSkill.maxLevel){

                            lore.add(Component
                                    .text("Upgrade Available")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(NamedTextColor.GREEN));
                        }else{
                            lore.add(Component
                                    .text("Maxed Out")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(NamedTextColor.DARK_GREEN));
                        }

                        displayMeta.lore(lore);

                    } else if (skill.meetsRequirements(cPlayer)) {
                        display = new ItemStack(Material.BOOK);
                        displayMeta = display.getItemMeta();
                        displayMeta.displayName(Component
                                .text(skill.displayName)
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component
                                        .text(" Available")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.GRAY)
                                )
                        );
                    } else {
                        display = new ItemStack(Material.BARRIER);
                        displayMeta = display.getItemMeta();
                        displayMeta.displayName(Component
                                        .text("Locked")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.RED)
                        );
                        List<Component> lore = new ArrayList<>();

                        lore.add(Component.text("Requirements:")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE));

                        lore.add(Component.text("Endurance Level: ")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component.text(skill.levelRequirements.getOrDefault("end", 0))
                                        .color(NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false)));

                        lore.add(Component.text("Strength Level: ")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component.text(skill.levelRequirements.getOrDefault("str", 0))
                                        .color(NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false)));

                        lore.add(Component.text("Intelligence Level: ")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component.text(skill.levelRequirements.getOrDefault("int", 0))
                                        .color(NamedTextColor.LIGHT_PURPLE)
                                        .decoration(TextDecoration.ITALIC, false)));

                        lore.add(Component.text("Dexterity Level: ")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component.text(skill.levelRequirements.getOrDefault("dex", 0))
                                        .color(NamedTextColor.GOLD)
                                        .decoration(TextDecoration.ITALIC, false)));

                        lore.add(Component.text("Agility Level: ")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component.text(skill.levelRequirements.getOrDefault("agi", 0))
                                        .color(NamedTextColor.AQUA)
                                        .decoration(TextDecoration.ITALIC, false)));

                        lore.add(Component.text("Player Level: ")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.WHITE)
                                .append(Component.text(skill.levelRequirements.getOrDefault("gen", 0))
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.BLUE)));

                        displayMeta.lore(lore);
                    }

                    displayMeta.getPersistentDataContainer().set(Skill.skillKey, PersistentDataType.STRING, skill.regName);

                    display.setItemMeta(displayMeta);

                    return display;
                })
                .consumer(e -> {
                    CPlayer player = CPlayer.getPlayerByUUID(e.getWhoClicked().getUniqueId());
                    if(skill.meetsRequirements(player)){
                        if(player.playerSkills.unlockedSkillMap.containsKey(skill.regName)){
                            Skill updatedSkill = player.playerSkills.unlockedSkillMap.get(skill.regName);

                            if (updatedSkill != null && updatedSkill.level < updatedSkill.maxLevel && player.abilityPoints > 0) {
                                updatedSkill.level++;
                                player.spentAbilityPoints++;
                            }
                        }else{
                            if(player.abilityPoints > 0){
                                Skill addedSkill = skill.clone();
                                addedSkill.level = 1;
                                player.playerSkills.unlockedSkillMap.put(addedSkill.regName, addedSkill);
                                player.spentAbilityPoints++;
                            }
                        }
                    }else{
                        player.getPlayer().showTitle(Title
                                .title(Component
                                        .text("You don't meet the requirements!")
                                        .color(NamedTextColor.RED)
                                        .decoration(TextDecoration.BOLD, true)
                                        .decoration(TextDecoration.ITALIC, false),
                                       Component.text(""),
                                       Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1))
                                ));
                        System.out.println(skill.levelRequirements);
                    }
                    player.updateStats();
                    e.setCancelled(true);
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

