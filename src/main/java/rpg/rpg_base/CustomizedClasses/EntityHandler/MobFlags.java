package rpg.rpg_base.CustomizedClasses.EntityHandler;


import com.sk89q.worldguard.protection.flags.StateFlag;

public final class MobFlags {
    public final static StateFlag customMobsFlag = new StateFlag("custom-mob-spawn", false);
    public final static StateFlag customMobsTeleportBackFlag = new StateFlag("custom-mob-teleport-back", false);
}