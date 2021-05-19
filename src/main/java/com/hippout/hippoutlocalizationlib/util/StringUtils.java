package com.hippout.hippoutlocalizationlib.util;

import javax.annotation.*;
import java.util.*;

/**
 * Various String-related utilities.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class StringUtils {

    /**
     * Formats a String but only if args.length is greater than 0. Meant for a performance method because it seems that
     * AdoptOpenJDK doesn't actually do this check for whatever reason.
     *
     * @param str  String to format.
     * @param args Arguments to format with.
     * @return The formatted String.
     * @throws NullPointerException             if args is null.
     * @throws java.util.IllegalFormatException see String.format.
     * @since 1.0.0
     */
    public static String format(@Nonnull String str, @Nonnull Object... args)
    {
        Objects.requireNonNull(args, "Args cannot be null.");

        if (args.length != 0)
            return String.format(str, args);
        else
            return str;
    }
}
