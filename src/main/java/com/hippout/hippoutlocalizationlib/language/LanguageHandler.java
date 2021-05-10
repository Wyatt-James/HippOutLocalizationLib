package com.hippout.hippoutlocalizationlib.language;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.MessageReturnWrapper.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;

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
    private final Language defaultLanguage; // Cache

    public LanguageHandler(@Nonnull HippOutLocalizationLib plugin, @Nonnull String defaultLanguageCode)
    {
        Objects.requireNonNull(plugin, "Plugin cannot be null.");
        Objects.requireNonNull(defaultLanguageCode, "Default Language Code cannot be null.");

        this.plugin = plugin;
        languageMap = new HashMap<>();
        defaultLanguage = new Language(plugin, defaultLanguageCode);
        languageMap.put(defaultLanguageCode, defaultLanguage);
    }

    /**
     * Returns a MessageReturnWrapper containing a String corresponding to the given NamespacedKey. If the Language
     * denoted by locale was present and contained the given message, returns that message, else the Default
     * Language is used as a fallback. If neither have a corresponding String, throws an exception.
     *
     * @param locale Language Code to fetch from.
     * @param key    Key corresponding to the desired message ID.
     * @return A MessageReturnWrapper containing the desired String. If the Default Language was used as a fallback,
     * the MessageReturnWrapper will denote such.
     */
    @Nonnull
    public MessageReturnWrapper getLocalizedMessage(@Nonnull String locale, @Nonnull NamespacedKey key)
    {
        Objects.requireNonNull(locale, "Locale cannot be null.");
        Objects.requireNonNull(key, "Key cannot be null.");

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
        if (language != null && language.containsMessage(key)) {
            message = language.getMessage(key);
            foundLocale = language.getLocale();
            messageType = MessageType.FOUND;
        } else {
            if (defaultLanguage.containsMessage(key)) {
                message = defaultLanguage.getMessage(key);
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
     * @param key Key corresponding to the desired message ID.
     * @return A MessageReturnWrapper containing the desired String.
     */
    public MessageReturnWrapper getDefaultLanguageMessage(@Nonnull NamespacedKey key)
    {
        Objects.requireNonNull(key, "Key cannot be null.");

        final Configuration config = plugin.getConfiguration();

        String message;
        String foundLocale;
        MessageType messageType;

        // Attempt to find from default language, else grab failsafe.
        if (defaultLanguage.containsMessage(key)) {
            message = defaultLanguage.getMessage(key);
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
     * @param key Key corresponding to the desired message ID.
     * @return A MessageReturnWrapper containing the desired String.
     */
    public MessageReturnWrapper getConsoleMessage(@Nonnull NamespacedKey key)
    {
        return getLocalizedMessage(plugin.getConfiguration().CONSOLE_LOCALE, key);
    }

    /**
     * Adds a localized message to the correct language. If the Language does not yet exist, one is created.
     *
     * @param key     Key to give new localized message.
     * @param message Message to add.
     * @param locales Locales to add the new message to. Must have length of at least 1.
     * @throws NullPointerException                                                if any parameters are null.
     * @throws com.hippout.hippoutlocalizationlib.exceptions.LocaleFormatException if the Locale is not in a valid
     *                                                                             format and API-layer validation is enabled.
     * @throws IllegalArgumentException                                            if the corresponding Language already has a message with this key.
     */
    public void addLocalizedMessage(@Nonnull NamespacedKey key, @Nonnull String message, @Nonnull String... locales)
    {
        Objects.requireNonNull(key, "Key cannot be null.");
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

            if (language.containsMessage(key))
                plugin.getLogger().warning(String.format("Language %s already contains message %s. Will keep the old message.", locale, key));
            else
                language.addMessage(key, message);
        }
    }
}
