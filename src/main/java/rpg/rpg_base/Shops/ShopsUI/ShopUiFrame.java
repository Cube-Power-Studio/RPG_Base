package rpg.rpg_base.Shops.ShopsUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.GuiHandlers.HeadsHandlers;
import rpg.rpg_base.GuiHandlers.InventoryButton;
import rpg.rpg_base.GuiHandlers.MultiPageInventoryGUI;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Shops.ShopsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static rpg.rpg_base.GuiHandlers.HeadsList.getLeftScrollButton;
import static rpg.rpg_base.GuiHandlers.HeadsList.getRightScrollButton;

public class ShopUiFrame extends MultiPageInventoryGUI {

    public List<String> itemList = new ArrayList<>();
    public HashMap<Integer /* PAGE */,Map<Integer /* SLOT */, List<Object>>> shopMapping = new HashMap<>();
    private NamespacedKey itemKey = new NamespacedKey(RPG_Base.getInstance(), "item");

    public ShopUiFrame(String invName) {
        super(invName);
    }


    public void loadShop(){
        for(String itemString : itemList){
            ItemStack item = CItem.customItemsByName.get(itemString.split(",")[0]).clone().getItem();
            int slot = Integer.parseInt(itemString.split(",")[1]);
            int price = Integer.parseInt(itemString.split(",")[2]);
            int page = Integer.parseInt(itemString.split(",")[3]);

            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                List<Component> lore = meta.lore();
                lore.add(Component.text(""));
                lore.add(Component.text("\uD83E\uDE99: " + price).color(NamedTextColor.GOLD));
                meta.lore(lore);
                meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, itemString.split(",")[0]);
                item.setItemMeta(meta);
            }

            List<Object> itemXprice = List.of(item, price);

            Map<Integer, List<Object>> mappedItem = shopMapping.getOrDefault(page, new HashMap<>());
            mappedItem.put(slot, itemXprice);
            shopMapping.put(page, mappedItem);

            this.addButton(slot, page, itemButtons(item));
        }
    }

    @Override
    protected Inventory createInventory(String pageName) {
        return Bukkit.createInventory(null, 6 * 9, pageName);
    }

    @Override
    public void decorate(Player player) {
        for (int i = 0; i <= 8; i++) {
            this.addButton(i, -1, filler());
        }
        for (int i = 45; i <= 53; i++) {
            this.addButton(i, -1, filler());
        }
        this.addButton(46, -1, scrollButtons(46));
        this.addButton(49, -1, display());
        this.addButton(52, -1, scrollButtons(52));

        super.decorate(player);
    }

    public InventoryButton filler(){
        return new InventoryButton()
                .creator(player -> new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .consumer(e-> e.setCancelled(true));
    }

    private InventoryButton display(){
        return new InventoryButton(){
        }
                .creator(player -> {
                    ItemStack display = new ItemStack(Material.CHEST);
                    ItemMeta meta = display.getItemMeta();
                    meta.displayName(Component.text(pageNameMap.get(currentPage)));
                    display.setItemMeta(meta);
                    return display;
                })
                .consumer(e -> e.setCancelled(true));
    }

    private InventoryButton scrollButtons(int slot) {
        return new InventoryButton() {
        }
                .creator(player -> {
                    ItemStack display = new ItemStack(Material.DIRT);
                    if (slot == 46) {
                        if(shopMapping.get(currentPage - 1) != null) {
                            display = new ItemStack(HeadsHandlers.getHead(getLeftScrollButton()));
                            ItemMeta meta = display.getItemMeta();
                            meta.displayName(Component.text("Previous page"));
                            display.setItemMeta(meta);
                        } else{
                            display = new ItemStack(Material.BARRIER);
                            ItemMeta meta = display.getItemMeta();
                            meta.displayName(Component.text(""));
                            display.setItemMeta(meta);
                        }

                        return display;
                    }
                    if (slot == 52) {
                        if(shopMapping.get(currentPage + 1) != null){
                            display = new ItemStack(HeadsHandlers.getHead(getRightScrollButton()));
                            ItemMeta meta = display.getItemMeta();
                            meta.displayName(Component.text("Next page"));
                            display.setItemMeta(meta);
                        }else{
                            display = new ItemStack(Material.BARRIER);
                            ItemMeta meta = display.getItemMeta();
                            meta.displayName(Component.text(""));
                            display.setItemMeta(meta);
                        }

                        return display;
                    }
                    return new ItemStack(display);
                })
                .consumer(e -> {
                    if(slot == 46){
                        if(shopMapping.get(currentPage - 1) != null) {
                            currentPage--;
                        }
                    }
                    if(slot == 52){
                        if(shopMapping.get(currentPage + 1) != null){
                            currentPage++;
                        }
                    }
                    e.setCancelled(true);
                });
    }

    private InventoryButton itemButtons(ItemStack item) {
        return new InventoryButton() {
        }
                .creator(player -> item)
                .consumer(e -> {
                    Player buyer = (Player) e.getWhoClicked();
                    if(buyer.getInventory().firstEmpty() != -1){
                        int slot = e.getSlot();

                        int itemPrice = (int) shopMapping.get(currentPage).get(slot).get(1);

                        if(MoneyManager.getPlayerGold(buyer) >= itemPrice){
                            MoneyManager.remPlayerGold(buyer, itemPrice);

                            ItemStack itemBought = CItem.customItemsByName.get(e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING)).getItem();
                            buyer.getInventory().addItem(itemBought);
                        }
                    }
                    e.setCancelled(true);
                });
    }

    @Override
    public void onClick(InventoryClickEvent event){

        if(event.getClickedInventory() == this.inventory ){
            if(buttonMap.containsKey(event.getSlot())){
                buttonMap.get(event.getSlot()).getEventConsumer().accept(event);
                decorate((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }

        InventoryAction action = event.getAction();

        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
            event.setCancelled(true);
        }

    }

    @Override
    public void onDrag(InventoryDragEvent event){
        int topInventorySize = event.getView().getTopInventory().getSize();

        // Check if any of the dragged slots are in the top inventory
        boolean involvesTopInventory = event.getRawSlots().stream().anyMatch(slot -> slot < topInventorySize);

        if (involvesTopInventory) {
            event.setCancelled(true); // Cancel the drag event
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event){
        ShopsManager.closeShop((Player) event.getPlayer());
    }
}

