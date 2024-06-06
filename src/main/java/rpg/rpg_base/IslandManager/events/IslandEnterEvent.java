package rpg.rpg_base.IslandManager.events;

import org.bukkit.Location;
import rpg.rpg_base.IslandManager.Island;

import java.util.UUID;

public class IslandEnterEvent extends BlockEvent {
    private final Location location;

    /**
     * Called to create the event
     * @param player
     * @param island - island the player is entering
     * @param location - Location of where the player entered the island or tried to enter
     */
    public IslandEnterEvent(UUID player, Island island, Location location) {
        super(player,island);
        this.location = location;
        //Bukkit.getLogger().info("DEBUG: IslandEnterEvent called");
    }

    /**
     * Location of where the player entered the island or tried to enter
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

}
