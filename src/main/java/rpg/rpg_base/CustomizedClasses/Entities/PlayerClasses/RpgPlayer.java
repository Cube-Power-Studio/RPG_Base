package rpg.rpg_base.CustomizedClasses.Entities.PlayerClasses;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import rpg.rpg_base.CustomizedClasses.Entities.RpgEntity;

public class RpgPlayer extends RpgEntity {

    Player player = getPlayer();

    int xp = 0;
    int totalXp = 0;
    int xpToNextLvl = 0;

    public RpgPlayer(Player player) {

    }

    @Override
    public void update() {

    }

    @Override
    public void dealDamage(int amount, Entity damager) {

    }

    public void kill(Entity entity){

    }

    public void addXp(int amount){
        xp += amount;
    }

    public Player getPlayer(){
        return player;
    }
}
