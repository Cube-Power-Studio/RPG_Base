package rpg.rpg_base.CustomMining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockSpawner extends BukkitRunnable {
    private final Block block;
    private final Material material;

    public BlockSpawner(Block block, Material material){
        this.block = block;
        this.material = material;
    }

    @Override
    public void run() {
        double blockX = block.getX();
        double blockY = block.getY();
        double blockZ = block.getZ();

        Location location = new Location(block.getWorld(), blockX, blockY, blockZ);
        Block replacement = block.getWorld().getBlockAt(location);

        replacement.setType(material);
        this.cancel();
    }
}
