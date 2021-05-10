package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.language.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import javax.annotation.*;
import java.util.*;

/**
 * A utility class for external-facing API functionality. While none of this is technically required, it makes things
 * much easier for plugin developers.
 *
 * @author Wyatt Kalmer
 */
public class ExternalUtils {
    private static final String BROADCAST_HEADER = ChatColor.GREEN + "[Broadcast] " + ChatColor.RESET;

    /**
     * Broadcasts a Localized Message to the given Collection of Players and the Console.
     *
     * @param key        Message Key to send.
     * @param players    Players to broadcast to.
     * @param formatArgs String Formatting arguments.
     * @throws NullPointerException     if Key or Players is null.
     * @throws IllegalArgumentException if Players is empty.
     * @apiNote The String formatting arguments are only formatted once per Locale to save on processing time.
     */
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey key, @Nonnull Collection<? extends Player> players, Object... formatArgs)
    {
        Objects.requireNonNull(key, "Key cannot be null.");
        Objects.requireNonNull(players, "Player Collection cannot be null.");
        if (players.isEmpty()) throw new IllegalArgumentException("Player Collection cannot be empty.");

        final Map<String, String> messageMap = new HashMap<>();
        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final PlayerLocaleCache playerLocaleCache = HippOutLocalizationLib.getPlugin().getPlayerLocaleCache();

        // Cache console language here because it's faster than finding the same message twice later.
        MessageReturnWrapper consoleMessageWrapper = languageHandler.getConsoleMessage(key);
        String consoleMessage = String.format(consoleMessageWrapper.getMessage(), formatArgs);
        messageMap.put(consoleMessageWrapper.getLocale(), consoleMessage);

        HippOutLocalizationLib.getPlugin().getLogger().info(BROADCAST_HEADER + consoleMessage);

        for (Player player : players) {
            String locale = playerLocaleCache.getPlayerLocale(player.getUniqueId());

            String message;
            if (messageMap.containsKey(locale))
                message = messageMap.get(locale);
            else {
                message = String.format(languageHandler.getLocalizedMessage(locale, key).getMessage(), formatArgs);
                messageMap.put(locale, message);
            }

            player.sendMessage(message);
        }
    }

    /**
     * Broadcasts a Localized Message to all currently online Players and the Console.
     *
     * @param key        Message Key to send.
     * @param formatArgs String Formatting arguments.
     * @throws NullPointerException if Key is null.
     * @apiNote The String formatting arguments are only formatted once per Locale to save on processing time.
     */
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey key, Object... formatArgs)
    {
        broadcastLocalizedMessage(key, Bukkit.getOnlinePlayers(), formatArgs);
    }

    /**
     * Broadcasts a Localized Message to the given Collection of Players and the Console.
     *
     * @param key     Message Key to send.
     * @param players Players to broadcast to.
     * @throws NullPointerException     if Key or Players is null.
     * @throws IllegalArgumentException if Players is empty.
     */
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey key, @Nonnull Collection<? extends Player> players)
    {
        Objects.requireNonNull(key, "Key cannot be null.");
        Objects.requireNonNull(players, "Player Collection cannot be null.");
        if (players.isEmpty()) throw new IllegalArgumentException("Player Collection cannot be empty.");

        final Map<String, String> messageMap = new HashMap<>();
        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final PlayerLocaleCache playerLocaleCache = HippOutLocalizationLib.getPlugin().getPlayerLocaleCache();

        // Cache console language here because it's faster than finding the same message twice later.
        MessageReturnWrapper consoleMessageWrapper = languageHandler.getConsoleMessage(key);
        messageMap.put(consoleMessageWrapper.getLocale(), consoleMessageWrapper.getMessage());

        HippOutLocalizationLib.getPlugin().getLogger().info(BROADCAST_HEADER + consoleMessageWrapper.getMessage());

        for (Player player : players) {
            String locale = playerLocaleCache.getPlayerLocale(player.getUniqueId());

            String message;
            if (messageMap.containsKey(locale))
                message = messageMap.get(locale);
            else {
                message = languageHandler.getLocalizedMessage(locale, key).getMessage();
                messageMap.put(locale, message);
            }

            player.sendMessage(message);
        }
    }

    /**
     * Broadcasts a Localized Message to all currently online Players and the Console.
     *
     * @param key Message Key to send.
     * @throws NullPointerException     if Key or Players is null.
     * @throws IllegalArgumentException if Players is empty.
     */
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey key)
    {
        broadcastLocalizedMessage(key, Bukkit.getOnlinePlayers());
    }

    /**
     * Sends a localized Message to a given Player.
     *
     * @param key        Message Key to send.
     * @param player     Player to send the Message to.
     * @param formatArgs String formatting arguments.
     * @throws NullPointerException if Key or Player is null.
     */
    public static void sendLocalizedMessage(@Nonnull NamespacedKey key, @Nonnull Player player, Object[] formatArgs)
    {
        Objects.requireNonNull(key, "Key cannot be null.");
        Objects.requireNonNull(player, "Player cannot be null.");

        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final PlayerLocaleCache playerLocaleCache = HippOutLocalizationLib.getPlugin().getPlayerLocaleCache();

        player.sendMessage(String.format(languageHandler.getLocalizedMessage(
                playerLocaleCache.getPlayerLocale(player.getUniqueId()), key).getMessage(), formatArgs));
    }

    /**
     * Sends a localized Message to a given Player.
     *
     * @param key    Message Key to send.
     * @param player Player to send the Message to.
     * @throws NullPointerException if Key or Player is null.
     */
    public static void sendLocalizedMessage(@Nonnull NamespacedKey key, @Nonnull Player player)
    {
        Objects.requireNonNull(key, "Key cannot be null.");
        Objects.requireNonNull(player, "Player cannot be null.");

        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final PlayerLocaleCache playerLocaleCache = HippOutLocalizationLib.getPlugin().getPlayerLocaleCache();

        player.sendMessage(languageHandler.getLocalizedMessage(
                playerLocaleCache.getPlayerLocale(player.getUniqueId()), key).getMessage());
    }
}
