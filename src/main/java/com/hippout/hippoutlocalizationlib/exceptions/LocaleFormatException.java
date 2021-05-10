package com.hippout.hippoutlocalizationlib.exceptions;

/**
 * An Exception thrown when a locale String has an invalid format.
 *
 * @author Wyatt Kalmer
 */
public class LocaleFormatException extends IllegalArgumentException {

    /**
     * Constructs a LocaleFormatException with the specified detail message.
     *
     * @param s – the detail message.
     */
    public LocaleFormatException(String s)
    {
        super(s);
    }
}
