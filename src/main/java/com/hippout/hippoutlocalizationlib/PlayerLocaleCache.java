package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;

import javax.annotation.*;
import java.util.*;

/**
 * A cache for Player Locales.
 *
 * @author Wyatt Kalmer
 */
public class PlayerLocaleCache {
    private static final String ERROR_LOCALE_NOT_FOUND = "Could not find Locale for UUID %s. They are offline or they" +
            " have left and config.yml/debug.remove_disconnected_player_locales is set to true.";

    private final HippOutLocalizationLib plugin;
    private final Map<UUID, String> playerLocaleMap;

    /**
     * Constructs a Player Locale Cache with the given plugin.
     *
     * @param plugin The instance of HippOutLocalizationLib.
     * @throws NullPointerException if plugin is null.
     */
    PlayerLocaleCache(@Nonnull HippOutLocalizationLib plugin)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        playerLocaleMap = new HashMap<>();
    }

    /**
     * Returns the Locale used by a given UUID.
     *
     * @param id UUID to check.
     * @return The Locale String of the given Player, or null if there is none.
     * @throws NullPointerException if UUID is null.
     * @throws NullPointerException if the UUID is not present. This will depend on the configuration
     *                              config.yml/debug.remove_disconnected_player_locales.
     */
    @Nonnull
    public String getPlayerLocale(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");

        return Objects.requireNonNull(playerLocaleMap.get(id), String.format(ERROR_LOCALE_NOT_FOUND, id));
    }

    /**
     * Returns whether this PlayerLocaleCache contains information for the given UUID.
     *
     * @param id UUID to check
     * @throws NullPointerException if UUID is null.
     */
    public boolean isLocaleCached(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        return playerLocaleMap.containsKey(id);
    }

    /**
     * Sets the Locale for a given UUID.
     *
     * @param id     UUID to set the Locale of.
     * @param locale Locale to set the UUID to.
     * @throws NullPointerException  if UUID or Locale are null.
     * @throws LocaleFormatException if INTERNAL_REGEX_LOCALE_TESTS are enabled and locale is not a valid format.
     */
    void setPlayerLocale(@Nonnull UUID id, @Nonnull String locale)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        Objects.requireNonNull(locale, "Locale cannot be null.");
        if (plugin.getConfiguration().INTERNAL_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(locale);

        playerLocaleMap.put(id, locale);
    }

    /**
     * Removes the Locale from a given UUID.
     *
     * @param id UUID to remove the Locale of.
     * @throws NullPointerException if UUID is null.
     */
    void removePlayerLocale(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        if (!playerLocaleMap.containsKey(id))
            throw new IllegalStateException("Tried to remove UUID that was not present: " + id);

        playerLocaleMap.remove(id);
    }
}