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

import rpg.rpg_base.IslandManager.Island;

import java.util.UUID;



/**
 * Fired when a player leaves an island team
 * @author tastybento
 *
 */
public class IslandLeaveEvent extends BlockEvent {

    /**
     * @param player
     * @param island
     */
    public IslandLeaveEvent(UUID player, Island island) {
        super(player, island);
    }

}
