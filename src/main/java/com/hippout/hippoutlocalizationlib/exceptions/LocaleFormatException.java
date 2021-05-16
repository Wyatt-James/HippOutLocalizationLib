package com.hippout.hippoutlocalizationlib.exceptions;

/**
 * An Exception thrown when a locale String has an invalid format.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class LocaleFormatException extends RuntimeException {

    /**
     * Constructs a LocaleFormatException with the specified detail message.
     *
     * @param s – the detail message.
     * @since 1.0.0
     */
    public LocaleFormatException(String s)
    {
        super(s);
    }
}
