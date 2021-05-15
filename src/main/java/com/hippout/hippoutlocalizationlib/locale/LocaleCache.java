package com.hippout.hippoutlocalizationlib.locale;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.entity.*;

import javax.annotation.*;
import java.util.*;

/**
 * A cache for Player Locales.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class LocaleCache {
    private static final String ERROR_LOCALE_NOT_FOUND = "Could not find Locale for UUID %s. They are offline or they" +
            " have left and config.yml/debug.remove_disconnected_player_locales is set to true.";

    private final HippOutLocalizationLib plugin;
    private final Map<UUID, String> playerLocaleMap;
    public final boolean ENABLE_LOCALE_OVERRIDES;
    private final Map<UUID, String> playerLocaleOverrideMap;

    /**
     * Constructs a Player Locale Cache with the given plugin.
     *
     * @param plugin The instance of HippOutLocalizationLib.
     * @throws NullPointerException if plugin is null.
     * @since 1.0.0
     */
    public LocaleCache(@Nonnull HippOutLocalizationLib plugin)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        playerLocaleMap = new HashMap<>();

        ENABLE_LOCALE_OVERRIDES = plugin.getConfiguration().ENABLE_LOCALE_OVERRIDES;
        if (ENABLE_LOCALE_OVERRIDES)
            playerLocaleOverrideMap = new HashMap<>();
        else
            playerLocaleOverrideMap = new NullMap<>();
    }

    /**
     * Constructs a Player Locale Cache with the given plugin.
     *
     * @param plugin         The instance of HippOutLocalizationLib.
     * @param initialPlayers Collection of Players to populate this LocaleCache with.
     * @throws NullPointerException if plugin or initialPlayers is null.
     * @since 1.0.0
     */
    public LocaleCache(@Nonnull HippOutLocalizationLib plugin, @Nonnull Collection<? extends Player> initialPlayers)
    {
        this(plugin);

        Objects.requireNonNull(initialPlayers, "Initial Players List cannot be null.");
        initialPlayers.forEach(p -> setLocale(p.getUniqueId(), p.getLocale()));
    }

    /**
     * Returns the Locale used by a given UUID. Will return their override Locale if they have one.
     *
     * @param id UUID to check.
     * @return The Locale String of the given Player.
     * @throws NullPointerException    if UUID is null.
     * @throws LocaleNotFoundException if the UUID is not present. This will depend on the configuration
     *                                 config.yml/debug.remove_disconnected_player_locales.
     * @since 1.0.0
     */
    @Nonnull
    public String getLocale(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");

        final String playerLocale = playerLocaleOverrideMap.getOrDefault(id, playerLocaleMap.get(id));

        if (playerLocale == null)
            throw new LocaleNotFoundException("Locale for UUID " + id + " could not be found.");

        return playerLocale;
    }

    /**
     * Returns the Locale used by a given UUID, ignoring any override.
     *
     * @param id UUID to check.
     * @return The Locale String of the given Player, or null if there is none.
     * @throws NullPointerException if UUID is null.
     * @throws NullPointerException if the UUID is not present. This will depend on the configuration
     *                              config.yml/debug.remove_disconnected_player_locales.
     * @since 1.0.0
     */
    @Nonnull
    public String getLocaleNoOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");

        final String playerLocale = playerLocaleMap.get(id);

        if (playerLocale == null)
            throw new LocaleNotFoundException("Locale for UUID " + id + " could not be found, excluding overrides.");

        return playerLocale;
    }

    /**
     * Returns the Locale Override for the given UUID.
     *
     * @param id UUID to check.
     * @return The Locale Override for the given UUID.
     * @throws NullPointerException    if UUID is null.
     * @throws IllegalStateException   if Player Locale Overrides are disabled.
     * @throws LocaleNotFoundException if a Locale Override for the given UUID could not be found.
     * @since 1.0.0
     */
    public String getLocaleOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Player Locale Overrides are disabled.");

        final String playerLocale = playerLocaleOverrideMap.get(id);

        if (playerLocale == null)
            throw new LocaleNotFoundException("Locale Override for UUID " + id + " could not be found.");

        return playerLocale;
    }

    /**
     * Returns whether this LocaleCache contains information for the given UUID.
     *
     * @param id UUID to check
     * @return True if the UUID is cached, false otherwise.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    public boolean isLocaleCached(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        return playerLocaleMap.containsKey(id);
    }

    /**
     * Returns whether this LocaleCache contains a Locale Override for the given UUID.
     *
     * @param id UUID to check
     * @return True if the UUID has an override, false otherwise.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    public boolean hasLocaleOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        return playerLocaleOverrideMap.containsKey(id);
    }

    /**
     * Sets the Locale Override for a given UUID.
     *
     * @param id     UUID to set the Locale Override of.
     * @param locale Locale Override to set the UUID to.
     * @throws NullPointerException  if UUID or Locale are null.
     * @throws LocaleFormatException if INTERNAL_REGEX_LOCALE_TESTS are enabled and locale is not a valid format.
     * @throws IllegalStateException if Locale Overrides are disabled.
     * @since 1.0.0
     */
    public void setLocaleOverride(@Nonnull UUID id, @Nonnull String locale)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        Objects.requireNonNull(locale, "Locale cannot be null.");
        if (plugin.getConfiguration().API_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(locale);

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot set Locale Overrides as they are disabled.");

        playerLocaleOverrideMap.put(id, locale);
    }

    /**
     * Removes the Locale Override from a given UUID.
     *
     * @param id UUID to remove the Locale Override of.
     * @throws NullPointerException  if UUID is null.
     * @throws IllegalStateException if Locale Overrides are disabled.
     * @since 1.0.0
     */
    public void removeLocaleOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        if (!playerLocaleOverrideMap.containsKey(id))
            throw new IllegalStateException("Tried to remove UUID that was not present: " + id);

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot remove Locale Overrides as they are disabled.");

        playerLocaleOverrideMap.remove(id);
    }

    /**
     * Sets the Locale for a given UUID.
     *
     * @param id     UUID to set the Locale of.
     * @param locale Locale to set the UUID to.
     * @throws NullPointerException  if UUID or Locale are null.
     * @throws LocaleFormatException if INTERNAL_REGEX_LOCALE_TESTS are enabled and locale is not a valid format.
     * @since 1.0.0
     */
    void setLocale(@Nonnull UUID id, @Nonnull String locale)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        Objects.requireNonNull(locale, "Locale cannot be null.");
        if (plugin.getConfiguration().INTERNAL_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(locale);

        playerLocaleMap.put(id, locale);
    }

    /**
     * Removes the Locale from a given UUID. Does not alter Overrides.
     *
     * @param id UUID to remove the Locale of.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    void removeLocale(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        if (!playerLocaleMap.containsKey(id))
            throw new IllegalStateException("Tried to remove UUID that was not present: " + id);

        playerLocaleMap.remove(id);
    }
}