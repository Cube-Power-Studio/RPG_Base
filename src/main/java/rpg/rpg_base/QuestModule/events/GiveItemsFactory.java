package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.inventory.ItemStack;
import rpg.rpg_base.CustomizedClasses.ItemHandler.CItem;
import rpg.rpg_base.CustomizedClasses.ItemHandler.ItemManager;

public class GiveItemsFactory implements EventFactory {
    VariableNumber amount;
    CItem item;

    private final BetonQuestLoggerFactory loggerFactory;

    public GiveItemsFactory(BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Event parseEvent(Instruction instruction) throws InstructionParseException {
        final BetonQuestLogger log = loggerFactory.create(RemoveItems.class);

        item = CItem.customItemsByName.get(instruction.getPart(1));
        amount = instruction.getVarNum(instruction.getOptional("amount", "1"));

        return new OnlineEventAdapter( new GiveItems(
                item.getItem(),
                amount),
                log, instruction.getPackage()
        );
    }
}
