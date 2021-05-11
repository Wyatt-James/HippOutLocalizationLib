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
    private final List<NamespacedKey> keys;

    /**
     * Creates a Language with the given Locale.
     *
     * @param locale ISO-639 Locale as described in
     *               <a href=https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html>Oracle's Documentation</a>.
     *               Must abide by the regex stored in util.ValidationUtil.
     * @throws NullPointerException if locale is null.
     * @apiNote The LanguageHandler should either convert incoming codes to lowercase-only or throw an exception,
     * meaning that the Pattern test should never fail for case sensitivity.
     */
    Language(@Nonnull HippOutLocalizationLib plugin, @Nonnull String locale)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        this.locale = ValidationUtil.validateLocale(locale);
        this.messageMap = new HashMap<>();
        this.keys = new ArrayList<>();
    }

    /**
     * Adds a String message to this Language.
     *
     * @param messageKey Key to remove message of
     * @throws NullPointerException     if messageKey is null.
     * @throws NullPointerException     if message is null.
     * @throws IllegalArgumentException if given messageKey was already present in this Language.
     */
    void addMessage(@Nonnull NamespacedKey messageKey, @Nonnull String message)
    {
        Objects.requireNonNull(messageKey, "Message Key cannot be null. Lang: " + locale);
        Objects.requireNonNull(message, "Message cannot be null. Lang: " + locale);

        if (messageMap.containsKey(messageKey))
            throw new IllegalArgumentException(String.format(ERROR_ADD_ALREADY_CONTAINS, locale, messageKey));

        messageMap.put(messageKey, message);
        keys.add(messageKey);
    }

    /**
     * Removes a String message from this Language.
     *
     * @param messageKey Key to remove message of
     * @throws NullPointerException     if messageKey is null.
     * @throws IllegalArgumentException if the message could not be found.
     */
    void removeMessage(@Nonnull NamespacedKey messageKey)
    {
        Objects.requireNonNull(messageKey, "Message Key cannot be null. Lang: " + locale);

        // If removed, message was either null (illegal) or messageKey was not present.
        if (messageMap.remove(messageKey) == null)
            throw new IllegalArgumentException(String.format(ERROR_REMOVE_NOT_FOUND, locale, messageKey));
        else
            keys.remove(messageKey);
    }

    /**
     * Fetches a String message from this Language.
     *
     * @param messageKey Key to fetch message of
     * @return The requested message
     * @throws NullPointerException     if messageKey is null.
     * @throws IllegalArgumentException if the message could not be found.
     */
    @Nonnull
    String getMessage(@Nonnull NamespacedKey messageKey)
    {
        Objects.requireNonNull(messageKey, "Message Key cannot be null. Lang: " + locale);

        String out = messageMap.get(messageKey);

        if (out == null)
            throw new IllegalArgumentException(String.format(ERROR_GET_MESSAGE_NOT_FOUND, locale, messageKey));

        return out;
    }

    /**
     * Returns whether this Language contains a message with the given NamespacedKey.
     *
     * @param messageKey Key to check.
     * @throws NullPointerException if messageKey is null.
     */
    boolean containsMessage(@Nonnull NamespacedKey messageKey)
    {
        Objects.requireNonNull(messageKey, "Message Key cannot be null. Lang: " + locale);

        return keys.contains(messageKey);
    }

    /**
     * Fetches a NamespacedKey if it is contained within this Language.
     *
     * @param key Key of a NamespacedKey to fetch.
     * @return The requested NamespacedKey, or null if it did not exist.
     * @throws NullPointerException     if Key is null.
     * @throws IllegalArgumentException if Key is empty.
     * @apiNote For internal use only.
     */
    @Nullable
    NamespacedKey getMessageKey(@Nonnull String key)
    {
        Objects.requireNonNull(key, "Key cannot be null.");
        if (key.isEmpty()) throw new IllegalArgumentException("Key cannot be empty.");

        for (NamespacedKey messageKey : keys) {
            if (messageKey.getKey().equals(key))
                return messageKey;
        }

        return null;
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
