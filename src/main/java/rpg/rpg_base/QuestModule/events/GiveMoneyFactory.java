package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import rpg.rpg_base.MoneyHandlingModule.MoneyTypes;


public class GiveMoneyFactory implements EventFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveMoneyFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }


    @Override
    public Event parseEvent(Instruction instruction) throws InstructionParseException {
        final BetonQuestLogger log = loggerFactory.create(GiveMoney.class);

        MoneyTypes type = instruction.getEnum(MoneyTypes.class);
        VariableNumber amount = instruction.getVarNum(instruction.getOptional("amount", "1")); // Default to one money

        return new OnlineEventAdapter( new GiveMoney(
                amount,
                type),
                log, instruction.getPackage()
        );
    }
}
