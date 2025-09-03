package com.undeadlydev.UWinEffects.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandManager<T extends JavaPlugin> extends Command implements CommandExecutor, PluginIdentifiableCommand {

    private static CommandMap commandMap;

    static {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private final T plugin;
    private final HashMap<Integer, List<TabCommand>> tabComplete;
    private boolean registered = false;
    private String description = "";

    protected CommandManager(T plugin, String name) {
        super(name);

        assert commandMap != null;
        assert plugin != null;
        assert name != null && !name.isEmpty();

        setLabel(name);
        this.plugin = plugin;
        this.tabComplete = new HashMap<>();
    }

    public Command setDescription(String description) {
        this.description = description;
        return null;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public T getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage() != null ? getPermissionMessage() : ChatColor.RED + "You do not have permission to use this command.");
            return true; // Indicate that the command was handled (permission denied)
        }
        return onCommand(sender, this, commandLabel, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        int index = args.length - 1;

        if ((getPermission() != null && !sender.hasPermission(getPermission())) || tabComplete.isEmpty() || !tabComplete.containsKey(index)) {
            return super.tabComplete(sender, alias, args);
        }

        String currentArg = args[index].toLowerCase();
        String previousArg = (index > 0) ? args[index - 1].toLowerCase() : null;

        return tabComplete.get(index).stream()
                .filter(tab -> tab.getRequiredPreviousArgument() == null || (previousArg != null && tab.getRequiredPreviousArgument().contains(previousArg)))
                .filter(tab -> tab.getText().toLowerCase().startsWith(currentArg))
                .filter(tab -> tab.getPermission() == null || sender.hasPermission(tab.getPermission()))
                .map(TabCommand::getText)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    protected void setAliases(String... aliases) {
        if (aliases != null && aliases.length > 0) {
            setAliases(Arrays.asList(aliases));
        }
    }

    protected void addTabSimple(int index, String... suggestions) {
        addTabWithContext(index, null, null, suggestions);
    }

    protected void addTabWithPermission(int index, String permission, String... suggestions) {
        addTabWithContext(index, permission, null, suggestions);
    }

    protected void addTabWithContext(int index, String permission, String requiredPreviousArgument, String... suggestions) {
        if (suggestions != null && suggestions.length > 0 && index >= 0) {
            tabComplete.computeIfAbsent(index, k -> new ArrayList<>())
                    .addAll(Arrays.stream(suggestions)
                            .map(s -> new TabCommand(s, permission, requiredPreviousArgument))
                            .collect(Collectors.toList()));
        }
    }

    protected boolean register() {
        if (!registered) {
            if (commandMap.register(plugin.getName(), this)) {
                registered = true;
                return true;
            } else {
                plugin.getLogger().warning("Failed to register command: " + getName());
                return false;
            }
        }
        return true;
    }

    private static class TabCommand {
        private final String text;
        private final String permission;
        private final String requiredPreviousArgument;

        public TabCommand(String text, String permission, String requiredPreviousArgument) {
            this.text = text;
            this.permission = permission;
            this.requiredPreviousArgument = requiredPreviousArgument;
        }

        public String getText() {
            return text;
        }

        public String getPermission() {
            return permission;
        }

        public String getRequiredPreviousArgument() {
            return requiredPreviousArgument;
        }
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}