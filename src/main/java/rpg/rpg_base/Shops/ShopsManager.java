package rpg.rpg_base.Shops;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.GuiHandlers.InventoryGUI;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.Shops.ShopsUI.ShopUiFrame;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ShopsManager {

    public static GUIManager guiManager;

    public static HashMap<String, ShopUiFrame> shopRegister = new HashMap<>();
    public static HashMap<Player, InventoryGUI> currentOpenShop = new HashMap<>();
    public static HashMap<Player, String /* SHOP NAME */> currentOpenShopName = new HashMap<>();

    public ShopsManager(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public static void loadShops(){
        File file = new File(RPG_Base.getInstance().getDataFolder() + "/shops");
        if(!file.exists()){
            file.mkdir();
        }
        if (file.listFiles() != null) {
            for(File shopFile : file.listFiles()) {

                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(shopFile);

                if (cfg.getString("name") == null) {
                    return;
                }

                String shopName = cfg.getString("name");

                ShopUiFrame shop = new ShopUiFrame(shopName);

                for (String cfgPage : cfg.getConfigurationSection("pages").getKeys(false)) {
                    String pageName = cfg.getString("pages." + cfgPage + ".pagename");
                    int pageNum = cfg.getInt("pages." + cfgPage + ".num");

                    shop.pageNameMap.put(pageNum, pageName);

                    List<String> items = cfg.getStringList("pages." + cfgPage + ".contents");
                    for(String item : items){
                        items.set(items.indexOf(item), item + "," + pageNum);
                    }

                    shop.itemList.addAll(items);
                }

                shop.loadShop();

                shopRegister.put(shopName, shop);
            }
        }else{
            shopRegister.put("default", new ShopUiFrame("default"));
        }

    }

    public static void openShop(Player player, String string) {

        String shopName = string.replace("_", " ").trim();

        if (shopRegister.get(shopName) == null) {
            player.sendMessage("Shop not found.");

            // Log all the available shop names for debugging
            player.sendMessage("Available shops:");
            for (String str : shopRegister.keySet()) {
                player.sendMessage(str);  // Send each shop name to the player for debugging
            }

            return;
        }

        // Retrieve and open the shop
        ShopUiFrame shop = shopRegister.get(shopName);
        if (shop != null) {
            shop.currentPage = 1;
            currentOpenShop.put(player, shop);
            currentOpenShopName.put(player, string);
            guiManager.openGui(shop, player);

            Bukkit.getScheduler().runTaskLater(RPG_Base.getInstance(), () -> {
                shop.decorate(player); // Ensures client receives the correct inventory state
            }, 2L);
        }
    }

    public static void closeShop(Player player){
        currentOpenShop.remove(player);
        currentOpenShopName.remove(player);
    }
}
