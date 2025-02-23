package rpg.rpg_base.CustomizedClasses.PlayerHandler;

import org.bukkit.scheduler.BukkitRunnable;

public class PlayerRegenTask extends BukkitRunnable {
    private final CPlayer player;
    public int cooldown = 0;

    public PlayerRegenTask(CPlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        if(cooldown == 0){
            player.regenHp();
        }else{
            cooldown --;
        }
    }
}
