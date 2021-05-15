package com.hippout.hippoutlocalizationlib.locale;

import com.hippout.hippoutlocalizationlib.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import javax.annotation.*;
import java.util.*;

/**
 * Listens for Player Locale changes and disconnects and modifies the Player Locale Cache appropriately.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class EventListener implements Listener {
    private static final String LOG_PLAYER_LOCALE_CHANGED = "Player %s changed Locale to %s.";
    private static final String LOG_PLAYER_LOCALE_REMOVED = "Player %s disconnected and their Locale was removed.";
    private final HippOutLocalizationLib plugin;

    /**
     * Constructs an EventListener.
     *
     * @param plugin HippOutLocalizationLib instance.
     * @throws NullPointerException if plugin is null.
     * @since 1.0.0
     */
    public EventListener(@Nonnull HippOutLocalizationLib plugin)
    {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
    }

    /**
     * Updates the Player Locale Cache when they join the server.
     *
     * @param event Event passed from Bukkit
     * @since 1.0.0
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        plugin.getPlayerLocaleCache().setLocale(event.getPlayer().getUniqueId(), event.getPlayer().getLocale());
        plugin.getLogger().info(String.format(LOG_PLAYER_LOCALE_CHANGED,
                event.getPlayer().getName(), event.getPlayer().getLocale()));
    }

    /**
     * Updates the Player Locale Cache when their Locale changes or they join the server.
     *
     * @param event Event passed from Bukkit
     * @since 1.0.0
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangesLocale(PlayerLocaleChangeEvent event)
    {
        plugin.getPlayerLocaleCache().setLocale(event.getPlayer().getUniqueId(), event.getLocale());
        plugin.getLogger().info(String.format(LOG_PLAYER_LOCALE_CHANGED, event.getPlayer().getName(), event.getLocale()));
    }

    /**
     * Removes the Player from the Player Locale Cache when they disconnect, but only if
     * config.yml/debug.remove_disconnected_player_locales is set to true.
     *
     * @param event Event passed from Bukkit
     * @since 1.0.0
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        if (plugin.getConfiguration().REMOVE_DISCONNECTED_PLAYER_LOCALES) {
            plugin.getPlayerLocaleCache().removeLocale(event.getPlayer().getUniqueId());
            plugin.getLogger().info(String.format(LOG_PLAYER_LOCALE_REMOVED, event.getPlayer().getName()));
        }
    }
}