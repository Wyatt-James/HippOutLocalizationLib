package com.hippout.hippoutlocalizationlib.api;

import com.hippout.hippoutlocalizationlib.Configuration;
import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.language.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * A convenient way to load language files into HippOutLocalizationLib.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class LanguageLoader {
    private final JavaPlugin plugin;
    private final boolean suppressSectionWarnings;
    private final String languageDirectoryName;
    private final String languageDirectory;

    /**
     * Constructs a LanguageLoader with the given JavaPlugin as its parent.
     *
     * @param plugin                Parent JavaPlugin of this LanguageLoader. See API Note.
     * @param languageDirectoryName Directory to load language resources from.
     * @throws NullPointerException if plugin or languageDirectoryName is null.
     * @api.Note If languageDirectoryName is empty, will load from main data folder directory. This is discouraged
     * however.
     * @api.Note Do NOT use HippOutLocalizationLib when using this as an API. For internal use only.
     * @since 1.0.0
     */
    public LanguageLoader(@Nonnull JavaPlugin plugin, @Nonnull String languageDirectoryName, boolean suppressSectionWarnings)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        this.languageDirectoryName = Objects.requireNonNull(languageDirectoryName, "Language Directory Name" +
                " cannot be null.");
        this.languageDirectory = plugin.getDataFolder() + File.separator + languageDirectoryName;
        this.suppressSectionWarnings = suppressSectionWarnings;
    }

    /**
     * Loads the requested file from the plugin's specified language directory and adds its messages to
     * HippOutLocalizationLib with the file's defined Locales.
     *
     * @param fileName Language file to load
     * @return A List of all generated NamespacedKeys which were successfully added to the LanguageHandler.
     * @throws NullPointerException          if fileName is null.
     * @throws IllegalArgumentException      if fileName is empty.
     * @throws IOException                   if config.load fails to load the requested file.
     * @throws InvalidConfigurationException if config.load can load the requested file but it is not a valid YAML file.
     * @since 1.0.0
     */
    public List<NamespacedKey> loadLanguageFile(@Nonnull String fileName) throws IOException, InvalidConfigurationException
    {
        Objects.requireNonNull(fileName, "File Name cannot be null.");
        if (fileName.isEmpty()) throw new IllegalArgumentException("File Name cannot be empty.");

        final Logger logger = plugin.getLogger();
        final YamlConfiguration fc = loadLanguageConfig(fileName + ".yml");

        final String[] locales = fc.getStringList("config.locales").toArray(new String[0]);
        final boolean suppressSectionWarnings = fc.getBoolean("suppress_section_warnings", false);

        final ConfigurationSection messageSection = Objects.requireNonNull(fc.getConfigurationSection("messages"),
                "ConfigurationSection messageSection could not be found in language file " + fileName);

        return loadLanguage(messageSection, locales);
    }

    /**
     * Loads a ConfigurationSection as a Language. All String keys (including in sub-sections) are added.
     *
     * @param languageSection A ConfigurationSection containing all language keys to parse.
     * @param locales         An Array of Locale aliases for this LanguageLoader to add its messages to.
     * @return A List of the NamespacedKeys created or found by this method which were added with Messages.
     * @throws NullPointerException     if languageSection or locales are null.
     * @throws IllegalArgumentException if locales is empty.
     * @since 1.0.0
     */
    private List<NamespacedKey> loadLanguage(@Nonnull ConfigurationSection languageSection, @Nonnull String... locales)
    {
        Objects.requireNonNull(languageSection, "LanguageSection cannot be null.");
        Objects.requireNonNull(locales, "Locales cannot be null.");
        if (locales.length < 1) throw new IllegalArgumentException("Locales cannot be empty.");

        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final Configuration config = HippOutLocalizationLib.getPlugin().getConfiguration();

        if (config.API_REGEX_LOCALE_TESTS)
            for (String locale : locales)
                ValidationUtil.validateLocale(locale);

        final Set<String> keys = languageSection.getKeys(true);
        final List<NamespacedKey> messageKeys = new ArrayList<>(keys.size());

        for (Map.Entry<String, Object> entry : languageSection.getValues(true).entrySet()) {
            if (entry.getValue() instanceof String) {
                final String key = entry.getKey();
                final String message = (String) entry.getValue();

                if (message.isEmpty()) {
                    plugin.getLogger().warning(String.format("Empty Message for language section %s: %s. Ignoring.",
                            languageSection.getName(), entry.getKey()));
                    continue;
                }

                // This avoids duplicate NamespacedKeys
                final NamespacedKey messageKey = languageHandler.getKey(plugin, key);

                // This will throw an exception if the message already exists. Fail-fast in this situation.
                languageHandler.addLocalizedMessage(messageKey, message, locales);
                messageKeys.add(messageKey);
            } else {
                if (!suppressSectionWarnings)
                    plugin.getLogger().warning(String.format("Non-message tag in language section %s: %s. Ignoring.",
                            languageSection.getName(), entry.getKey()));
            }
        }

        return messageKeys;
    }

    /**
     * Loads the requested file from the plugin's specified language directory into the plugin's FileConfiguration.
     *
     * @param fileName Language file to load
     * @return The Plugin's FileConfiguration object. See API Note tag, as this works somewhat differently to how it
     * seems.
     * @throws NullPointerException          if config or fileName are null.
     * @throws IllegalArgumentException      if fileName is empty.
     * @throws IOException                   if config.load fails to load the requested file.
     * @throws InvalidConfigurationException if config.load can load the requested file but it is not a valid YAML file.
     * @since 1.0.0
     */
    private YamlConfiguration loadLanguageConfig(@Nonnull String fileName) throws IOException,
            InvalidConfigurationException
    {
        Objects.requireNonNull(fileName, "Language Name cannot be null.");
        if (fileName.isEmpty()) throw new IllegalArgumentException("File Name cannot be empty.");

        final File configFile = new File(languageDirectory, fileName);

        if (!configFile.exists())
            throw new FileNotFoundException(String.format("Could not find requested file: %s", configFile.getPath()));

        final YamlConfiguration languageConfig = new YamlConfiguration();
        languageConfig.load(configFile); // Load explicitly for exceptions.

        return languageConfig;
    }

    /**
     * Returns the JavaPlugin of this LanguageLoader.
     *
     * @return The JavaPlugin of this LanguageLoader.
     * @since 1.0.0
     */
    public JavaPlugin getPlugin()
    {
        return plugin;
    }
}
