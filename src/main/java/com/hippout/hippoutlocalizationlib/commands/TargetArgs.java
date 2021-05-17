package com.hippout.hippoutlocalizationlib.commands;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.api.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import javax.annotation.*;
import java.util.*;
import java.util.regex.*;

/**
 * Class for parsing Player/UUID targets.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class TargetArgs {
    public static final String TARGET_ARG_REGEX_PLAYER = "^(p):([0-9A-Za-z_]{3,16}$)";
    public static final Pattern TARGET_ARG_PATTERN_PLAYER = Pattern.compile(TARGET_ARG_REGEX_PLAYER);

    public static final String TARGET_ARG_REGEX_UUID = "^(u):(([0-9a-z]{32})|([0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-" +
            "[0-9a-z]{4}-[0-9a-z]{12}))$";
    public static final Pattern TARGET_ARG_PATTERN_UUID = Pattern.compile(TARGET_ARG_REGEX_UUID);

    static final List<String> TARGET_TYPES_SUGGESTIONS = new ArrayList<>();
    private static final List<String> TARGET_TYPES = new ArrayList<>(Arrays.asList("p", "u"));

    static {
        TARGET_TYPES.forEach(targetType -> TARGET_TYPES_SUGGESTIONS.add(targetType + ":"));
    }

    /**
     * Parses a UUID from the given TargetArg String. Can parse Player names and direct UUIDs in both full and
     * trimmed form.
     *
     * @param errorMessageLocale Locale of the Error Messages. Does not affect parameter checks.
     * @param targetArg          TargetArg to parse.
     * @return The parsed UUID.
     * @throws NullPointerException     if sender or targetArg is null.
     * @throws IllegalArgumentException if targetArg is empty.
     * @throws IllegalArgumentException if targetArg is not valid UUID or Player Name format.
     * @throws IllegalArgumentException if the parsed Player could not be found.
     * @since 1.0.0
     */
    static UUID parseTargetArg(@Nonnull String targetArg, @Nonnull String errorMessageLocale)
    {
        Objects.requireNonNull(errorMessageLocale, "Error Message Locale cannot be null.");
        if (errorMessageLocale.isEmpty()) throw new IllegalArgumentException("Error Message Locale cannot be empty.");
        if (HippOutLocalizationLib.getPlugin().getConfiguration().API_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(errorMessageLocale);

        Objects.requireNonNull(targetArg, "TargetArg cannot be null.");
        if (targetArg.isEmpty()) throw new IllegalArgumentException("TargetArg cannot be empty.");

        // Get Message Keys
        final KeyRegistry keyRegistry = HippOutLocalizationLib.getKeyRegistry();
        final NamespacedKey INVALID_TARGET_TYPE = keyRegistry.TARGETARGS_INVALID_TYPE;
        final NamespacedKey INVALID_TARGET_FORMAT = keyRegistry.TARGETARGS_INVALID_FORMAT;
        final NamespacedKey PLAYER_NOT_FOUND = keyRegistry.PLAYER_NOT_FOUND;

        final char matcherMode = targetArg.charAt(0);

        switch (matcherMode) {
            case 'p': {
                final Matcher matcher = TARGET_ARG_PATTERN_PLAYER.matcher(targetArg);

                if (!matcher.matches())
                    throw new IllegalArgumentException(String.format(Macros.getLocalizedMessage(INVALID_TARGET_FORMAT,
                            errorMessageLocale), targetArg));

                final String playerName = matcher.group(2);
                final Player player = Bukkit.getPlayer(playerName);

                if (player == null)
                    throw new IllegalArgumentException(String.format(Macros.getLocalizedMessage(PLAYER_NOT_FOUND,
                            errorMessageLocale), playerName));
                else
                    return player.getUniqueId();
            }

            case 'u': {
                final Matcher matcher = TARGET_ARG_PATTERN_UUID.matcher(targetArg);

                if (!matcher.matches())
                    throw new IllegalArgumentException(String.format(Macros.getLocalizedMessage(INVALID_TARGET_FORMAT,
                            errorMessageLocale), targetArg));

                final String matcherOutput = matcher.group(2);

                final UUID id;
                if (matcherOutput.length() == 36)
                    id = UUID.fromString(matcherOutput);
                else
                    id = UUID.fromString(MiscUtil.convertTrimmedUuidToFull(matcherOutput));

                return id;
            }
            default:
                throw new IllegalArgumentException(String.format(Macros.getLocalizedMessage(INVALID_TARGET_TYPE,
                        errorMessageLocale), matcherMode));
        }
    }

    /**
     * Tab Completer for a TargetArg.
     *
     * @param targetArgString TargetArg String to parse.
     * @return A List of potential Tab Completions. Must still copy partial matches.
     * @api.Note Does not parse for valid UUIDs.
     * @since 1.0.0
     */
    static List<String> tabCompleteTargetArg(@Nonnull String targetArgString)
    {
        final List<String> suggestions = new LinkedList<>();

        if (targetArgString.length() <= 1) {
            suggestions.addAll(TARGET_TYPES_SUGGESTIONS);
            return suggestions;
        }

        final char targetType = targetArgString.charAt(0);

        if (targetType == 'p') {
            final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            players.forEach(p -> suggestions.add("p:" + p.getName()));
        }

        return suggestions;
    }
}
