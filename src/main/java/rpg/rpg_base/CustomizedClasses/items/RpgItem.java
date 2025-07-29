package rpg.rpg_base.CustomizedClasses.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RpgItem implements Cloneable{
    private ItemStack item;

    public Map<String, String> itemParams = new HashMap<>();
    private final String regName;

    public RpgItem(ItemStack item, String regName) {
        this.item = item;
        this.regName = regName;
    }

    public ItemStack getItem(){
        return item;
    }

    public String getRegName(){
        return regName;
    }

    @Override
    public RpgItem clone() {
        try {
            RpgItem clone = (RpgItem) super.clone();
            clone.item = this.item != null ? this.item.clone() : null; // Deep copy ItemStack
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
