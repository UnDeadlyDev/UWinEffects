package com.undeadlydev.UWinEffects.cmds;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.managers.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class winEffectsCMD extends CommandManager<Main> {
    public Main plugin;

    public winEffectsCMD(Main plugin) {
        super(plugin, "wineffects");
        register();
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            DBPlayer dbPlayer = plugin.getDb().getDBPlayer(p);
            if (args.length < 1) {
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "menu":
                    this.plugin.getUim().getPages().put(p.getUniqueId(), 1);
                    this.plugin.getUim().createWinEffectSelectorMenu(p);
                    break;
                case "coins":
                    p.sendMessage(plugin.getLang().get(p,"").replace("<coins>", String.valueOf(dbPlayer.getCoins())));
                    break;
                default:
                    break;

            }
        }
        return true;
    }
}
