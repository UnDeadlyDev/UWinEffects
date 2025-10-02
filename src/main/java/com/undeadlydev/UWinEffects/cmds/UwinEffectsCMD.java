package com.undeadlydev.UWinEffects.cmds;

import com.undeadlydev.UWinEffects.Main;
import com.undeadlydev.UWinEffects.data.DBPlayer;
import com.undeadlydev.UWinEffects.enums.Permission;
import com.undeadlydev.UWinEffects.managers.CommandManager;
import com.undeadlydev.UWinEffects.superclass.UltraInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UwinEffectsCMD extends CommandManager<Main> {
    private final Main plugin;

    public UwinEffectsCMD(Main plugin) {
        super(plugin, "uwineffects");
        setPermission(Permission.ADMIN.get());
        register();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.usage"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "editinv":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getLang().get("messages.commands.errors.only-player"));
                    return true;
                }
                handleEditInventoryCommand((Player) sender, args);
                break;
            case "start":
                handleStartCommand(sender, args);
                break;
            case "stop":
                handleStopCommand(sender, args);
                break;
            case "reload":
                handleReloadCommand(sender, args);
                break;
            case "coins":
                handleCoinsCommand(sender, args);
                break;
            default:
                sender.sendMessage(plugin.getLang().get("messages.commands.errors.unknown-subcommand"));
                break;
        }
        return true;
    }

    private void handleEditInventoryCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getLang().get("messages.commands.wineffects.editinv.usage"));
            return;
        }
        String type = args[1].toLowerCase();

        switch (type) {
            case "wineffects":
                UltraInventory inventory = plugin.getUim().getMenus("wineffectsselector");
                plugin.getUim().openInventory(player, inventory);
                plugin.getSm().setSetupInventory(player, inventory);
                break;
            default:
                player.sendMessage(plugin.getLang().get("messages.commands.wineffects.editinv.unknown-type"));
                break;
        }
    }

    private void handleStartCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.start.usage"));
            return;
        }
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.getLang().get("messages.commands.errors.no-player"));
            return;
        }
        if (plugin.getCos().winEffectsTask.containsKey(target.getUniqueId()))
            return;
        DBPlayer dbPlayer = plugin.getDb().getDBPlayer(target);
        plugin.getCos().executeWinEffect(target, dbPlayer.getWinEffect());
        sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.start.success").replace("%player%", target.getName()));
    }

    private void handleStopCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.stop.usage"));
            return;
        }
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.getLang().get("messages.commands.errors.no-player"));
            return;
        }
        if (plugin.getCos().winEffectsTask.containsKey(target.getUniqueId())) {
            plugin.getCos().winEffectsTask.remove(target.getUniqueId()).stop();
            sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.stop.success").replace("%player%", target.getName()));
        } else {
            sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.stop.no-effect"));
        }
    }

    private void handleReloadCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            plugin.reload();
            sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.reload.all"));
        } else {
            String reloadType = args[1].toLowerCase();
            switch (reloadType) {
                case "lang":
                    plugin.reloadLang();
                    sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.reload.lang"));
                    break;
                case "config":
                    plugin.reloadConfig();
                    sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.reload.config"));
                    break;
                case "menu":
                    plugin.getMenus().reload();
                    plugin.getUim().loadMenus();
                    sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.reload.menu"));
                    break;
                default:
                    sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.reload.invalid-option"));
                    break;
            }
        }
    }

    private void handleCoinsCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.coins.usage"));
            return;
        }
        String action = args[1].toLowerCase();
        String playerName = args[2];
        int amount;

        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLang().get("messages.commands.errors.invalid-amount"));
            return;
        }

        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(plugin.getLang().get("messages.commands.errors.no-player"));
            return;
        }

        DBPlayer dbPlayer = plugin.getDb().getDBPlayer(target);

        switch (action) {
            case "add":
                dbPlayer.addCoins(amount);
                plugin.getDb().savePlayer(target);
                sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.coins.added")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName()));
                break;
            case "remove":
                if (dbPlayer.getCoins() < amount) {
                    sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.coins.not-enough")
                            .replace("%player%", target.getName()));
                    return;
                }
                dbPlayer.removeCoins(amount);
                plugin.getDb().savePlayer(target);
                sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.coins.removed")
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", target.getName()));
                break;
            default:
                sender.sendMessage(plugin.getLang().get("messages.commands.wineffects.coins.invalid-action"));
                break;
        }
    }
}