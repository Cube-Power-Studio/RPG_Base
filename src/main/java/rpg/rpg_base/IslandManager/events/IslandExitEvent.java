package rpg.rpg_base.IslandManager.events;

import org.bukkit.Location;
import rpg.rpg_base.IslandManager.Island;

import java.util.UUID;

public class IslandExitEvent extends BlockEvent {
    private final Location location;

    /**
     * @param player
     * @param island that the player is leaving
     * @param location - Location of where the player exited the island's protected area
     */
    public IslandExitEvent(UUID player, Island island, Location location) {
        super(player,island);
        this.location = location;
        //Bukkit.getLogger().info("DEBUG: IslandExitEvent called");
    }

    /**
     * Location of where the player exited the island's protected area
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

}