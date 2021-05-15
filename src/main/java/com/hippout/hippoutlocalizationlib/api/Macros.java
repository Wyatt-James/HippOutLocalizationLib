package com.hippout.hippoutlocalizationlib.api;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.language.*;
import com.hippout.hippoutlocalizationlib.locale.*;
import com.hippout.hippoutlocalizationlib.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import javax.annotation.*;
import java.util.*;

/**
 * A utility class for external-facing API macro functionality. While none of this is technically required, it makes
 * things much easier for plugin developers.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class Macros {
    private static final String BROADCAST_HEADER = ChatColor.GREEN + "[Broadcast] " + ChatColor.RESET;

    /**
     * Broadcasts a Localized Message to the given Collection of Players and the Console.
     *
     * @param messageKey Message Key to send.
     * @param recipients CommandSenders to broadcast to. Note that they may not have actually sent any Command.
     * @param formatArgs String Formatting arguments.
     * @throws NullPointerException     if MessageKey, Players, or formatArgs is null.
     * @throws IllegalArgumentException if Recipients is empty.
     * @api.Note The String formatting arguments are only formatted once per Locale to save on processing time.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey messageKey,
                                                 @Nonnull Collection<? extends CommandSender> recipients,
                                                 @Nonnull Object... formatArgs)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");
        Objects.requireNonNull(formatArgs, "Format Args cannot be null.");
        Objects.requireNonNull(recipients, "Recipients cannot be null.");
        if (recipients.isEmpty()) throw new IllegalArgumentException("Recipients cannot be empty.");

        final Map<String, String> messageMap = new HashMap<>();
        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getPlayerLocaleCache();

        // Cache console language here because it's faster than finding the same message twice later.
        MessageReturnWrapper consoleMessageWrapper = languageHandler.getConsoleMessage(messageKey);
        String consoleMessage = String.format(consoleMessageWrapper.getMessage(), formatArgs);
        messageMap.put(consoleMessageWrapper.getLocale(), consoleMessage);

        HippOutLocalizationLib.getPlugin().getLogger().info(BROADCAST_HEADER + consoleMessage);

        for (CommandSender sender : recipients) {
            final String locale = getLocale(sender);

            String message;
            if (messageMap.containsKey(locale))
                message = messageMap.get(locale);
            else {
                message = String.format(languageHandler.getLocalizedMessage(locale, messageKey).getMessage(), formatArgs);
                messageMap.put(locale, message);
            }

            sender.sendMessage(message);
        }
    }

    /**
     * Broadcasts a Localized Message to all currently online Players and the Console.
     *
     * @param messageKey Message Key to send.
     * @param formatArgs String Formatting arguments.
     * @throws NullPointerException if MessageKey or formatArgs is null.
     * @api.Note The String formatting arguments are only formatted once per Locale to save on processing time.
     * @api.Note Ends silently if no Players are online.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey messageKey, @Nonnull Object... formatArgs)
    {
        Objects.requireNonNull(messageKey, "Message Key cannot be null.");
        Objects.requireNonNull(formatArgs, "Format Args cannot be null.");

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.size() > 0)
            broadcastLocalizedMessage(messageKey, onlinePlayers, formatArgs);
    }

    /**
     * Broadcasts a Localized Message to the given Collection of Players and the Console.
     *
     * @param messageKey Message Key to send.
     * @param recipients CommandSenders to broadcast to. Note that they may not have actually sent the Command.
     * @throws NullPointerException     if MessageKey or Players is null.
     * @throws IllegalArgumentException if Recipients is empty.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey messageKey,
                                                 @Nonnull Collection<? extends CommandSender> recipients)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");
        Objects.requireNonNull(recipients, "Recipients cannot be null.");
        if (recipients.isEmpty()) throw new IllegalArgumentException("Recipients cannot be empty.");

        final Map<String, String> messageMap = new HashMap<>();
        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getPlayerLocaleCache();

        // Cache console language here because it's faster than finding the same message twice later.
        MessageReturnWrapper consoleMessageWrapper = languageHandler.getConsoleMessage(messageKey);
        messageMap.put(consoleMessageWrapper.getLocale(), consoleMessageWrapper.getMessage());

        HippOutLocalizationLib.getPlugin().getLogger().info(BROADCAST_HEADER + consoleMessageWrapper.getMessage());

        for (CommandSender sender : recipients) {
            final String locale = getLocale(sender);

            String message;
            if (messageMap.containsKey(locale))
                message = messageMap.get(locale);
            else {
                message = languageHandler.getLocalizedMessage(locale, messageKey).getMessage();
                messageMap.put(locale, message);
            }

            sender.sendMessage(message);
        }
    }

    /**
     * Broadcasts a Localized Message to all currently online Players and the Console.
     *
     * @param messageKey Message Key to send.
     * @throws NullPointerException if MessageKey is null.
     * @api.Note Ends silently if no Players are online.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedMessage(@Nonnull NamespacedKey messageKey)
    {
        Objects.requireNonNull(messageKey, "Message Key cannot be null.");

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.size() > 0)
            broadcastLocalizedMessage(messageKey, onlinePlayers);
    }

    /**
     * Sends a localized Message to a given Player.
     *
     * @param messageKey    Message Key to send.
     * @param commandSender CommandSender to send the Message to.
     * @param formatArgs    String formatting arguments.
     * @throws NullPointerException  if MessageKey, commandSender, or formatArgs is null.
     * @throws IllegalStateException if Player is offline.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void sendLocalizedMessage(@Nonnull NamespacedKey messageKey, @Nonnull CommandSender commandSender,
                                            @Nonnull Object... formatArgs)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");
        Objects.requireNonNull(commandSender, "Command Sender cannot be null.");
        Objects.requireNonNull(formatArgs, "Format Args cannot be null.");

        final String locale = getLocale(commandSender);
        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();

        commandSender.sendMessage(String.format(languageHandler.getLocalizedMessage(locale, messageKey).getMessage(), formatArgs));
    }

    /**
     * Sends a localized Message to a given Player.
     *
     * @param messageKey    Message Key to send.
     * @param commandSender CommandSender to send the Message to.
     * @throws NullPointerException  if MessageKey or commandSender is null.
     * @throws IllegalStateException if Player is offline.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void sendLocalizedMessage(@Nonnull NamespacedKey messageKey, @Nonnull CommandSender commandSender)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");
        Objects.requireNonNull(commandSender, "Command Sender cannot be null.");

        final String locale = getLocale(commandSender);
        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();

        commandSender.sendMessage(languageHandler.getLocalizedMessage(locale, messageKey).getMessage());
    }

    /**
     * Returns a localized Message with the given CommandSender's Locale.
     *
     * @param messageKey    Message Key to retrieve.
     * @param commandSender CommandSender to get the Locale of.
     * @return A Localized MEssage String with the given CommandSender's Locale.
     * @throws NullPointerException if MessageKey or commandSender is null.
     * @since 1.0.0
     */
    public static String getLocalizedMessage(@Nonnull NamespacedKey messageKey, @Nonnull CommandSender commandSender)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");
        Objects.requireNonNull(commandSender, "Command Sender cannot be null.");

        return getLocalizedMessage(messageKey, getLocale(commandSender));
    }

    /**
     * Returns a localized Message with the given Locale.
     *
     * @param messageKey Message Key to retrieve.
     * @param locale     Locale String to get the message from.
     * @return A Localized MEssage String with the given Locale.
     * @throws NullPointerException if MessageKey or commandSender is null.
     * @since 1.0.0
     */
    public static String getLocalizedMessage(@Nonnull NamespacedKey messageKey, @Nonnull String locale)
    {
        Objects.requireNonNull(messageKey, "Key cannot be null.");
        Objects.requireNonNull(locale, "Locale cannot be null.");

        if (HippOutLocalizationLib.getPlugin().getConfiguration().API_REGEX_LOCALE_TESTS)
            ValidationUtil.validateLocale(locale);

        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        return languageHandler.getLocalizedMessage(locale, messageKey).getMessage();
    }

    /**
     * Fetches the Locale of the given CommandSender. For ProxiedCommandSenders, recursively fetches caller.
     *
     * @param commandSender CommandSender to get the Locale of.
     * @return The Locale. May be a default.
     * @throws NullPointerException if commandSender is null.
     * @throws NullPointerException if commandSender is of type org.bukkit.Player and their UUID is not present in the
     *                              LocaleCache.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static String getLocale(@Nonnull CommandSender commandSender)
    {
        Objects.requireNonNull(commandSender, "Command sender cannot be null.");

        HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();
        String locale;

        if (commandSender instanceof Player)
            locale = plugin.getPlayerLocaleCache().getLocale(((Player) commandSender).getUniqueId());

        else if (commandSender instanceof ConsoleCommandSender)
            locale = plugin.getConfiguration().CONSOLE_LOCALE;

        else if (commandSender instanceof RemoteConsoleCommandSender)
            locale = plugin.getConfiguration().REMOTE_CONSOLE_LOCALE;

        else if (commandSender instanceof ProxiedCommandSender)
            return getLocale(((ProxiedCommandSender) commandSender).getCaller());

        else
            locale = plugin.getConfiguration().DEFAULT_LOCALE;

        return locale;
    }

    /**
     * Fetches the Locale of the given CommandSender, excluding any Overrides. For ProxiedCommandSenders, recursively
     * fetches caller.
     *
     * @param commandSender CommandSender to get the Locale of.
     * @return The Locale. May be a default.
     * @throws NullPointerException if commandSender is null.
     * @throws NullPointerException if commandSender is of type org.bukkit.Player and their UUID is not present in the
     *                              LocaleCache.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static String getLocaleNoOverride(@Nonnull CommandSender commandSender)
    {
        Objects.requireNonNull(commandSender, "Command sender cannot be null.");

        HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();
        String locale;

        if (commandSender instanceof Player)
            locale = plugin.getPlayerLocaleCache().getLocaleNoOverride(((Player) commandSender).getUniqueId());

        else if (commandSender instanceof ConsoleCommandSender)
            locale = plugin.getConfiguration().CONSOLE_LOCALE;

        else if (commandSender instanceof RemoteConsoleCommandSender)
            locale = plugin.getConfiguration().REMOTE_CONSOLE_LOCALE;

        else if (commandSender instanceof ProxiedCommandSender)
            return getLocale(((ProxiedCommandSender) commandSender).getCaller());

        else
            locale = plugin.getConfiguration().DEFAULT_LOCALE;

        return locale;
    }
}
