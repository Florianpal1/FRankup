package fr.florianpal.frankup.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import fr.florianpal.frankup.FRankup;
import fr.florianpal.frankup.gui.subGui.MainGui;
import fr.florianpal.frankup.languages.MessageKeys;
import fr.florianpal.frankup.managers.commandManagers.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("rankup")
public class RankupCommand extends BaseCommand {

    private final CommandManager commandManager;
    private final FRankup plugin;

    public RankupCommand(FRankup plugin) {
        this.plugin = plugin;
        this.commandManager = plugin.getCommandManager();
    }

    @Default
    @CommandPermission("frankup.list")
    @Description("{@@frankup.rankup_help_description}")
    public void onList(Player playerSender){
        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        if (plugin.getConfigurationManager().getLastRank(playerSender) != null) {
            MainGui gui = new MainGui(plugin, playerSender, 1);
            gui.initializeItems();
            issuerTarget.sendInfo(MessageKeys.OPEN_MAIN);
        } else {
            issuerTarget.sendInfo(MessageKeys.RANKUP_NO_MORE);
        }
    }

    @Subcommand("reload")
    @CommandPermission("frankup.reload")
    @Description("{@@frankup.reload_help_description}")
    public void onReload(Player playerSender) {
        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.reloadConfig();
        issuerTarget.sendInfo(MessageKeys.RELOAD);
    }

    @HelpCommand
    @Description("{@@frankup.help_description}")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
