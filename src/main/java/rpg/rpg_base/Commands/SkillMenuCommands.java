package rpg.rpg_base.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rpg.rpg_base.GUIs.SkillGui;
import rpg.rpg_base.GuiHandlers.GUIManager;
import rpg.rpg_base.RPG_Base;
import rpg.rpg_base.StatManager.EnduranceManager;

public class SkillMenuCommands implements CommandExecutor {
    private final GUIManager guiManager;
    private final RPG_Base plugin;
    private final EnduranceManager enduranceManager;
    public SkillMenuCommands (GUIManager guiManager, RPG_Base rpg_base, EnduranceManager enduranceManager){
        this.plugin = rpg_base;
        this.guiManager = guiManager;
        this.enduranceManager = enduranceManager;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("Skills")) {
            this.guiManager.openGui(new SkillGui(plugin), (Player) sender);
        }
        return true;
    }
}
