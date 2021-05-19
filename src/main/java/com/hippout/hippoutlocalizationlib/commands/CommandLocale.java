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
 * Command to get the Locale of a UUID or Player.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class CommandLocale implements CommandExecutor, TabCompleter {

    private final NamespacedKey PERMISSION_ERROR, PERMISSION_ERROR_SELF, PERMISSION_ERROR_ALL;

    private final NamespacedKey USAGE, SUCCESS, SUCCESS_OVERRIDE, NO_LOCALE;

    private final Permission checkAll, checkSelf;

    /**
     * Constructs a CommandLocale.
     *
     * @since 1.0.0
     */
    public CommandLocale()
    {
        final KeyRegistry keyRegistry = HippOutLocalizationLib.getKeyRegistry();

        PERMISSION_ERROR = keyRegistry.COM_GENERIC_PERMISSION_ERROR;
        PERMISSION_ERROR_SELF = keyRegistry.COM_GENERIC_PERMISSION_ERROR_CHECK_SELF;
        PERMISSION_ERROR_ALL = keyRegistry.COM_GENERIC_PERMISSION_ERROR_CHECK_ALL;

        USAGE = keyRegistry.COM_LOCALE_USAGE;
        SUCCESS = keyRegistry.COM_LOCALE_SUCCESS;
        SUCCESS_OVERRIDE = keyRegistry.COM_LOCALE_SUCCESS_OVERRIDE;
        NO_LOCALE = keyRegistry.COM_LOCALE_NO_LOCALE;

        checkAll = Bukkit.getPluginManager().getPermission("hippoutlocalizationlib.locales.check.all");
        checkSelf = Bukkit.getPluginManager().getPermission("hippoutlocalizationlib.locales.check.self");
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
        if (!sender.hasPermission(checkAll) && !sender.hasPermission(checkSelf)) {
            Macros.sendLocalizedMessage(PERMISSION_ERROR, sender);
            return true;
        }

        if (args.length != 1) {
            Macros.sendLocalizedMessage(USAGE, sender);
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
        if (!sender.hasPermission(checkAll)) {
            if (sender instanceof Player) {
                final Player p = (Player) sender;

                if (!p.getUniqueId().equals(id)) {
                    Macros.sendLocalizedMessage(PERMISSION_ERROR_ALL, sender);
                    return true;
                } else if (!sender.hasPermission(checkSelf)) {
                    Macros.sendLocalizedMessage(PERMISSION_ERROR_SELF, sender);
                    return true;
                }
            } else {
                Macros.sendLocalizedMessage(PERMISSION_ERROR_ALL, sender);
                return true;
            }
        }

        final String targetArgShort = targetArg.substring(2);
        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getLocaleCache();

        final String locale = localeCache.getLocale(id);

        if (localeCache.hasLocaleOverride(id))
            Macros.sendLocalizedMessage(SUCCESS_OVERRIDE, sender, targetArgShort, locale);
        else
            Macros.sendLocalizedMessage(SUCCESS, sender, targetArgShort, locale);

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

        if (args.length == 1)
            suggestions.addAll(TargetArgs.tabCompleteTargetArg(latestArg));
        else
            return suggestions;

        final List<String> outList = new LinkedList<>();
        StringUtil.copyPartialMatches(latestArg, suggestions, outList);
        return outList;
    }
}
