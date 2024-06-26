package rpg.rpg_base.IslandManager.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import rpg.rpg_base.IslandManager.Island;

import java.util.UUID;

public abstract class BlockEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UUID player;
    private Island island;



    /**
     * @param player
     * @param island
     */
    public BlockEvent(UUID player, Island island) {
        this.player = player;
        this.island = island;
    }

    public BlockEvent(UUID player) {
        this.player = player;
        this.island = null;
    }

    /**
     * Gets the player involved in this event
     * @return the player
     */
    public UUID getPlayer() {
        return player;
    }

    /**
     * The island involved in the event
     * @return the island
     */
    public Island getIsland() {
        return island;
    }

    /**
     * Convenience function to obtain the island's protection size
     * @return the protectionSize
     */
    public int getProtectionSize() {
        return island.getProtectionSize();
    }

    /**
     * Convenience function to obtain the island's locked status
     * @return the isLocked
     */
    public boolean isLocked() {
        return island.isLocked();
    }

    /**
     * Convenience function to obtain the island's distance
     * @return the islandDistance
     */
    public int getIslandDistance() {
        return island.getIslandDistance();
    }

    /**
     * @return the teamLeader
     */
    public UUID getTeamLeader() {
        return island.getOwner();
    }

    /**
     * Convenience function to obtain the island's owner
     * @return UUID of owner
     */
    public UUID getIslandOwner() {
        return island.getOwner();
    }

    /**
     * Convenience function to obtain the island's center location
     * @return the island location
     */
    public Location getIslandLocation() {
        return island.getCenter();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
