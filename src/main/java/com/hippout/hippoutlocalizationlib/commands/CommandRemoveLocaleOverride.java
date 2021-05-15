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
 * A Command to remove the Locale Override of a UUID or Player.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class CommandRemoveLocaleOverride implements CommandExecutor, TabCompleter {

    private final NamespacedKey USAGE, SUCCESS, ERROR_NO_OVERRIDE;

    public CommandRemoveLocaleOverride()
    {
        final KeyRegistry keyRegistry = HippOutLocalizationLib.getKeyRegistry();

        USAGE = keyRegistry.COM_REMLOCALEOVERRIDE_USAGE;
        SUCCESS = keyRegistry.COM_REMLOCALEOVERRIDE_SUCCESS;
        ERROR_NO_OVERRIDE = keyRegistry.COM_REMLOCALEOVERRIDE_NO_OVERRIDE;
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

        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getPlayerLocaleCache();
        if (!localeCache.hasLocaleOverride(id)) {
            Macros.sendLocalizedMessage(ERROR_NO_OVERRIDE, sender, targetArg.substring(2));
            return true;
        }

        localeCache.removeLocaleOverride(id);
        Macros.sendLocalizedMessage(SUCCESS, sender, targetArg.substring(2));
        return true;
    }

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
