package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RemoveItems implements OnlineEvent {

    private final ItemStack item;
    private final Variable<Number> amount;

    public RemoveItems(ItemStack item, Variable<Number> amount) {
        this.item = item;
        this.amount = amount;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        int countAmount = amount.getValue(profile).intValue();
        Inventory inv = profile.getPlayer().getInventory();

        for(int i = 0; i < inv.getSize(); i++){
            if(countAmount > 0) {
                if (inv.getItem(i) != null) {
                    if (inv.getItem(i).isSimilar(item)) {
                        int itemCount = inv.getItem(i).getAmount();
                        if (itemCount > countAmount) {
                            itemCount -= countAmount;
                            countAmount = 0;
                            inv.getItem(i).setAmount(itemCount);
                        }else{
                            countAmount -= itemCount;
                            inv.setItem(i, null);
                        }
                    }
                }
            }
        }
    }
}
