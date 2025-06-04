package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;

public class GiveItemsFactory implements PlayerEventFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveItemsFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(RemoveItems.class);

        CItem item = CItem.customItemsByName.get(instruction.getPart(1));

        return new OnlineEventAdapter( new GiveItems(
                item.getItem(),
                instruction),
                log, instruction.getPackage()
        );
    }
}
