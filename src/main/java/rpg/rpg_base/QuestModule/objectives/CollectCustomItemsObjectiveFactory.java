package rpg.rpg_base.QuestModule.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

public class CollectCustomItemsObjectiveFactory implements ObjectiveFactory {
    public CollectCustomItemsObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(Instruction instruction) throws QuestException {
        final String item = instruction.getPart(0);
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new CollectCustomItemsObjective(instruction, targetAmount, item);
    }
}
