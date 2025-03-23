package rpg.rpg_base.QuestModule.objectives;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;
import rpg.rpg_base.RPG_Base;

import java.util.List;

public class CollectCustomItemsObjective extends CountingObjective implements Listener {

    private final List<String> itemToCollect;

    public CollectCustomItemsObjective(Instruction instruction) throws InstructionParseException {
        super(instruction, "items_left");

        this.itemToCollect = instruction.getList(original -> original);

        targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(final EntityPickupItemEvent event){
        if(CItem.customItemsByName.get(event.getItem().getItemStack().getItemMeta().getPersistentDataContainer().get(CItem.customItemConfig, PersistentDataType.STRING)).getItem() != null){
            if(itemToCollect.contains(event.getItem().getItemStack().getItemMeta().getPersistentDataContainer().get(CItem.customItemConfig, PersistentDataType.STRING))){
                final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getEntity());

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

    @Override
    public String getProperty(String s, Profile profile) {
        return "";
    }
}
