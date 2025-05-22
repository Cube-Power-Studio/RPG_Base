package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;

public class GiveXP implements OnlineEvent {
    final Variable<Number> amount;

    public GiveXP(Variable<Number> amount){
        this.amount = amount;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        int amountFinal = amount.getValue(profile).intValue();
        CPlayer.getPlayerByUUID(profile.getPlayerUUID()).xp += amountFinal;
        CPlayer.getPlayerByUUID(profile.getPlayerUUID()).updateStats();
    }
}
