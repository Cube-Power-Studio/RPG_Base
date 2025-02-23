package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;

public class ShopOpenFactory implements EventFactory {
    String shopName;

    private final BetonQuestLoggerFactory loggerFactory;

    public ShopOpenFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Event parseEvent(Instruction instruction) throws InstructionParseException {
        final BetonQuestLogger log = loggerFactory.create(RemoveItems.class);

        if(instruction.getPart(1) != null){
            shopName = instruction.getPart(1);
            return new OnlineEventAdapter( new ShopOpen(shopName), log, instruction.getPackage());
        }else{
            return null;
        }
    }
}
