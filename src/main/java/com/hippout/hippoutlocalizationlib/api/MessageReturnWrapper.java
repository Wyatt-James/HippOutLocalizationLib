package com.hippout.hippoutlocalizationlib.api;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.util.*;

import javax.annotation.*;
import java.util.*;

/**
 * Wraps a Message with some additional details of how it was found.
 *
 * @author Wyatt Kalmer
 */
public class MessageReturnWrapper {
    private final String message;
    private final String locale;
    private final MessageType messageType;

    /**
     * Constructs a MessageReturnWrapper with the given parameters.
     *
     * @param message     Message to return.
     * @param locale      Locale of the found message.
     * @param messageType How the message was found, whether it was present, the default Language was used, or if no
     *                    Language contained it and the failsafe message was used.
     * @throws NullPointerException     if message, locale, or messageType are null.
     * @throws IllegalArgumentException if locale is empty.
     * @throws LocaleFormatException    if locale is not a valid format and config.yml/debug.api_regex_locale_tests is
     *                                  true.
     */
    public MessageReturnWrapper(@Nonnull String message, @Nonnull String locale, @Nonnull MessageType messageType)
    {
        this.message = Objects.requireNonNull(message, "Message cannot be null.");
        this.locale = Objects.requireNonNull(locale, "Locale cannot be null.");
        this.messageType = Objects.requireNonNull(messageType, "Message Type cannot be null.");

        if (this.locale.isEmpty())
            throw new IllegalArgumentException("Locale cannot be empty.");

        if (HippOutLocalizationLib.getPlugin().getConfiguration().API_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(this.locale, "Invalid Locale in MessageReturnWrapper: %s.");
    }

    /**
     * Returns the found Message. It is not guaranteed to be of the requested locale.
     *
     * @return the found message.
     */
    @Nonnull
    public String getMessage()
    {
        return message;
    }

    /**
     * Returns the actual locale of the found Message. It is not guaranteed to be the requested Locale.
     *
     * @return the locale of the found Mesasge.
     */
    @Nonnull
    public String getLocale()
    {
        return locale;
    }

    /**
     * Returns the MessageType of this MessageReturnWrapper.
     *
     * @return the MessageType of this MessageReturnWrapper.
     */
    @Nonnull
    public MessageType getMessageType()
    {
        return messageType;
    }

    @Override
    public String toString()
    {
        return message;
    }

    /**
     * Identifiers for different situations for locating Messages.
     *
     * @author Wyatt Kalmer
     * @enumValue FOUND is used when the Message was found in the requested Language.
     * @enumValue DEFAULT_LANGUAGE_FALLBACK is used when the Message could not be found in the given language but was found
     * in the Default Language.
     * @enumValue FAILSAFE_MESSAGE is used when the Message could not be found in either the requested Language or the
     * Default Language.
     */
    public enum MessageType {
        FOUND, DEFAULT_LANGUAGE_FALLBACK, FAILSAFE_MESSAGE
    }
}
