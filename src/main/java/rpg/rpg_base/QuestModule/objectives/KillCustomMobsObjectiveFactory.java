package rpg.rpg_base.QuestModule.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;

public class KillCustomMobsObjectiveFactory implements ObjectiveFactory {
    public KillCustomMobsObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(Instruction instruction) throws QuestException {
        final String mob = instruction.next();

        return new KillCustomMobsObjective(
                instruction,
                instruction.get(Argument.NUMBER),
                mob);
    }
}
