package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import rpg.rpg_base.MoneyHandlingModule.MoneyTypes;


public class GiveMoneyFactory implements PlayerEventFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveMoneyFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(GiveMoney.class);
        MoneyTypes type = MoneyTypes.valueOf(instruction.getPart(1).toString().toUpperCase());

        return new OnlineEventAdapter( new GiveMoney(
                instruction,
                type),
                log, instruction.getPackage()
        );
    }
}
