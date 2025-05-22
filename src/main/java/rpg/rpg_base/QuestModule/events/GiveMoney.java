package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.MoneyHandlingModule.MoneyTypes;

public class GiveMoney implements OnlineEvent {
    final Variable<Number> amount;
    final MoneyTypes type;

    public GiveMoney(Variable<Number> amount, MoneyTypes type){
        this.amount = amount;
        this.type = type;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        int amountFinal = amount.getValue(profile).intValue();
        switch (type) {
            case GOLD -> MoneyManager.addPlayerGold(profile.getPlayer().getPlayer(), amountFinal);
            case RUNICSIGILS -> MoneyManager.addPlayerRunicSigils(profile.getPlayer().getPlayer(), amountFinal);
            case GUILDMEDALS -> MoneyManager.addPlayerGuildMedals(profile.getPlayer().getPlayer(), amountFinal);
        }
    }
}
