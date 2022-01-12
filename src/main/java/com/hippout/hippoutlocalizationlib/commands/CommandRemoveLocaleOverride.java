package com.hippout.hippoutlocalizationlib.commands;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.api.*;
import com.hippout.hippoutlocalizationlib.locale.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.permissions.*;
import org.bukkit.util.*;

import javax.annotation.*;
import java.util.*;

/**
 * A Command to remove the Locale Override of a UUID or Player.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class CommandRemoveLocaleOverride implements CommandExecutor, TabCompleter {

    private final NamespacedKey PERMISSION_ERROR, PERMISSION_ERROR_SELF, PERMISSION_ERROR_ALL;

    private final NamespacedKey USAGE, SUCCESS, ERROR_NO_OVERRIDE;

    private final Permission manageAll, manageSelf;

    /**
     * Constructs a CommandRemoveLocaleOverride.
     *
     * @since 1.0.0
     */
    public CommandRemoveLocaleOverride()
    {
        final KeyRegistry keyRegistry = HippOutLocalizationLib.getKeyRegistry();

        PERMISSION_ERROR = keyRegistry.COM_GENERIC_PERMISSION_ERROR;
        PERMISSION_ERROR_SELF = keyRegistry.COM_GENERIC_PERMISSION_ERROR_MANAGE_SELF;
        PERMISSION_ERROR_ALL = keyRegistry.COM_GENERIC_PERMISSION_ERROR_MANAGE_ALL;

        USAGE = keyRegistry.COM_REMLOCALEOVERRIDE_USAGE;
        SUCCESS = keyRegistry.COM_REMLOCALEOVERRIDE_SUCCESS;
        ERROR_NO_OVERRIDE = keyRegistry.COM_REMLOCALEOVERRIDE_NO_OVERRIDE;

        manageAll = Bukkit.getPluginManager().getPermission("hippoutlocalizationlib.locales.manage.all");
        manageSelf = Bukkit.getPluginManager().getPermission("hippoutlocalizationlib.locales.manage.self");
    }

    /**
     * Called when the Command is executed.
     *
     * @param sender  CommandSender.
     * @param command Command.
     * @param args    Arguments.
     * @return True. False would print plugin.yml/usage
     * @since 1.0.0
     */
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label,
                             @Nonnull String[] args)
    {
        if (!sender.hasPermission(manageAll) && !sender.hasPermission(manageSelf)) {
            Macros.sendLocalized(PERMISSION_ERROR, sender);
            return true;
        }

        if (args.length != 1) {
            Macros.sendLocalized(USAGE, sender);
            return true;
        }

        final String targetArg = args[0];

        // Parse UUID
        final UUID id;
        try {
            id = TargetArgs.parseTargetArg(targetArg, Macros.getLocale(sender));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        // Check Permission
        if (!sender.hasPermission(manageAll)) {
            if (sender instanceof Player) {
                final Player p = (Player) sender;

                if (!p.getUniqueId().equals(id)) {
                    Macros.sendLocalized(PERMISSION_ERROR_ALL, sender);
                    return true;
                } else if (!sender.hasPermission(manageSelf)) {
                    Macros.sendLocalized(PERMISSION_ERROR_SELF, sender);
                    return true;
                }
            } else {
                Macros.sendLocalized(PERMISSION_ERROR_ALL, sender);
                return true;
            }
        }

        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getLocaleCache();
        if (!localeCache.hasLocaleOverride(id)) {
            Macros.sendLocalized(ERROR_NO_OVERRIDE, sender, targetArg.substring(2));
            return true;
        }

        localeCache.removeLocaleOverride(id);
        Macros.sendLocalized(SUCCESS, sender, targetArg.substring(2));
        return true;
    }

    /**
     * Handles Tab-completion.
     *
     * @param sender  CommandSender.
     * @param command Command.
     * @param alias   Alias String.
     * @param args    Arguments.
     * @return A List of Tab Completions.
     * @since 1.0.0
     */
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias,
                                      @Nonnull String[] args)
    {
        final List<String> suggestions = new LinkedList<>();
        if (args.length == 0) return suggestions;

        final String latestArg = args[args.length - 1];
        final String token;

        if (args.length == 1) {
            token = latestArg;
            suggestions.addAll(TargetArgs.tabCompleteTargetArg(latestArg));
        } else {
            return suggestions;
        }

        final List<String> outList = new LinkedList<>();
        StringUtil.copyPartialMatches(token, suggestions, outList);
        return outList;
    }
}
