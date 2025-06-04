package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GiveItems implements OnlineEvent {
    private final ItemStack item;
    private final Instruction instruction;

    public GiveItems(ItemStack item, Instruction instruction) {
        this.item = item;
        this.instruction = instruction;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        Inventory inv = profile.getPlayer().getInventory();
        int finalAmount;
        if(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(2), profile) != null){
            finalAmount = Integer.parseInt(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(2), profile));
        }else{
            finalAmount = Integer.parseInt(instruction.getPart(2));
        }
        item.setAmount(finalAmount);
        if(inv.firstEmpty() != -1){
            inv.addItem(item);
        }else{
            profile.getPlayer().getWorld().dropItem(profile.getPlayer().getLocation(), item);
        }
    }
}
