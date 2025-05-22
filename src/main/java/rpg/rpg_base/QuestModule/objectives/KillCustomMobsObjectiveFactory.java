package rpg.rpg_base.QuestModule.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

public class KillCustomMobsObjectiveFactory implements ObjectiveFactory {
    public KillCustomMobsObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(Instruction instruction) throws QuestException {
        final String mob = instruction.getPart(0);
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new KillCustomMobsObjective(instruction, targetAmount, mob);
    }
}
