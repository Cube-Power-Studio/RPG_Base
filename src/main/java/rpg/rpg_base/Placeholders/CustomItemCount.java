package rpg.rpg_base.Placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;

public class CustomItemCount extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "itemAmount";
    }

    @Override
    public @NotNull String getAuthor() {
        return "majster2nn";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player.isOnline() && player instanceof Player) {
            return String.valueOf(getCustomItemAmount((Player) player, params));
        }
        return "0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return String.valueOf(getCustomItemAmount(player, params));
    }

    private int getCustomItemAmount(Player player, String itemName) {
        ItemStack itemNeeded = CItem.customItemsByName.get(itemName).getItem();

        if (itemNeeded == null) {
            return 0; // Return 0 if the item is not found
        }

        int count = 0;
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.isSimilar(itemNeeded)) { // Check for null first
                count += item.getAmount();
            }
        }
        return count;
    }
}
