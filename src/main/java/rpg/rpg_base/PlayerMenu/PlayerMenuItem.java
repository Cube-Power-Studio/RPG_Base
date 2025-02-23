package rpg.rpg_base.PlayerMenu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rpg.rpg_base.GuiHandlers.GUIManager;

public class PlayerMenuItem implements Listener {
    public static ItemStack menuItem;
    private GUIManager guiManager;

    public PlayerMenuItem(GUIManager guiManager){
        this.guiManager = guiManager;

        menuItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta itemMeta = menuItem.getItemMeta();
        itemMeta.displayName(Component.text("Menu").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.YELLOW));
        itemMeta.addEnchant(Enchantment.SHARPNESS, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        menuItem.setItemMeta(itemMeta);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        event.getPlayer().getInventory().setItem(8, menuItem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getSlot() == 8){
            guiManager.openGui(new PlayerMenuGui(guiManager), (Player) event.getWhoClicked());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent event){
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                guiManager.openGui(new PlayerMenuGui(guiManager), event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
            guiManager.openGui(new PlayerMenuGui(guiManager), event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event){
        if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
            guiManager.openGui(new PlayerMenuGui(guiManager), event.getPlayer());
            event.setCancelled(true);
        }
    }
}
