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
import rpg.rpg_base.MoneyHandlingModule.MoneyTypes;


public class GiveMoneyFactory implements PlayerEventFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveMoneyFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(GiveMoney.class);

        MoneyTypes type = MoneyTypes.valueOf(instruction.get(Argument.ENUM(MoneyTypes.class)).toString());

        Variable<Number> amount = instruction.get(Argument.NUMBER);

        return new OnlineEventAdapter( new GiveMoney(
                amount,
                type),
                log, instruction.getPackage()
        );
    }
}
