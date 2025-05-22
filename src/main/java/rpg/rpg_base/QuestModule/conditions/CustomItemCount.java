package rpg.rpg_base.QuestModule.conditions;


import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CustomItemCount implements NullableCondition {

    private final int count;

    private final ItemStack item;

    public CustomItemCount(int count, ItemStack item){
        this.count = count;
        this.item = item;
    }

    @Override
    public boolean check(@Nullable Profile profile) {
        int totalCount = 0;

        Inventory inv = profile.getPlayer().getPlayer().getInventory();

        for(int i = 0; i < inv.getSize(); i++) {
            if(inv.getItem(i) == null) continue;

            if(inv.getItem(i).isSimilar(item) ){
                totalCount += inv.getItem(i).getAmount();
            }
        }
        return totalCount >= count;
    }
}
