package com.hippout.hippoutlocalizationlib.events;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.event.*;

import javax.annotation.*;
import java.util.*;

/**
 * An Event called when the LocaleCache has a change that affects what getLocale(UUID) will return for a given UUID.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class LocaleCacheChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final UUID id;
    private final String oldLocale, newLocale;

    /**
     * Constructs a LocaleCacheChangeEvent with the given UUID and Locales.
     *
     * @param id        UUID which changed Locale.
     * @param oldLocale Old Locale of the given UUID.
     * @param newLocale New Locale of the given UUID.
     * @throws NullPointerException     if id, oldLocale, or newLocale is null.
     * @throws IllegalArgumentException if oldLocale.equals(newLocale).
     * @since 1.0.0
     */
    public LocaleCacheChangeEvent(@Nonnull UUID id, @Nonnull String oldLocale, @Nonnull String newLocale)
    {
        this.id = Objects.requireNonNull(id);
        this.oldLocale = Objects.requireNonNull(oldLocale, "Old Locale cannot be null.");
        this.newLocale = Objects.requireNonNull(newLocale, "New Locale cannot be null.");

        if (oldLocale.equals(newLocale))
            throw new IllegalArgumentException("Old and New Locales cannot be equal.");

        if (HippOutLocalizationLib.getPlugin().getConfiguration().API_REGEX_LOCALE_TESTS) {
            ValidationUtil.validateLocale(oldLocale);
            ValidationUtil.validateLocale(newLocale);
        }
    }

    /**
     * Returns the UUID of this LocaleCacheChangeEvent.
     *
     * @return The UUID of this LocaleCacheChangeEvent.
     * @since 1.0.0
     */
    @Nonnull
    public UUID getId()
    {
        return id;
    }


    /**
     * Returns the Old Locale of this LocaleCacheChangeEvent.
     *
     * @return The Old Locale of this LocaleCacheChangeEvent.
     * @since 1.0.0
     */
    @Nonnull
    public String getOldLocale()
    {
        return oldLocale;
    }


    /**
     * Returns the New Locale of this LocaleCacheChangeEvent.
     *
     * @return The New Locale of this LocaleCacheChangeEvent.
     * @since 1.0.0
     */
    @Nonnull
    public String getNewLocale()
    {
        return newLocale;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
