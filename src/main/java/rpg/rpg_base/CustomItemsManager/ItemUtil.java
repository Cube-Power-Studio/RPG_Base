package rpg.rpg_base.CustomItemsManager;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ItemUtil {

    public ItemUtil() {
    }
    public static String getAttributeValue(ItemStack item, String label) {
        String itemAttrib = ItemHandlers.getCustomItemsAttrib(item);
        String splitter = ItemHandlers.splitter;
        if (itemAttrib != null) {
            String[] attributesArray = itemAttrib.split(splitter);
            if(Arrays.asList(attributesArray).contains(label)) {
                for (int i = 0; i < attributesArray.length; i += 2) {
                    if (attributesArray[i].equals(label)) {
                        return attributesArray[i + 1];
                    }
                }
            }else{
                return "0";
            }
        }
        return "0";
    }
}
