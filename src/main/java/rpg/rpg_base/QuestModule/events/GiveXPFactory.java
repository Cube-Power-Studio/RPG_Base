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


public class GiveXPFactory implements PlayerEventFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveXPFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(GiveXP.class);

        Variable<Number> amount = instruction.get(Argument.NUMBER);

        return new OnlineEventAdapter( new GiveXP(
                amount),
                log, instruction.getPackage()
        );
    }
}
