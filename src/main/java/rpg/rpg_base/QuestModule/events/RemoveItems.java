package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RemoveItems implements OnlineEvent {

    private final ItemStack item;
    private final Instruction instruction;

    public RemoveItems(ItemStack item, Instruction instruction) {
        this.item = item;
        this.instruction = instruction;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        int countAmount;
        if(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(2), null) != null){
            countAmount = Integer.parseInt(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(2), null));
        }else{
            countAmount = Integer.parseInt(instruction.getPart(2));
        }
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
