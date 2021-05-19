package com.hippout.hippoutlocalizationlib.locale;

import com.hippout.hippoutlocalizationlib.Configuration;
import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.events.*;
import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;

import javax.annotation.*;
import java.util.*;

/**
 * A cache for Locales.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class LocaleCache {
    public final boolean ENABLE_LOCALE_OVERRIDES;

    private final HippOutLocalizationLib plugin;
    private final Map<UUID, String> localeMap;
    private final Map<UUID, String> localeOverrideMap;

    /**
     * Constructs a LocaleCache with the given plugin.
     *
     * @param plugin The instance of HippOutLocalizationLib.
     * @throws NullPointerException if plugin is null.
     * @since 1.0.0
     */
    public LocaleCache(@Nonnull HippOutLocalizationLib plugin)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        localeMap = new HashMap<>();

        ENABLE_LOCALE_OVERRIDES = plugin.getConfiguration().ENABLE_LOCALE_OVERRIDES;
        if (ENABLE_LOCALE_OVERRIDES)
            localeOverrideMap = new HashMap<>();
        else
            localeOverrideMap = new NullMap<>();
    }

    /**
     * Constructs a LocaleCache with the given plugin and the given Collection of initial Players.
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
     * Returns the Locale used by a given UUID. Will return their override Locale if they have one. If they have no
     * Locale, returns DEFAULT_LOCALE.
     *
     * @param id UUID to check.
     * @return The Locale String of the given UUID.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    @Nonnull
    public String getLocale(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");

        final String defaultLocale = HippOutLocalizationLib.getPlugin().getConfiguration().DEFAULT_LOCALE;

        return localeOverrideMap.getOrDefault(id, localeMap.getOrDefault(id, defaultLocale));
    }

    /**
     * Returns the Locale used by a given UUID, ignoring any override. If they have no Locale, returns DEFAULT_LOCALE.
     *
     * @param id UUID to check.
     * @return The Locale String of the given UUID.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    @Nonnull
    @SuppressWarnings("unused")
    public String getLocaleNoOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");

        final String defaultLocale = HippOutLocalizationLib.getPlugin().getConfiguration().DEFAULT_LOCALE;

        return localeMap.getOrDefault(id, defaultLocale);
    }

    /**
     * Returns the Locale Override for the given UUID.
     *
     * @param id UUID to check.
     * @return The Locale Override for the given UUID.
     * @throws NullPointerException    if UUID is null.
     * @throws IllegalStateException   if Locale Overrides are disabled.
     * @throws LocaleNotFoundException if a Locale Override for the given UUID could not be found.
     * @since 1.0.0
     */
    @Nonnull
    @SuppressWarnings("unused")
    public String getLocaleOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Locale Overrides are disabled.");

        final String locale = localeOverrideMap.get(id);

        if (locale == null)
            throw new LocaleNotFoundException("Locale Override for UUID " + id + " could not be found.");

        return locale;
    }

    /**
     * Returns whether this LocaleCache contains a standard Locale for the given UUID.
     *
     * @param id UUID to check
     * @return True if there is a Locale, false otherwise.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public boolean hasLocale(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        return localeMap.containsKey(id);
    }

    /**
     * Returns whether this LocaleCache contains a Locale Override for the given UUID.
     *
     * @param id UUID to check
     * @return True if the UUID has an override, false otherwise.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public boolean hasLocaleOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        return localeOverrideMap.containsKey(id);
    }

    /**
     * Returns whether this LocaleCache contains either a standard Locale or a Locale Override for the given UUID.
     *
     * @param id UUID to check
     * @return True if there is a Locale or Override, false otherwise.
     * @throws NullPointerException if UUID is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public boolean hasLocaleOrOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        return localeMap.containsKey(id) || localeOverrideMap.containsKey(id);
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
    @SuppressWarnings("unused")
    public void setLocaleOverride(@Nonnull UUID id, @Nonnull String locale)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        Objects.requireNonNull(locale, "Locale cannot be null.");
        if (plugin.getConfiguration().API_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(locale);

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot set Locale Overrides as they are disabled.");

        final String oldLocale = getLocale(id);

        localeOverrideMap.put(id, locale);

        if (!oldLocale.equals(locale))
            Bukkit.getPluginManager().callEvent(new LocaleCacheChangeEvent(id, oldLocale, locale));
    }

    /**
     * Removes the Locale Override from a given UUID.
     *
     * @param id UUID to remove the Locale Override of.
     * @throws NullPointerException  if UUID is null.
     * @throws IllegalStateException if Locale Overrides are disabled.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public void removeLocaleOverride(@Nonnull UUID id)
    {
        Objects.requireNonNull(id, "UUID cannot be null.");
        if (!localeOverrideMap.containsKey(id))
            throw new IllegalStateException("Tried to remove UUID that was not present: " + id);

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot remove Locale Overrides as they are disabled.");

        final String oldLocale = getLocale(id);

        localeOverrideMap.remove(id);

        final String newLocale = getLocale(id);

        if (!oldLocale.equals(newLocale))
            Bukkit.getPluginManager().callEvent(new LocaleCacheChangeEvent(id, oldLocale, newLocale));
    }

    /**
     * Clears all Locale overrides currently stored.
     *
     * @throws IllegalStateException if Locale Overrides are disabled.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public void clearLocaleOverrides()
    {
        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot clear Locale Overrides as they are disabled.");

        localeOverrideMap.clear();
    }

    /**
     * Reads a map of Locale Overrides from the given ConfigurationSection. Clears the localeOverrideMap before loading.
     *
     * @param configurationSection ConfigurationSection to load from.
     * @throws NullPointerException  if configurationSection is null.
     * @throws IllegalStateException if ENABLE_LOCALE_OVERRIDES is disabled in plugin Configuration.
     * @since 1.0.0
     */
    public void loadOverrides(@Nonnull ConfigurationSection configurationSection)
    {
        Objects.requireNonNull(configurationSection, "ConfigurationSection cannot be null.");

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot load Locale Overrides from a file while Locale Overrides are " +
                    "disabled.");

        final Set<String> keys = configurationSection.getKeys(false);

        localeOverrideMap.clear();

        for (String uuid : keys) {
            try {
                final UUID id = UUID.fromString(uuid);
                final String locale = Configuration.loadLocale(configurationSection, uuid);

                setLocaleOverride(id, locale);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(String.format("Error loading UUID %s from section %s. Ignored.", uuid,
                        configurationSection.getName()));
                e.printStackTrace();
            } catch (LocaleFormatException e) {
                plugin.getLogger().warning(String.format("Error loading Locale with UUID %s from section %s. Ignored.",
                        uuid, configurationSection.getName()));
            }
        }
    }

    /**
     * Writes the current LocaleCache to the given ConfigurationSection.
     *
     * @throws NullPointerException  if configurationSection is null.
     * @throws IllegalStateException if ENABLE_LOCALE_OVERRIDES is disabled in plugin Configuration.
     * @since 1.0.0
     */
    public void writeOverrides(@Nonnull ConfigurationSection configurationSection)
    {
        Objects.requireNonNull(configurationSection, "ConfigurationSection cannot be null.");

        if (!ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot write Locale Overrides to a file while Locale Overrides are " +
                    "disabled.");

        for (Map.Entry<UUID, String> entry : localeOverrideMap.entrySet()) {
            configurationSection.set(entry.getKey().toString(), entry.getValue());
        }
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

        final String oldLocale = getLocale(id);

        localeMap.put(id, locale);

        if (!hasLocaleOverride(id) && !oldLocale.equals(locale))
            Bukkit.getPluginManager().callEvent(new LocaleCacheChangeEvent(id, oldLocale, locale));
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
        if (!localeMap.containsKey(id))
            throw new IllegalStateException("Tried to remove UUID that was not present: " + id);

        final String oldLocale = getLocale(id);

        localeMap.remove(id);

        final String newLocale = getLocale(id);

        if (!hasLocaleOverride(id) && !oldLocale.equals(newLocale))
            Bukkit.getPluginManager().callEvent(new LocaleCacheChangeEvent(id, oldLocale, newLocale));
    }
}