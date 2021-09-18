package com.hippout.hippoutlocalizationlib.objects;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.api.*;
import com.hippout.hippoutlocalizationlib.events.*;
import com.hippout.hippoutlocalizationlib.exceptions.*;
import com.hippout.hippoutlocalizationlib.locale.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

import javax.annotation.*;
import java.util.*;

/**
 * A BossBar that will be localized for each Player seeing it.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class LocalizedBossBar implements BossBar, Listener {
    private final Map<String, BossBar> bossBarMap;
    private final List<BossBar> bossBars;
    private final Set<String> locales;

    // Dummy BossBar allows behavior to always be consistent with the server. Always set to invisible.
    private final BossBar dummy;

    private Object[] formatArgs;
    private BarFlag[] barFlags;

    private boolean isVisible;

    private NamespacedKey titleKey;

    private boolean isClosed;

    /**
     * Constructs a LocalizedBossBar with the given parameters.
     *
     * @param titleKey NamespacedKey for this LocalizedBossBar.
     * @param barColor Bar Color to use.
     * @param barStyle Bar Style to use.
     * @param barFlags Bar Flags to set.
     * @throws NullPointerException if titleKey or barFlags is null
     * @since 1.0.0
     */
    public LocalizedBossBar(@Nonnull NamespacedKey titleKey, @Nonnull BarColor barColor,
                            @Nonnull BarStyle barStyle, @Nonnull BarFlag... barFlags)
    {
        this.titleKey = Objects.requireNonNull(titleKey, "Title Key cannot be null.");
        Objects.requireNonNull(barFlags, "Bar Flags cannot be null.");

        dummy = Bukkit.createBossBar(null, barColor, barStyle, barFlags);
        dummy.setVisible(false);

        isVisible = false;

        bossBarMap = new HashMap<>();
        bossBars = new LinkedList<>();
        locales = new HashSet<>();

        formatArgs = new Object[0];
        this.barFlags = Arrays.copyOf(barFlags, barFlags.length);

        registerEvents();
        isClosed = false;
    }

    /**
     * Closes this LocalizedBossBar by setting it to invisible, removing all Players, and unregistering events. A
     * closed LocalizedBossBar cannot be re-opened.
     *
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public void close()
    {
        if (isClosed) throw new IllegalStateException("Cannot close a closed BossBar.");

        setVisible(false);
        removeAll();
        unregisterEvents();
        isClosed = true;
    }

    @SuppressWarnings("unused")
    public boolean isClosed()
    {
        return isClosed;
    }

    /**
     * Sets the Message Formatting Arguments of this LocalizedBossBar. Copies the given Array. Does NOT update messages.
     *
     * @param formatArgs New Formatting Arguments to use.
     * @throws NullPointerException if formatArgs is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public void setFormatArgs(@Nonnull Object... formatArgs)
    {
        Objects.requireNonNull(formatArgs, "Format Args cannot be null.");

        this.formatArgs = Arrays.copyOf(formatArgs, formatArgs.length);
    }

    /**
     * Updates the messages of all BossBars in this LocalizedBossBar.
     *
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public void updateMessages()
    {
        for (Map.Entry<String, BossBar> entry : bossBarMap.entrySet()) {
            final String message = String.format(Macros.getLocalizedMessage(titleKey, entry.getKey()), formatArgs);
            entry.getValue().setTitle(message);
        }
    }

    /**
     * Returns the Title Key of this LocalizedBossBar.
     *
     * @return The Title Key of this LocalizedBossBar.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public NamespacedKey getTitleKey()
    {
        return titleKey;
    }

    /**
     * Sets the Title Key of this LocalizedBossBar. Updates the messages of all BossBars contained within, so update
     * format args first. If the given NamespacedKey is not contained within the LanguageHandler, will print a warning.
     *
     * @param titleKey NamespacedKey to set as the Title Key.
     * @throws NullPointerException if titleKey is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public void setTitleKey(@Nonnull NamespacedKey titleKey)
    {
        this.titleKey = Objects.requireNonNull(titleKey, "Title Key cannot be null.");

        final HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();

        if (!plugin.getLanguageHandler().containsKey(titleKey))
            plugin.getLogger().warning(String.format("Warning: given key %s not contained within LanguageHandler.", titleKey));

        updateMessages();
    }

    /**
     * Convenience method. Registers all events associated with this Extension
     *
     * @since 1.0.0
     */
    protected void registerEvents()
    {
        final HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Convenience method. Unregisters all events associated with this Extension
     *
     * @since 1.0.0
     */
    protected void unregisterEvents()
    {
        HandlerList.unregisterAll(this);
    }

    /**
     * Creates a BossBar and updates all lists and Maps accordingly.
     *
     * @param locale Locale for the new BossBar
     * @throws NullPointerException  if locale is null.
     * @throws LocaleFormatException if INTERNAL_REGEX_LOCALE_TESTS is true and the Locale has an invalid format.
     * @throws IllegalStateException if the given Locale is already contained within locales.
     * @since 1.0.0
     */
    private BossBar createBossBar(@Nonnull String locale)
    {
        Objects.requireNonNull(locale, "Locale cannot be null.");

        if (HippOutLocalizationLib.getPlugin().getConfiguration().INTERNAL_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(locale);

        if (locales.contains(locale)) throw new IllegalStateException("Duplicate Locale " + locale);


        final String title = String.format(Macros.getLocalizedMessage(titleKey, locale), formatArgs);
        final BossBar bossBar = Bukkit.createBossBar(title, dummy.getColor(), dummy.getStyle(), barFlags);

        bossBar.setProgress(dummy.getProgress());
        bossBar.setVisible(isVisible());

        locales.add(locale);
        bossBarMap.put(locale, bossBar);
        bossBars.add(bossBar);

        for(Player p : dummy.getPlayers())
        {
            final String playerLocale = HippOutLocalizationLib.getPlugin().getLocaleCache().getLocale(p.getUniqueId());
            if(playerLocale.equals(locale))
            {
                bossBarMap.get(playerLocale).removePlayer(p);
                bossBar.addPlayer(p);
            }
        }

        return bossBar;
    }

    /**
     * Returns a BarFlag array without the given BarFlag.
     *
     * @param oldFlags Old BarFlag array.
     * @param barFlag  BarFlag to remove.
     * @return A copy of oldFlags with length 1 shorter, missing barFlag
     * @throws NullPointerException     if oldFlags or barFlag is null.
     * @throws IllegalArgumentException if oldFlags does not contain barFlag.
     * @since 1.0.0
     */
    private static BarFlag[] getWithout(@Nonnull BarFlag[] oldFlags, @Nonnull BarFlag barFlag)
    {
        Objects.requireNonNull(oldFlags, "OldFlags cannot be null.");
        Objects.requireNonNull(barFlag, "BarFlag cannot be null.");

        int removeIndex = -1;

        for (int i = 0; i < oldFlags.length; ++i) {
            if (barFlag == oldFlags[i]) {
                removeIndex = i;
                break;
            }
        }

        if (removeIndex == -1) throw new IllegalArgumentException("BarFlags did not contain barFlag " + barFlag);

        BarFlag[] newFlags = new BarFlag[oldFlags.length - 1];

        for (int i = 0; i < oldFlags.length; ++i) {
            if (i < removeIndex)
                newFlags[i] = oldFlags[i];
            else if (i > removeIndex)
                newFlags[i - 1] = oldFlags[i];
        }

        return newFlags;
    }

    // --------------- Overridden methods ---------------

    /**
     * Returns the Color of this LocalizedBossBar.
     *
     * @return The Color of this LocalizedBossBar.
     * @since 1.0.0
     */
    @Override
    @Nonnull
    public BarColor getColor()
    {
        return dummy.getColor();
    }

    /**
     * Sets the Color of this LocalizedBossBar.
     *
     * @param color New Color for this LocalizedBossBar.
     * @since 1.0.0
     */
    @Override
    public void setColor(@Nonnull BarColor color)
    {
        dummy.setColor(color);
        bossBars.forEach(b -> b.setColor(color));
    }

    /**
     * Returns the Style of this LocalizedBossBar.
     *
     * @return The Style of this LocalizedBossBar.
     * @since 1.0.0
     */
    @Override
    @Nonnull
    public BarStyle getStyle()
    {
        return dummy.getStyle();
    }

    /**
     * Sets the Style of this LocalizedBossBar.
     *
     * @param style Style to set this LocalizedBossBar to.
     * @since 1.0.0
     */
    @Override
    public void setStyle(@Nonnull BarStyle style)
    {
        dummy.setStyle(style);
        bossBars.forEach(b -> setStyle(style));
    }

    /**
     * Removes a flag from this LocalizedBossBar.
     *
     * @param flag Existing flag to remove.
     * @since 1.0.0
     */
    @Override
    public void removeFlag(@Nonnull BarFlag flag)
    {
        dummy.removeFlag(flag);
        bossBars.forEach(b -> removeFlag(flag));
        this.barFlags = getWithout(this.barFlags, flag);
    }

    /**
     * Adds a flag to this LocalizedBossBar.
     *
     * @param flag Flag to add.
     * @since 1.0.0
     */
    @Override
    public void addFlag(@Nonnull BarFlag flag)
    {
        dummy.addFlag(flag);
        bossBars.forEach(b -> addFlag(flag));

        BarFlag[] newFlags = Arrays.copyOf(barFlags, barFlags.length + 1);
        newFlags[newFlags.length - 1] = flag;
        barFlags = newFlags;
    }

    /**
     * Returns whether this LocalziedBossBar has a given flag set.
     *
     * @param flag Flag to check
     * @return True if the flag is set, false otherwise.
     * @since 1.0.0
     */
    @Override
    public boolean hasFlag(@Nonnull BarFlag flag)
    {
        return dummy.hasFlag(flag);
    }

    /**
     * Sets the progress of this LocalizedBossBar.
     *
     * @param progress the new progress for this LocalizedBossBar.
     * @since 1.0.0
     */
    @Override
    public void setProgress(double progress)
    {
        dummy.setProgress(progress);
        bossBars.forEach(b -> b.setProgress(progress));
    }

    /**
     * Returns the progress of this LocalizedBossBar.
     *
     * @return The progress of this LocalizedBossBar.
     * @since 1.0.0
     */
    @Override
    public double getProgress()
    {
        return dummy.getProgress();
    }

    /**
     * Adds the given Player to this LocalizedBossBar.
     *
     * @param player Player to add.
     * @since 1.0.0
     */
    @Override
    public void addPlayer(@Nonnull Player player)
    {
        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getLocaleCache();
        final String locale = localeCache.getLocale(player.getUniqueId());

        if (!locales.contains(locale))
            createBossBar(locale);

        dummy.addPlayer(player);
        bossBarMap.get(locale).addPlayer(player);
    }

    /**
     * Removes the given Player from this LocalizedBossBar.
     *
     * @param player Player to remove.
     * @since 1.0.0
     */
    @Override
    public void removePlayer(@Nonnull Player player)
    {
        dummy.removePlayer(player);
        bossBarMap.get(HippOutLocalizationLib.getPlugin().getLocaleCache().getLocale(player.getUniqueId())).removePlayer(player);
    }

    /**
     * Removes all Players from this LocalizedBossBar.
     *
     * @since 1.0.0
     */
    @Override
    public void removeAll()
    {
        dummy.removeAll();
        bossBars.forEach(BossBar::removeAll);
    }

    /**
     * Returns a List of Players currently held by this LocalizedBossBar.
     *
     * @return a List of Players currently held by this LocalizedBossBar.
     * @since 1.0.0
     */
    @Override
    @Nonnull
    public List<Player> getPlayers()
    {
        return dummy.getPlayers();
    }

    /**
     * Sets whether this LocalizedBossBar is visible.
     *
     * @param visible Whether or not to be visible.
     * @since 1.0.0
     */
    @Override
    public void setVisible(boolean visible)
    {
        isVisible = visible;
        bossBars.forEach(b -> b.setVisible(visible));
    }

    /**
     * Returns whether this LocalizedBossBar is visible.
     *
     * @return whether this LocalizedBossBar is visible.
     * @since 1.0.0
     */
    @Override
    public boolean isVisible()
    {
        return isVisible;
    }

    // --------------- Unused & Deprecated Methods ---------------

    /**
     * Cannot be used with a LocalizedBossBar. Throws an IllegalStateException. Use getTitleKey instead.
     *
     * @throws IllegalStateException always.
     * @deprecated Cannot be used with a LocalizedBossBar.
     */
    @Override
    @Nonnull
    public String getTitle()
    {
        throw new IllegalStateException("Cannot get title of LocalizedBossBar.");
    }

    /**
     * Cannot be used with a LocalizedBossBar. Throws an IllegalStateException. Use setTitleKey instead.
     *
     * @throws IllegalStateException always.
     * @deprecated Cannot be used with a LocalizedBossBar.
     */
    @Override
    public void setTitle(String title)
    {
        throw new IllegalStateException("Cannot set title of LocalizedBossBar.");
    }

    /**
     * @deprecated Deprecated. Use setVisible instead.
     */
    @Override
    public void show()
    {
        bossBars.forEach(BossBar::show);
    }

    /**
     * @deprecated Deprecated. Use setVisible instead.
     */
    @Override
    public void hide()
    {
        bossBars.forEach(BossBar::hide);
    }

    // --------------- Event Listeners ---------------

    @EventHandler
    @SuppressWarnings("unused")
    public void onLocaleCacheChange(LocaleCacheChangeEvent event)
    {
        final OfflinePlayer p = Bukkit.getOfflinePlayer(event.getId());
        if (p.isOnline()) {
            final Player player = Objects.requireNonNull(p.getPlayer(), "Could not get Player from OfflinePlayer.");

            if (getPlayers().contains(player)) {
                final BossBar oldBossBar = bossBarMap.get(event.getOldLocale());

                final BossBar newBossBar;
                if (bossBarMap.containsKey(event.getNewLocale()))
                    newBossBar = bossBarMap.get(event.getNewLocale());
                else {
                    newBossBar = createBossBar(event.getNewLocale());
                }

                oldBossBar.removePlayer(player);
                newBossBar.addPlayer(player);
            }
        }
    }
}
