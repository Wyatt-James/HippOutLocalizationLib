package com.hippout.hippoutlocalizationlib.util;

import com.hippout.hippoutlocalizationlib.exceptions.*;

import javax.annotation.*;
import java.util.*;
import java.util.regex.*;

/**
 * A utility class for easily validating various parameters throughout the project.
 *
 * @author Wyatt Kalmer
 */
public class ValidationUtil {
    public static final String LOCALE_REGEX = "(^[a-z\\-]{2,8}_[a-z0-9]{2,3}$)|(^[a-z]{2,8}$)";
    public static final Pattern ISO639_LANGUAGE_PATTERN = Pattern.compile(LOCALE_REGEX);

    private static final String DEFAULT_VALIDATION_ERROR = "Locale does not match ISO-639 Locale Pattern in" +
            " ValidationUtil. Yours: %s";

    /**
     * Validates and returns a given Locale with a default error message.
     *
     * @param locale Locale to validate
     * @return The input Locale.
     * @throws NullPointerException  is thrown if locale is null.
     * @throws LocaleFormatException is thrown if locale does not follow the format of ValidationUtil.LOCALE_REGEX.
     */
    @Nonnull
    public static String validateLocale(@Nonnull String locale)
    {
        return validateLocale(locale, DEFAULT_VALIDATION_ERROR);
    }

    /**
     * Validates and returns a given Locale with the given error message. For formatting, %1$s is the locale, which
     * will never be null.
     *
     * @param locale             Locale to validate
     * @param customErrorMessage A custom error message to pass to the LocaleFormatException.
     * @return The input Locale.
     * @throws NullPointerException  is thrown if locale is null.
     * @throws NullPointerException  is thrown if customErrorMessage is null.
     * @throws LocaleFormatException is thrown if locale does not follow the format of ValidationUtil.LOCALE_REGEX.
     */
    @Nonnull
    public static String validateLocale(@Nonnull String locale, @Nonnull String customErrorMessage)
    {
        Objects.requireNonNull(locale, "Locale cannot be null.");
        Objects.requireNonNull(customErrorMessage, "Custom Error Message cannot be null.");

        if (!ISO639_LANGUAGE_PATTERN.matcher(locale).matches())
            throw new LocaleFormatException(String.format(customErrorMessage, locale));

        return locale;
    }
}