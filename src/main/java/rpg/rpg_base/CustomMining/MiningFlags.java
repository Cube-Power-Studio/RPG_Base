package rpg.rpg_base.CustomMining;

import com.sk89q.worldguard.protection.flags.StateFlag;

public final class MiningFlags {
    public final static StateFlag oreMiningFlag = new StateFlag("custom-ore-mining", false);
    public final static StateFlag customOreFlag = new StateFlag("custom-ores", false);
    public final static StateFlag customBlockMining = new StateFlag("custom-block-mining", false);
}
