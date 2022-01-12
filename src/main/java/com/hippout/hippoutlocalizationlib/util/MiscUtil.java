package com.hippout.hippoutlocalizationlib.util;

import org.bukkit.*;
import org.bukkit.entity.*;

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
    public static String convertTrimmedUuidToFull(@Nonnull String trimmedUUIDString)
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

    /**
     * Returns a Collection containing all Online Players whose UUIDs are contained within the UUID Collection.
     *
     * @param uuidCollection UUIDs to check.
     * @return The new Collection of Players.
     * @throws NullPointerException if uuidCollection is null.
     * @throws NullPointerException if any individual UUID is null.
     * @since 1.0.0
     */
    public static Collection<Player> getOnlinePlayers(@Nonnull Collection<UUID> uuidCollection)
    {
        Objects.requireNonNull(uuidCollection, "UUID Collection cannot be null.");
        final List<Player> playerList = new ArrayList<>();

        for (UUID id : uuidCollection) {
            Objects.requireNonNull(id, "Individual UUIDs cannot be null.");

            final Player p = Bukkit.getPlayer(id);

            if (p != null)
                playerList.add(p);
        }

        return playerList;
    }

    /**
     * Returns a Collection containing the UUIDs of all Players inside playerCollection.
     *
     * @param playerCollection UUIDs to check.
     * @return The new Collection of UUIDs.
     * @throws NullPointerException if playerCollection is null.
     * @throws NullPointerException if any individual Player is null.
     * @since 1.0.0
     */
    public static Collection<UUID> getPlayerIds(@Nonnull Collection<? extends Player> playerCollection)
    {
        Objects.requireNonNull(playerCollection, "Player Collection cannot be null.");
        final List<UUID> idList = new ArrayList<>();

        for (Player p : playerCollection) {
            Objects.requireNonNull(p, "Player cannot be null.");
            idList.add(p.getUniqueId());
        }

        return idList;
    }
}
