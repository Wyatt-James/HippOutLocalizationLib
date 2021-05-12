package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;

import javax.annotation.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

/**
 * A Configuration class for HippOutLocalizationLib
 *
 * @author Wyatt Kalmer
 */
public class Configuration {
    private static final String VERSION_REGEX = "^[0-9]{1,2}.[0-9]{1,2}.[0-9]{1,3}$";
    private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEX);

    private static final String ERR_CONFIG_NOT_FOUND = "config_version not found in config.yml. Please delete" +
            " config.yml and restart the server to regenerate the config.";
    private static final String ERR_CONFIG_INVALID_FORMAT = "config_version has an invalid format. Please delete" +
            " config.yml and restart the server to regenerate the config.";

    private static final String ERR_FAILSAFE_MESSAGE_NOT_FOUND = "failsafe_message could not be found. A default message" +
            " can be used but a custom one can be added to config.yml.";

    private static final String ERR_LOCALE_NOT_FOUND = "Locale %s could not be found. Please delete config.yml and" +
            " restart the server to regenerate the config.";
    private static final String ERR_LOCALE_INVALID_FORMAT = "Locale %s has an invalid format. Please fix it in" +
            " accordance with the readme.";

    private static final String DEFAULT_FAILSAFE_MESSAGE = "The requested message could not be loaded.";

    // --------------- Instance Variables ---------------

    private final HippOutLocalizationLib plugin;

    public final String CONFIG_VERSION;
    public final String FAILSAFE_MESSAGE;
    public final String DEFAULT_LOCALE, CONSOLE_LOCALE, REMOTE_CONSOLE_LOCALE;

    public final boolean API_REGEX_LOCALE_TESTS;
    public final boolean INTERNAL_REGEX_LOCALE_TESTS;
    public final boolean REMOVE_DISCONNECTED_PLAYER_LOCALES;

    /**
     * Constructs a Configuration and loads from config.yml.
     *
     * @param plugin HippOutLocalizationLib instance.
     * @throws NullPointerException  if plugin is null.
     * @throws IllegalStateException if config_version is in an invalid format.
     * @throws LocaleFormatException if any of the Locales in config.yml have an invalid format.
     */
    Configuration(@Nonnull HippOutLocalizationLib plugin)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");

        final FileConfiguration rootConfig = this.plugin.getConfig();
        final ConfigurationSection debugConfig = rootConfig.getConfigurationSection("debug");

        // Load config_version
        CONFIG_VERSION = Objects.requireNonNull(rootConfig.getString("config_version"), ERR_CONFIG_NOT_FOUND);
        if (!VERSION_PATTERN.matcher(CONFIG_VERSION).matches())
            throw new IllegalStateException(ERR_CONFIG_INVALID_FORMAT);

        // Load failsafe_message
        String failsafeMessage = rootConfig.getString("failsafe_message");
        if (failsafeMessage == null) {
            failsafeMessage = DEFAULT_FAILSAFE_MESSAGE;
            plugin.getLogger().log(Level.WARNING, ERR_FAILSAFE_MESSAGE_NOT_FOUND);
        }

        FAILSAFE_MESSAGE = failsafeMessage;

        // Load default and console Locales
        DEFAULT_LOCALE = loadLocale(rootConfig, "default_locale");
        CONSOLE_LOCALE = loadLocale(rootConfig, "console_locale");
        REMOTE_CONSOLE_LOCALE = loadLocale(rootConfig, "remote_console_locale");

        API_REGEX_LOCALE_TESTS = debugConfig.getBoolean("api_regex_locale_tests", false);
        INTERNAL_REGEX_LOCALE_TESTS = debugConfig.getBoolean("internal_regex_locale_tests", false);
        REMOVE_DISCONNECTED_PLAYER_LOCALES = debugConfig.getBoolean("remove_disconnected_player_locales", true);
    }

    /**
     * Helper method for loading a Locale.
     *
     * <p>
     * Technically checks varName for null twice but it's not really an issue.
     * </p>
     *
     * @param configurationSection FileConfiguration containing the Locale String in its top level.
     * @param varName              Locale Variable Name in the FileConfiguration.
     * @return The verified Locale.
     * @throws NullPointerException  if configurationSection or varName is null.
     * @throws NullPointerException  if the requested variable could not be found.
     * @throws LocaleFormatException if the requested variable was not a valid Locale as per the regex in
     *                               ValidationUtil.
     */
    @Nonnull
    private static String loadLocale(@Nonnull ConfigurationSection configurationSection, @Nonnull String varName)
    {
        Objects.requireNonNull(configurationSection, "ConfigurationSection cannot be null.");
        Objects.requireNonNull(varName, "Variable Name cannot be null.");

        final String locale = Objects.requireNonNull(configurationSection.getString(varName),
                String.format(ERR_LOCALE_NOT_FOUND, varName));

        ValidationUtil.validateLocale(locale, String.format(ERR_LOCALE_INVALID_FORMAT, varName)
                + " Yours: %s");

        return locale;
    }
}
