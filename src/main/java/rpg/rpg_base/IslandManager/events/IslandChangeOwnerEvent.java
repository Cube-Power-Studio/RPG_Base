package rpg.rpg_base.IslandManager.events;

import rpg.rpg_base.IslandManager.Island;

import java.util.UUID;

public class IslandChangeOwnerEvent extends BlockEvent {
    private final UUID oldOwner, newOwner;

    /**
     * @param island
     * @param oldOwner
     * @param newOwner
     */
    public IslandChangeOwnerEvent(Island island, UUID oldOwner, UUID newOwner) {
        super(oldOwner, island);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    /**
     * @return the old owner
     */
    public UUID getOldOwner() {
        return oldOwner;
    }

    /**
     * @return the new owner
     */
    public UUID getNewOwner() {
        return newOwner;
    }
}