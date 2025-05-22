package rpg.rpg_base.QuestModule.events;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import rpg.rpg_base.Shops.ShopsManager;

public class ShopOpen implements OnlineEvent {
    final String shopName;

    public ShopOpen(String shopName){
        this.shopName = shopName;
    }

    @Override
    public void execute(OnlineProfile onlineProfile) throws QuestException {
        ShopsManager.openShop(onlineProfile.getPlayer(), shopName);
    }
}
