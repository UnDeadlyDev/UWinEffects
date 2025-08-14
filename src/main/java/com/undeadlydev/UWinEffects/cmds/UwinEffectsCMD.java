package com.undeadlydev.UWinEffects.cmds;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.managers.CommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UwinEffectsCMD extends CommandManager<Main> {
    private final Main plugin;

    public UwinEffectsCMD(Main plugin) {
        super(plugin, "uwineffects");
        register();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUso: /uwineffects <start|stop|reload> [jugador]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (args.length >= 2) {
                    Player target = plugin.getServer().getPlayer(args[1]);
                    if (target != null && target.isOnline()) {
                        DBPlayer dbPlayer = plugin.getDb().getDBPlayer(target);
                        plugin.getCos().executeWinEffect(target, dbPlayer.getWinEffect());
                        sender.sendMessage("§aEfecto de victoria iniciado para " + target.getName());
                    } else {
                        sender.sendMessage("§cJugador no encontrado o no está en línea.");
                    }
                } else {
                    sender.sendMessage("§cUso: /uwineffects start <jugador>");
                }
                break;

            case "stop":
                if (args.length >= 2) {
                    Player target = plugin.getServer().getPlayer(args[1]);
                    if (target != null && target.isOnline()) {
                        if (plugin.getCos().winEffectsTask.containsKey(target.getUniqueId())) {
                            plugin.getCos().winEffectsTask.remove(target.getUniqueId()).stop();
                            sender.sendMessage("§aEfecto de victoria detenido para " + target.getName());
                        } else {
                            sender.sendMessage("§cEse jugador no tiene efecto activo.");
                        }
                    } else {
                        sender.sendMessage("§cJugador no encontrado o no está en línea.");
                    }
                } else {
                    sender.sendMessage("§cUso: /uwineffects stop <jugador>");
                }
                break;

            case "reload":
                if (args.length == 1) {
                    plugin.reload();
                    sender.sendMessage(plugin.getLang().get( "setup.reload"));
                } else {
                    switch (args[1].toLowerCase()) {
                        case "lang":
                            plugin.reloadLang();
                            sender.sendMessage(plugin.getLang().get("setup.reloadLang"));
                            break;
                        case "config":
                            plugin.reloadConfig();
                            sender.sendMessage(plugin.getLang().get("setup.reload"));
                            break;
                        case "menu":
                            plugin.getMenus().reload();
                            plugin.getUim().loadMenus();
                            sender.sendMessage(plugin.getLang().get("setup.reloadMenu"));
                            break;
                        default:
                            sender.sendMessage("§cOpciones: lang, config, menu");
                            break;
                    }
                }
                break;

            default:
                sender.sendMessage("§cSubcomando desconocido.");
                break;
        }
        return true;
    }
}
