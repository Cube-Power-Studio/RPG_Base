package rpg.rpg_base.QuestModule.objectives;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import rpg.rpg_base.CustomizedClasses.EntityHandler.CEntity;
import rpg.rpg_base.RPG_Base;

import java.util.List;
import java.util.Locale;

public class KillCustomMobsObjective extends CountingObjective implements Listener {

    private final List<String> mobTypes;

    public KillCustomMobsObjective(Instruction instruction) throws InstructionParseException {
        super(instruction, "mobs_left");
        mobTypes = instruction.getList(original -> original);

        targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobKill(final MobKilledEvent e){
        LivingEntity entity = (LivingEntity) e.getEntity();

        if(entity.getKiller() != null) {
            final OnlineProfile onlineProfile = e.getProfile().getOnlineProfile().get();
            if (!containsPlayer(onlineProfile)) return;

            String mobID = entity.getPersistentDataContainer().get(CEntity.mobTypeKey, PersistentDataType.STRING);

            if (mobID == null || !mobTypes.contains(mobID)) return;

            if (checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, RPG_Base.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        final Integer data = switch (name.toLowerCase(Locale.ROOT)) {
            case "amount" -> getCountingData(profile).getCompletedAmount();
            case "left" -> getCountingData(profile).getAmountLeft();
            case "total" -> getCountingData(profile).getTargetAmount();
            case "absoluteamount" -> Math.abs(getCountingData(profile).getCompletedAmount());
            case "absoluteleft" -> Math.abs(getCountingData(profile).getAmountLeft());
            case "absolutetotal" -> Math.abs(getCountingData(profile).getTargetAmount());
            default -> null;
        };
        return data == null ? "" : data.toString();
    }
}
