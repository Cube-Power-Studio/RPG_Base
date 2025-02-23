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


public class GiveXPFactory implements EventFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveXPFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }


    @Override
    public Event parseEvent(Instruction instruction) throws InstructionParseException {
        final BetonQuestLogger log = loggerFactory.create(GiveXP.class);

        VariableNumber amount = instruction.getVarNum(instruction.getOptional("amount", "1"));

        return new OnlineEventAdapter( new GiveXP(
                amount),
                log, instruction.getPackage()
        );
    }
}
