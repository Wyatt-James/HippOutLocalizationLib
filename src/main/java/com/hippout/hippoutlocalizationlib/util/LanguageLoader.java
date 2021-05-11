package com.hippout.hippoutlocalizationlib.util;

import com.hippout.hippoutlocalizationlib.Configuration;
import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.language.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.plugin.java.*;

import javax.annotation.*;
import java.util.*;

/**
 * Loads a language file for a plugin.
 *
 * @author Wyatt Kalmer
 */
public class LanguageLoader {
    private final JavaPlugin plugin;

    /**
     * Constructs a LanguageLoader with the given JavaPlugin.
     *
     * @param plugin Parent JavaPlugin of this LanguageLoader.
     * @throws NullPointerException if plugin is null.
     */
    public LanguageLoader(@Nonnull JavaPlugin plugin)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
    }

    /**
     * Loads a ConfigurationSection as a Language. All String keys (including in sub-sections) are added.
     *
     * @param languageSection A ConfigurationSection containing all language keys to parse.
     * @param locales         An Array of Locale aliases for this LanguageLoader to add its messages to.
     * @return A List of the NamespacedKeys created or found by this method which were added with Messages.
     * @throws NullPointerException     if languageSection or locales are null.
     * @throws IllegalArgumentException if locales is empty.
     */
    public List<NamespacedKey> loadLanguage(@Nonnull ConfigurationSection languageSection, @Nonnull String... locales)
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
                plugin.getLogger().warning(String.format("Non-message tag in language section %s: %s. Ignoring.",
                        languageSection.getName(), entry.getKey()));
            }
        }

        return messageKeys;
    }
}
