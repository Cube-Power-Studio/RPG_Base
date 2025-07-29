package rpg.rpg_base.CustomizedClasses.Entities.PlayerClasses;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    public static Map<UUID, RpgPlayer> playerMap = new HashMap<>();

    public static void registerPlayer(RpgPlayer player){
        playerMap.put(player.getPlayer().getUniqueId(), player);
    }
    public static RpgPlayer getPlayer(UUID uuid){
        return playerMap.get(uuid);
    }
}
