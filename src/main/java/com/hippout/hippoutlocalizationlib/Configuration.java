package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;

/**
 * A Configuration class for HippOutLocalizationLib. Contains any internal-use flags and variables.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
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

    public static final String LOCALE_CACHE_FILE_NAME = "locale_overrides.yml";
    public static final String LOCALE_CACHE_HEADER = "A Map of UUIDs and their Locale Cache Overrides.";

    // --------------- Instance Variables ---------------

    // Root
    public final String CONFIG_VERSION;

    private final List<String> LANGUAGE_FILE_DEFINITIONS;

    public final boolean SEND_BROADCASTS_TO_CONSOLE;
    public final boolean ENABLE_LOCALE_OVERRIDES;
    public final boolean SAVE_AND_LOAD_LOCALE_OVERRIDES;

    // Defaults
    public final String DEFAULT_LOCALE, CONSOLE_LOCALE, REMOTE_CONSOLE_LOCALE;
    public final String FAILSAFE_MESSAGE;

    // Loading
    final boolean SUPPRESS_SECTION_WARNINGS;

    // Debug
    public final boolean API_REGEX_LOCALE_TESTS;
    public final boolean INTERNAL_REGEX_LOCALE_TESTS;
    public final boolean REMOVE_DISCONNECTED_PLAYER_LOCALES;

    /**
     * Constructs a Configuration and loads from config.yml.
     *
     * @param plugin HippOutLocalizationLib instance.
     * @throws NullPointerException          if plugin is null.
     * @throws IllegalStateException         if config_version is in an invalid format.
     * @throws IllegalStateException         if SAVE_LOCALE_OVERRIDES is true but ENABLE_LOCALE_OVERRIDES is false.
     * @throws LocaleFormatException         if any of the Locales in config.yml have an invalid format.
     * @throws IOException                   if Bukkit fails to reload the default configuration file config.yml.
     * @throws InvalidConfigurationException if config.yml is not in valid YAML format.
     * @api.Note Creating a Configuration object overwrites whatever is loaded in this plugin's FileConfiguration. Use
     * with care.
     * @since 1.0.0
     */
    Configuration(@Nonnull HippOutLocalizationLib plugin) throws IOException, InvalidConfigurationException
    {
        loadConfigFile(); // Exceptions can be thrown here.

        final FileConfiguration rootConfig = plugin.getConfig();
        final ConfigurationSection defaultsSection = rootConfig.getConfigurationSection("defaults");
        final ConfigurationSection loadingSection = rootConfig.getConfigurationSection("loading");
        final ConfigurationSection debugSection = rootConfig.getConfigurationSection("debug");

        // Load config_version
        CONFIG_VERSION = Objects.requireNonNull(rootConfig.getString("config_version"), ERR_CONFIG_NOT_FOUND);
        if (!VERSION_PATTERN.matcher(CONFIG_VERSION).matches())
            throw new IllegalStateException(ERR_CONFIG_INVALID_FORMAT);

        LANGUAGE_FILE_DEFINITIONS = Objects.requireNonNull(rootConfig.getStringList("language_files"), "Language File" +
                " Definitons cannot be null.");

        SEND_BROADCASTS_TO_CONSOLE = rootConfig.getBoolean("send_broadcasts_to_console", true);
        ENABLE_LOCALE_OVERRIDES = rootConfig.getBoolean("enable_locale_overrides");
        SAVE_AND_LOAD_LOCALE_OVERRIDES = rootConfig.getBoolean("save_and_load_locale_overrides_to_file");

        if (SAVE_AND_LOAD_LOCALE_OVERRIDES && !ENABLE_LOCALE_OVERRIDES)
            throw new IllegalStateException("Cannot have Locale Override saving enabled while Locale Overrides are " +
                    "disabled.");

        // Load failsafe_message
        String failsafeMessage = defaultsSection.getString("failsafe_message");
        if (failsafeMessage == null) {
            failsafeMessage = DEFAULT_FAILSAFE_MESSAGE;
            plugin.getLogger().log(Level.WARNING, ERR_FAILSAFE_MESSAGE_NOT_FOUND);
        }

        FAILSAFE_MESSAGE = failsafeMessage;

        // Load default and console Locales
        DEFAULT_LOCALE = loadLocale(defaultsSection, "default_locale");
        CONSOLE_LOCALE = loadLocale(defaultsSection, "console_locale");
        REMOTE_CONSOLE_LOCALE = loadLocale(defaultsSection, "remote_console_locale");

        SUPPRESS_SECTION_WARNINGS = loadingSection.getBoolean("suppress_section_warnings");

        API_REGEX_LOCALE_TESTS = debugSection.getBoolean("api_regex_locale_tests", false);
        INTERNAL_REGEX_LOCALE_TESTS = debugSection.getBoolean("internal_regex_locale_tests", false);
        REMOVE_DISCONNECTED_PLAYER_LOCALES = debugSection.getBoolean("remove_disconnected_player_locales", true);
    }

    /**
     * Gets a List of Language File definitions.
     *
     * @return A copy of the List of Language File Definitions.
     * @since 1.0.0
     */
    @Nonnull
    public List<String> getLanguageFileDefinitions()
    {
        return new ArrayList<>(LANGUAGE_FILE_DEFINITIONS);
    }

    /**
     * Helper method for loading a Locale.
     *
     * @param configurationSection FileConfiguration containing the Locale String in its top level.
     * @param key                  Locale Variable Name in the FileConfiguration.
     * @return The verified Locale.
     * @throws NullPointerException  if configurationSection or key is null.
     * @throws NullPointerException  if the requested variable could not be found.
     * @throws LocaleFormatException if the requested variable was not a valid Locale as per the regex in
     *                               ValidationUtil.
     * @since 1.0.0
     */
    @Nonnull
    public static String loadLocale(@Nonnull ConfigurationSection configurationSection, @Nonnull String key)
    {
        Objects.requireNonNull(configurationSection, "ConfigurationSection cannot be null.");
        Objects.requireNonNull(key, "Key cannot be null.");

        final String locale = Objects.requireNonNull(configurationSection.getString(key),
                String.format(ERR_LOCALE_NOT_FOUND, key));

        ValidationUtil.validateLocale(locale, String.format(ERR_LOCALE_INVALID_FORMAT, key)
                + " Yours: %s");

        return locale;
    }

    /**
     * Reloads the default Configuration file config.yml from HippOutLocalizationLib's data folder.
     *
     * @throws IOException                   if Bukkit fails to find or load config.yml.
     * @throws InvalidConfigurationException if config.yml is not in valid YAML format.
     * @since 1.0.0
     */
    private static void loadConfigFile() throws IOException, InvalidConfigurationException
    {
        final HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();
        final String configPath = plugin.getDataFolder().getPath() + File.separator + "config.yml";
        final FileConfiguration config = HippOutLocalizationLib.getPlugin().getConfig();

        config.load(configPath);
    }
}
