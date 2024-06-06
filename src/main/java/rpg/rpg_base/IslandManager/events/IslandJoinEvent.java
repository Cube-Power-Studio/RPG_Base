package rpg.rpg_base.IslandManager.events;

import java.util.UUID;

import rpg.rpg_base.IslandManager.Island;


/**
 * This event is fired when a player joins an island team
 * 
 * @author tastybento
 * 
 */
public class IslandJoinEvent extends BlockEvent {


    /**
     * @param player
     * @param island
     */
    public IslandJoinEvent(UUID player, Island island) {
        super(player,island);
    }

}
