package com.hippout.hippoutlocalizationlib.exceptions;

/**
 * An Exception thrown when a requested Locale could not be found.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class LocaleNotFoundException extends IllegalStateException {

    /**
     * Constructs a LocaleNotFoundException with the specified detail message.
     *
     * @param s – the detail message.
     * @since 1.0.0
     */
    public LocaleNotFoundException(String s)
    {
        super(s);
    }
}
