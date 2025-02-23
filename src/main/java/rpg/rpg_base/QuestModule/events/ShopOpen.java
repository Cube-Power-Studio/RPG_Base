package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;
import rpg.rpg_base.Shops.ShopsManager;

public class ShopOpen implements OnlineEvent {
    final String shopName;

    public ShopOpen(String shopName){
        this.shopName = shopName;
    }

    @Override
    public void execute(OnlineProfile onlineProfile) throws QuestRuntimeException {
        ShopsManager.openShop(onlineProfile.getPlayer(), shopName);
    }
}
