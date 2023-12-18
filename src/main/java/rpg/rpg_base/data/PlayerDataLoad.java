package rpg.rpg_base.data;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataLoad {
    private static Map<String, PlayerData> playerData = new HashMap<>();

    public static PlayerData getPlayerData(Player p) {
        String uuid = p.getUniqueId().toString();

        if (playerData.containsKey(uuid)) {
            return playerData.get(uuid);
        }

        // If the player data is not in the map, create a new instance
        PlayerData data = new PlayerData();
        playerData.put(uuid, data);
        return data;
    }

    public static void setPlayerData(Player p, PlayerData data) {
        playerData.put(p.getUniqueId().toString(), data);
    }

    public static String getFolderPath(Player p) {
        String folderPath = "plugins/RPG_Base/player/" + p.getUniqueId();
        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folderPath;
    }
}