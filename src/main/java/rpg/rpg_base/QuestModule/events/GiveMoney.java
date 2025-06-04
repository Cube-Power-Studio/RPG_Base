package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.Instruction;
import rpg.rpg_base.MoneyHandlingModule.MoneyManager;
import rpg.rpg_base.MoneyHandlingModule.MoneyTypes;

public class GiveMoney implements OnlineEvent {
    final Instruction instruction;
    final MoneyTypes type;

    public GiveMoney(Instruction instruction, MoneyTypes type){
        this.instruction = instruction;
        this.type = type;
    }

    @Override
    public void execute(OnlineProfile profile) throws QuestException {
        int finalAmount;
        if(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(1), profile) != null){
            finalAmount = Integer.parseInt(BetonQuest.getInstance().getVariableProcessor().getValue(instruction.getPackage(), instruction.getPart(1), profile));
        }else{
            finalAmount = Integer.parseInt(instruction.getPart(1));
        }

        switch (type) {
            case GOLD -> MoneyManager.addPlayerGold(profile.getPlayer().getPlayer(), finalAmount);
            case RUNICSIGILS -> MoneyManager.addPlayerRunicSigils(profile.getPlayer().getPlayer(), finalAmount);
            case GUILDMEDALS -> MoneyManager.addPlayerGuildMedals(profile.getPlayer().getPlayer(), finalAmount);
        }
    }
}
