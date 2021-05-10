package com.hippout.hippoutlocalizationlib;

import org.bukkit.event.*;
import org.bukkit.event.player.*;

import javax.annotation.*;
import java.util.*;

/**
 * Listens for Player Locale changes and disconnects.
 *
 * @author Wyatt Kalmer
 */
public class EventListener implements Listener {
    private static final String LOG_PLAYER_LOCALE_CHANGED = "Player %s changed Locale to %s.";
    private static final String LOG_PLAYER_LOCALE_REMOVED = "Player %s disconnected and their Locale was removed.";
    private final HippOutLocalizationLib plugin;
    private final PlayerLocaleCache playerLocaleCache;

    public EventListener(@Nonnull HippOutLocalizationLib plugin, @Nonnull PlayerLocaleCache playerLocaleCache)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        this.playerLocaleCache = Objects.requireNonNull(playerLocaleCache, "Player Locale Cache cannot be null.");
    }

    /**
     * Updates the Player Locale Cache when their Locale changes or they join.
     */
    @EventHandler
    public void onPlayerChangesLocale(PlayerLocaleChangeEvent event)
    {
        String locale = event.getLocale();
        playerLocaleCache.setPlayerLocale(event.getPlayer().getUniqueId(), event.getLocale());
        plugin.getLogger().info(String.format(LOG_PLAYER_LOCALE_CHANGED, event.getPlayer().getName(), event.getLocale()));
    }

    /**
     * Removes the Player from the Player Locale Cache when they disconnect, but only if
     * config.yml/debug.remove_disconnected_player_locales is set to true.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        if (plugin.getConfiguration().REMOVE_DISCONNECTED_PLAYER_LOCALES)
            playerLocaleCache.removePlayerLocale(event.getPlayer().getUniqueId());

        plugin.getLogger().info(String.format(LOG_PLAYER_LOCALE_REMOVED, event.getPlayer().getName()));
    }
}