package rpg.rpg_base.hooks;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import rpg.rpg_base.RPG_Base;

public final class Protocolib {
    public static void register(){
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(RPG_Base.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.REL_ENTITY_MOVE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                super.onPacketReceiving(event);
            }
        });
    }
}
