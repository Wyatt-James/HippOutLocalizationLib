package com.hippout.hippoutlocalizationlib.util;

import javax.annotation.*;
import java.util.*;

/**
 * Miscellanious utilities.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class MiscUtil {

    /**
     * Converts a trimmed UUID to a full UUID suitable for UUID fetching.
     * <p>
     * Original version created by and thanks to TheKraken7. Cleaned up by Wyatt Kalmer.
     * </p>
     *
     * @param trimmedUUIDString Trimmed UUID String to convert to full UUID String. Must be length 32.
     * @return The converted full UUID String with length 36.
     * @throws IllegalArgumentException is thrown if the UUID String is not of length 32.
     * @throws NullPointerException     if trimmedUUIDString is null.
     * @since 1.0.0
     */
    public static String convertTrimmedUuidToFull(@Nonnull String trimmedUUIDString) throws IllegalArgumentException
    {
        Objects.requireNonNull(trimmedUUIDString, "Trimmed UUID String cannot be null.");

        if (trimmedUUIDString.length() != 32)
            throw new IllegalArgumentException("Trimmed UUID String length was not 32.");

        final StringBuilder builder = new StringBuilder(trimmedUUIDString.trim());

        /* Backwards adding to avoid index adjustments */
        builder.insert(20, "-");
        builder.insert(16, "-");
        builder.insert(12, "-");
        builder.insert(8, "-");

        return builder.toString();
    }
}
