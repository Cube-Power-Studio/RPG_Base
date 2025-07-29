package rpg.rpg_base.QuestModule.conditions;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.bukkit.inventory.ItemStack;

public class CustomItemCountFactory implements PlayerConditionFactory, PlayerlessConditionFactory {
    private final PrimaryServerThreadData data;

    public CustomItemCountFactory(PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(parseItemCondition(instruction), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(parseItemCondition(instruction), data);
    }

    private NullableConditionAdapter parseItemCondition(final Instruction instruction) throws QuestException {

        if(CItem.customItemsByName.get(instruction.getPart(1)).getItem() != null){

            ItemStack item = CItem.customItemsByName.get(instruction.getPart(1)).getItem();
            int count = Integer.parseInt(instruction.getPart(2));
            return new NullableConditionAdapter(new CustomItemCount(count, item));
        }else{
            return null;
        }
    }
}
