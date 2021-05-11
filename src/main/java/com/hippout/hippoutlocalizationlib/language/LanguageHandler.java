package com.hippout.hippoutlocalizationlib.language;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.MessageReturnWrapper.*;
import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;
import org.bukkit.plugin.java.*;

import javax.annotation.*;
import java.util.*;

/**
 * Handles languages, including fetching from, creating, etc.
 *
 * @author Wyatt Kalmer
 */
public class LanguageHandler {
    private final HippOutLocalizationLib plugin;

    private final Map<String, Language> languageMap;
    private final List<NamespacedKey> keys;
    private final Language defaultLanguage; // Cache

    /**
     * Constructs a LanguageManager with the given HippOutLocalizationLib and default Locale.
     *
     * @param plugin        HippOutLocalizationLib instance.
     * @param defaultLocale Default ISO-639 Locale. Must pass regex in ValidationUtil.
     * @throws NullPointerException  if plugin or defaultLocale are null.
     * @throws LocaleFormatException if defaultLocale is an invalid format and config.yml/internal_regex_locale_tests
     *                               is enabled.
     */
    public LanguageHandler(@Nonnull HippOutLocalizationLib plugin, @Nonnull String defaultLocale)
    {
        Objects.requireNonNull(plugin, "Plugin cannot be null.");
        Objects.requireNonNull(defaultLocale, "Default Language Code cannot be null.");

        if (plugin.getConfiguration().INTERNAL_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(defaultLocale);

        this.plugin = plugin;
        languageMap = new HashMap<>();
        keys = new ArrayList<>();
        defaultLanguage = new Language(plugin, defaultLocale);
        languageMap.put(defaultLocale, defaultLanguage);
    }

    /**
     * Returns a MessageReturnWrapper containing a String corresponding to the given NamespacedKey. If the Language
     * denoted by locale was present and contained the given message, returns that message, else the Default
     * Language is used as a fallback. If neither have a corresponding String, throws an exception.
     *
     * @param locale     Language Code to fetch from.
     * @param messageKey Key corresponding to the desired message ID.
     * @return A MessageReturnWrapper containing the desired String. If the Default Language was used as a fallback,
     * the MessageReturnWrapper will denote such.
     * @throws NullPointerException  if MessageKey or Locale is null.
     * @throws LocaleFormatException if locale is an invalid format and config.yml/api_regex_locale_tests is
     *                               enabled.
     */
    @Nonnull
    public MessageReturnWrapper getLocalizedMessage(@Nonnull String locale, @Nonnull NamespacedKey messageKey)
    {
        Objects.requireNonNull(locale, "Locale cannot be null.");
        Objects.requireNonNull(messageKey, "Key cannot be null.");

        final Configuration config = plugin.getConfiguration();

        if (config.API_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(locale, "The given LanguageCode does not match the ISO-639" +
                    " test Pattern. This can usually be disabled for production use. Yours: %s");

        String message;
        String foundLocale;
        MessageType messageType;

        /*
         *  Attempt to load message from the language. If it could not be found, load it from the default language.
         *  If it still could not be found, load the fallback from the Configuration.
         *
         *  IllegalArgumentExceptions are faster than language.containsMessage but much harder to read and write.
         */
        Language language = languageMap.get(locale);
        if (language != null && language.containsMessage(messageKey)) {
            message = language.getMessage(messageKey);
            foundLocale = language.getLocale();
            messageType = MessageType.FOUND;
        } else {
            if (defaultLanguage.containsMessage(messageKey)) {
                message = defaultLanguage.getMessage(messageKey);
                foundLocale = defaultLanguage.getLocale();
                messageType = MessageType.DEFAULT_LANGUAGE_FALLBACK;
            } else {
                message = config.FAILSAFE_MESSAGE;
                foundLocale = HippOutLocalizationLib.FAILSAFE_LOCALE;
                messageType = MessageType.FAILSAFE_MESSAGE;
            }
        }

        return new MessageReturnWrapper(message, foundLocale, messageType);
    }

    /**
     * Returns a MessageReturnWrapper containing a String corresponding to the given NamespacedKey, fetched from the
     * Default Language.
     *
     * @param messageKey Key corresponding to the desired message ID.
     * @return A MessageReturnWrapper containing the desired String.
     * @throws NullPointerException if MessageKey is null.
     */
    public MessageReturnWrapper getDefaultLanguageMessage(@Nonnull NamespacedKey messageKey)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");

        final Configuration config = plugin.getConfiguration();

        String message;
        String foundLocale;
        MessageType messageType;

        // Attempt to find from default language, else grab failsafe.
        if (defaultLanguage.containsMessage(messageKey)) {
            message = defaultLanguage.getMessage(messageKey);
            foundLocale = defaultLanguage.getLocale();
            messageType = MessageType.FOUND;
        } else {
            message = config.FAILSAFE_MESSAGE;
            foundLocale = HippOutLocalizationLib.FAILSAFE_LOCALE;
            messageType = MessageType.FAILSAFE_MESSAGE;
        }

        return new MessageReturnWrapper(message, foundLocale, messageType);
    }

    /**
     * Convenience method. Equivalent to calling
     * <b>getLocalizedMessage(HippOutLocalizationLib.getPlugin().getconfiguration().CONSOLE_LOCALE, key)</b>
     *
     * @param messageKey Key corresponding to the desired message ID.
     * @return A MessageReturnWrapper containing the desired String.
     * @throws NullPointerException if MessageKey is null.
     */
    public MessageReturnWrapper getConsoleMessage(@Nonnull NamespacedKey messageKey)
    {
        return getLocalizedMessage(plugin.getConfiguration().CONSOLE_LOCALE, messageKey);
    }

    /**
     * Convenience method. Equivalent to calling
     * <b>getLocalizedMessage(HippOutLocalizationLib.getPlugin().getconfiguration().REMOTE_CONSOLE_LOCALE, key)</b>
     *
     * @param messageKey Key corresponding to the desired message ID.
     * @return A MessageReturnWrapper containing the desired String.
     * @throws NullPointerException if MessageKey is null.
     */
    public MessageReturnWrapper getRemoteConsoleMessage(@Nonnull NamespacedKey messageKey)
    {
        return getLocalizedMessage(plugin.getConfiguration().REMOTE_CONSOLE_LOCALE, messageKey);
    }

    /**
     * Adds a localized message to the correct language. If the Language does not yet exist, one is created.
     *
     * @param messageKey Key to give new localized message.
     * @param message    Message to add.
     * @param locales    Locales to add the new message to. Must have length of at least 1.
     * @throws NullPointerException     if any parameters are null.
     * @throws LocaleFormatException    if the Locale is not in a valid format and API-layer validation is enabled.
     * @throws IllegalArgumentException if the corresponding Language already has a message with this key.
     */
    public void addLocalizedMessage(@Nonnull NamespacedKey messageKey, @Nonnull String message, @Nonnull String... locales)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");
        Objects.requireNonNull(locales, "Locale cannot be null.");
        Objects.requireNonNull(message, "message cannot be null.");
        if (locales.length < 1) throw new IllegalArgumentException("Must provide at least one Locale.");

        if (plugin.getConfiguration().API_REGEX_LOCALE_TESTS)
            for (String locale : locales)
                ValidationUtil.validateLocale(locale);

        for (String locale : locales) {
            Language language;
            if (languageMap.containsKey(locale)) {
                language = languageMap.get(locale);
            } else {
                language = new Language(plugin, locale);
                languageMap.put(locale, language);
            }

            if (language.containsMessage(messageKey)) {
                plugin.getLogger().warning(String.format("Language %s already contains message %s. The original" +
                        " message will be kept.", locale, messageKey));
            } else {
                language.addMessage(messageKey, message);
                keys.add(messageKey);
            }
        }
    }

    /**
     * Returns a NamespacedKey with the given plugin namespace and key. If this NamespacedKey is already registered,
     * returns the existing instance, else creates a new NamespacedKey.
     *
     * @param plugin Plugin of the NamespacedKey to fetch.
     * @param key    Key to search for.
     * @return The found or created NamespacedKey.
     * @throws NullPointerException     if Plugin or Key is empty.
     * @throws IllegalArgumentException if Key is empty.
     */
    @Nonnull
    public NamespacedKey getKey(@Nonnull JavaPlugin plugin, @Nonnull String key)
    {
        Objects.requireNonNull(plugin, "Plugin cannot be null.");
        Objects.requireNonNull(key, "Key cannot be null.");
        if (key.isEmpty()) throw new IllegalArgumentException("Key cannot be empty.");

        final String pluginKeyName = plugin.getName().toLowerCase();
        final String keyLowerCase = key.toLowerCase();

        if (!keyLowerCase.equals(key)) plugin.getLogger().warning("Uppercase keys are automatically converted to " +
                "lowercase by Bukkit. Yours: " + key);

        for (NamespacedKey messageKey : keys) {
            if (messageKey.getKey().equals(keyLowerCase) && messageKey.getNamespace().equals(pluginKeyName))
                return messageKey;
        }

        return new NamespacedKey(plugin, keyLowerCase);
    }
}
