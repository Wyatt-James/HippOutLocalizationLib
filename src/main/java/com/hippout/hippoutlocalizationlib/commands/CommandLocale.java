package com.hippout.hippoutlocalizationlib.commands;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.api.*;
import com.hippout.hippoutlocalizationlib.locale.*;
import org.bukkit.*;
import org.bukkit.command.*;
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

    private final NamespacedKey USAGE, SUCCESS, SUCCESS_OVERRIDE, NO_LOCALE;

    public CommandLocale()
    {
        final KeyRegistry keyRegistry = HippOutLocalizationLib.getKeyRegistry();

        USAGE = keyRegistry.COM_LOCALE_USAGE;
        SUCCESS = keyRegistry.COM_LOCALE_SUCCESS;
        SUCCESS_OVERRIDE = keyRegistry.COM_LOCALE_SUCCESS_OVERRIDE;
        NO_LOCALE = keyRegistry.COM_LOCALE_NO_LOCALE;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label,
                             @Nonnull String[] args)
    {
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

        final String targetArgShort = targetArg.substring(2);
        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getLocaleCache();

        final String locale = localeCache.getLocale(id);

        if (localeCache.hasLocaleOverride(id))
            Macros.sendLocalizedMessage(SUCCESS_OVERRIDE, sender, targetArgShort, locale);
        else
            Macros.sendLocalizedMessage(SUCCESS, sender, targetArgShort, locale);

        return true;
    }

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
