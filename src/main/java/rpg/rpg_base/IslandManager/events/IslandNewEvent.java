/*******************************************************************************
 * This file is part of ASkyBlock.
 *
 *     ASkyBlock is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ASkyBlock is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ASkyBlock.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package rpg.rpg_base.IslandManager.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import rpg.rpg_base.IslandManager.Island;
import rpg.rpg_base.data.NBT;

/**
 * This event is fired when a player starts a new island
 * 
 * @author tastybento
 * 
 */
public class IslandNewEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final NBT schematic;
    private final Island island;

    /**
     * @param player
     * @param schematic
     * @param island
     */
    public IslandNewEvent(Player player, NBT schematic, Island island) {
        this.player = player;
        this.schematic = schematic;
        this.island = island;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the schematicName
     */
    public NBT getSchematicName() {
        return schematic;
    }

    /**
     * @return the island
     */
    public Location getIslandLocation() {
        return island.getCenter();
    }

    /**
     * @return the protectionSize
     */
    public int getProtectionSize() {
        return island.getProtectionSize();
    }

    /**
     * @return the isLocked
     */
    public boolean isLocked() {
        return island.isLocked();
    }

    /**
     * @return the islandDistance
     */
    public int getIslandDistance() {
        return island.getIslandDistance();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
