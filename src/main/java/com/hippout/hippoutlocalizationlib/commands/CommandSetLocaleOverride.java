package com.hippout.hippoutlocalizationlib.commands;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.api.*;
import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.util.*;

import javax.annotation.*;
import java.util.*;

/**
 * A Command to set the Locale Override of a UUID or Player.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class CommandSetLocaleOverride implements CommandExecutor, TabCompleter {

    private final NamespacedKey USAGE, SUCCESS, INVALID_FORMAT_LOCALE;

    /**
     * Constructs a CommandSetLocaleOverride.
     *
     * @since 1.0.0
     */
    public CommandSetLocaleOverride()
    {
        final KeyRegistry keyRegistry = HippOutLocalizationLib.getKeyRegistry();

        USAGE = keyRegistry.COM_SETLOCALEOVERRIDE_USAGE;
        SUCCESS = keyRegistry.COM_SETLOCALEOVERRIDE_SUCCESS;

        INVALID_FORMAT_LOCALE = keyRegistry.INVALID_FORMAT_LOCALE;
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
        if (args.length != 2) {
            Macros.sendLocalizedMessage(USAGE, sender);
            return true;
        }

        final String targetArg = args[0];
        final String localeArg = args[1];

        // Parse UUID
        final UUID id;
        try {
            id = TargetArgs.parseTargetArg(targetArg, Macros.getLocale(sender));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        // Validate Locale
        try {
            ValidationUtil.validateLocale(localeArg, Macros.getLocalizedMessage(INVALID_FORMAT_LOCALE, sender));
        } catch (LocaleFormatException e) {
            sender.sendMessage(e.getMessage());
            return true;
        }

        HippOutLocalizationLib.getPlugin().getLocaleCache().setLocaleOverride(id, localeArg);
        Macros.sendLocalizedMessage(SUCCESS, sender, targetArg.substring(2), localeArg);
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

        switch (args.length) {
            case 1:
                token = latestArg;
                suggestions.addAll(TargetArgs.tabCompleteTargetArg(latestArg));
                break;
            case 2:
                suggestions.addAll(HippOutLocalizationLib.getPlugin().getLanguageHandler().getRegisteredLocales());
                token = latestArg;
                break;
            default:
                return suggestions;
        }

        final List<String> outList = new LinkedList<>();
        StringUtil.copyPartialMatches(token, suggestions, outList);
        return outList;
    }
}
