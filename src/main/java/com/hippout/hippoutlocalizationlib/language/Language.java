package com.hippout.hippoutlocalizationlib.language;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;

import javax.annotation.*;
import java.util.*;

/**
 * Represents a container for all messages associated with a given language.
 *
 * @author Wyatt Kalmer
 */
public class Language {
    private static final String ERROR_ADD_ALREADY_CONTAINS = "[%s] Message Map already contains given key %s.";
    private static final String ERROR_REMOVE_NOT_FOUND = "[%s] Message Map did not contain given key %s.";
    private static final String ERROR_GET_MESSAGE_NOT_FOUND = "[%s] The requested message %s could not be found.";

    private final HippOutLocalizationLib plugin;
    private final String locale;

    private final Map<NamespacedKey, String> messageMap;

    /**
     * Creates a Language with the given language code.
     *
     * @param locale ISO-639 Language Code as described in
     *               <a href=https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html>Oracle's Documentation</a>
     *               including the "variant" extension. Must be entirely lowercase, with an underscore separator.
     * @throws NullPointerException is thrown if locale is null.
     * @apiNote The LanguageHandler should either convert incoming codes to lowercase-only or throw an exception,
     * meaning that the Pattern test should never fail for case sensitivity.
     */
    Language(@Nonnull HippOutLocalizationLib plugin, @Nonnull String locale)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        this.locale = ValidationUtil.validateLocale(locale);
        this.messageMap = new HashMap<>();
    }

    /**
     * Adds a String message to this Language.
     *
     * @param key Key to remove message of
     * @throws NullPointerException     is thrown if key is null.
     * @throws NullPointerException     is thrown if message is null.
     * @throws IllegalArgumentException is thrown if given key was already present in this Language..
     */
    void addMessage(@Nonnull NamespacedKey key, @Nonnull String message)
    {
        Objects.requireNonNull(key, "Key cannot be null. Lang: " + locale);
        Objects.requireNonNull(message, "Message cannot be null. Lang: " + locale);

        if (messageMap.containsKey(key))
            throw new IllegalArgumentException(String.format(ERROR_ADD_ALREADY_CONTAINS, locale, key));

        messageMap.put(key, message);
    }

    /**
     * Removes a String message from this Language.
     *
     * @param key Key to remove message of
     * @throws NullPointerException     is thrown if key is null.
     * @throws IllegalArgumentException is thrown if the message could not be found.
     */
    void removeMessage(@Nonnull NamespacedKey key)
    {
        Objects.requireNonNull(key, "Key cannot be null. Lang: " + locale);

        // If removed, message was either null (illegal) or key was not present.
        if (messageMap.remove(key) == null)
            throw new IllegalArgumentException(String.format(ERROR_REMOVE_NOT_FOUND, locale, key));
    }

    /**
     * Fetches a String message from this Language.
     *
     * @param key Key to fetch message of
     * @return The requested message
     * @throws NullPointerException     is thrown if key is null.
     * @throws IllegalArgumentException is thrown if the message could not be found.
     */
    @Nonnull
    String getMessage(@Nonnull NamespacedKey key)
    {
        Objects.requireNonNull(key, "Key cannot be null. Lang: " + locale);

        String out = messageMap.get(key);

        if (out == null)
            throw new IllegalArgumentException(String.format(ERROR_GET_MESSAGE_NOT_FOUND, locale, key));

        return out;
    }


    /**
     * Returns whether this Language contains a message with the given NamespacedKey.
     *
     * @param key Key to fetch message of
     * @throws NullPointerException is thrown if key is null.
     */
    boolean containsMessage(@Nonnull NamespacedKey key)
    {
        Objects.requireNonNull(key, "Key cannot be null.");

        return messageMap.containsKey(key);
    }

    /**
     * Returns ISO-639 the Locale of this Language. It will be entirely in lowercase and will always have a language
     * extension code, separated by an underscore.
     */
    @Nonnull
    String getLocale()
    {
        return locale;
    }
}
