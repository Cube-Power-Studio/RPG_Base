package rpg.rpg_base.data;

import org.bukkit.entity.Player;

import java.io.File;


public class PlayerDataLoad {

    public static String getFolderPath(Player p) {
        String folderPath = "plugins/RPG_Base/player/" + p.getUniqueId();
        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folderPath;
    }
}