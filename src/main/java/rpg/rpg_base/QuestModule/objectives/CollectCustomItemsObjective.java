package rpg.rpg_base.QuestModule.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.RPG_Base;

public class CollectCustomItemsObjective extends CountingObjective implements Listener {

    private final String itemToCollect;

    public CollectCustomItemsObjective(Instruction instruction, Variable<Number> targetAmount, String item) throws QuestException {
        super(instruction, targetAmount,"items_left");

        this.itemToCollect = item;

    }

    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(final EntityPickupItemEvent event) throws QuestException {
        String itemTag = event.getItem().getItemStack().getItemMeta().getPersistentDataContainer().get(CItem.customItemConfig, PersistentDataType.STRING);
        if(CItem.customItemsByName.get(itemTag) != null){
            if(itemToCollect.equals(itemTag)){
                final OnlineProfile onlineProfile = BetonQuest.getInstance().getProfileProvider().getProfile(event.getEntity().getUniqueId()).getOnlineProfile().get();

                getCountingData(onlineProfile).progress(event.getItem().getItemStack().getAmount());
                completeIfDoneOrNotify(onlineProfile);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, RPG_Base.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
