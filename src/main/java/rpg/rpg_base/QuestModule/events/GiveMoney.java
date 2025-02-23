package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.MoneyHandlingModule.MoneyTypes;

public class GiveMoney implements OnlineEvent {
    final VariableNumber amount;
    final MoneyTypes type;

    public GiveMoney(VariableNumber amount, MoneyTypes type){
        this.amount = amount;
        this.type = type;
    }

    @Override
    public void execute(OnlineProfile profile) {
        try {
            int amountFinal = amount.getValue(profile).intValue();
            switch (type){
                case GOLD -> MoneyManager.addPlayerGold(profile.getPlayer(), amountFinal);
                case RUNICSIGILS -> MoneyManager.addPlayerRunicSigils(profile.getPlayer(), amountFinal);
                case GUILDMEDALS -> MoneyManager.addPlayerGuildMedals(profile.getPlayer(), amountFinal);
            }
        } catch (QuestRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
