package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.Instruction;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;

public class GiveXP implements OnlineEvent {
    final Instruction instruction;

    public GiveXP(Instruction amount){
        this.instruction = amount;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        int finalAmount;
        if(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(1), profile) != null){
            finalAmount = Integer.parseInt(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(1), profile));
        }else{
            finalAmount = Integer.parseInt(instruction.getPart(1));
        }

        CPlayer.getPlayerByUUID(profile.getPlayerUUID()).xp += finalAmount;
        CPlayer.getPlayerByUUID(profile.getPlayerUUID()).updateStats();
    }
}
