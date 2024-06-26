package rpg.rpg_base.IslandManager.events;

import org.bukkit.event.Cancellable;
import rpg.rpg_base.IslandManager.Island;

import java.util.UUID;

public class CoopLeaveEvent extends BlockEvent implements Cancellable {
    private final UUID expeller;
    private boolean cancelled;

    /**
     * Note that not all coop leaving events can be cancelled because they could be due to bigger events than
     * coop, e.g., an island being reset.
     * @param expelledPlayer
     * @param expellingPlayer
     * @param island
     */
    public CoopLeaveEvent(UUID expelledPlayer, UUID expellingPlayer, Island island) {
        super(expelledPlayer, island);
        this.expeller = expellingPlayer;
        //Bukkit.getLogger().info("DEBUG: Coop leave event " + Bukkit.getServer().getOfflinePlayer(expelledPlayer).getName() + " was expelled from "
        //	+ Bukkit.getServer().getOfflinePlayer(expellingPlayer).getName() + "'s island.");
    }

    /**
     * @return the expelling player
     */
    public UUID getExpeller() {
        return expeller;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.Cancellable#setCancelled(boolean)
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}