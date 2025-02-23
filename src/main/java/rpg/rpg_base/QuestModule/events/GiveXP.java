package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;

public class GiveXP implements OnlineEvent {
    final VariableNumber amount;

    public GiveXP(VariableNumber amount){
        this.amount = amount;
    }

    @Override
    public void execute(OnlineProfile profile) {
        try {
            int amountFinal = amount.getValue(profile).intValue();
            CPlayer.getPlayerByUUID(profile.getPlayerUUID()).xp += amountFinal;
            CPlayer.getPlayerByUUID(profile.getPlayerUUID()).updateStats();
        } catch (QuestRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
