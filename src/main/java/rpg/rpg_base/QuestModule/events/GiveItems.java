package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GiveItems implements OnlineEvent {
    private final ItemStack item;
    private final VariableNumber amount;

    public GiveItems(ItemStack item, VariableNumber amount) {
        this.item = item;
        this.amount = amount;
    }

    @Override
    public void execute(OnlineProfile profile) {
        try {
            Inventory inv = profile.getPlayer().getInventory();
            item.setAmount(amount.getValue(profile).intValue());
            if(inv.firstEmpty() != -1){
                inv.addItem(item);

            }else{
                profile.getPlayer().getWorld().dropItem(profile.getPlayer().getLocation(), item);
            }


        } catch (QuestRuntimeException e) {
            throw new RuntimeException(e);
        }


    }
}
