package rpg.rpg_base.data;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface NMSAbstraction {

    /**
     * Update the low-level chunk information for the given block to the new block ID and data.  This
     * change will not be propagated to clients until the chunk is refreshed to them.
     * @param block
     * @param blockId
     * @param data
     * @param applyPhysics
     */
    void setBlockSuperFast(Block block, int blockId, byte data, boolean applyPhysics);

    ItemStack setBook(Tag item);

    /**
     * Sets a block to be an item stack
     * @param block
     * @param itemStack
     */
    void setFlowerPotBlock(Block block, ItemStack itemStack);

    boolean isPotion(ItemStack item);

    /**
     * Returns a potion ItemStack
     * @param itemMaterial
     * @param itemTag
     * @param chestItem
     * @return
     */
    ItemStack setPotion(Material itemMaterial, Tag itemTag, ItemStack chestItem);

    /**
     * Gets a monster egg itemstack
     * @param type
     * @param amount
     * @return itemstack
     */
    ItemStack getSpawnEgg(EntityType type, int amount);
}