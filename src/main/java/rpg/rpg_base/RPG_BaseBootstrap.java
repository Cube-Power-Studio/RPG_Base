package rpg.rpg_base;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import rpg.rpg_base.Commands.RegisteredCommands;

@SuppressWarnings("UnstableApiUsage")
public class RPG_BaseBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        bootstrapContext.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            RegisteredCommands registeredCommands = new RegisteredCommands();
            registeredCommands.register();

            for(LiteralCommandNode node : registeredCommands.commandBuilders){
                commands.registrar().register(node);
            }
        });
    }
}
