package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;

public class GiveItemsFactory implements PlayerEventFactory {
    Variable<Number> amount;
    CItem item;

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveItemsFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(RemoveItems.class);

        item = CItem.customItemsByName.get(instruction.getPart(1));
        amount = instruction.get(Argument.NUMBER);

        return new OnlineEventAdapter( new GiveItems(
                item.getItem(),
                amount),
                log, instruction.getPackage()
        );
    }
}
