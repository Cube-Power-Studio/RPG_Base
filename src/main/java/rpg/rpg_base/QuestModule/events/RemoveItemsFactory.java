package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RemoveItemsFactory implements PlayerEventFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    public RemoveItemsFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(RemoveItems.class);

        ///ItemStack item = CItem.customItemsByName.getOrDefault(instruction.getPart(1), null).getItem();
        ItemStack item = new ItemStack(Material.DIRT);


        return new OnlineEventAdapter( new RemoveItems(
                item,
                instruction),
                log, instruction.getPackage()
        );
    }
}
