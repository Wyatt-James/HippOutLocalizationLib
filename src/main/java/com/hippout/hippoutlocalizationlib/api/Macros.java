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
        Objects.requireNonNull(messageKey, "Message Key cannot be null.");
        Objects.requireNonNull(formatArgs, "Format Args cannot be null.");
        Objects.requireNonNull(recipients, "Recipients cannot be null.");
        if (recipients.isEmpty()) throw new IllegalArgumentException("Recipients cannot be empty.");

        final Map<String, String> messageMap = new HashMap<>();
        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final LocaleCache localeCache = HippOutLocalizationLib.getPlugin().getLocaleCache();

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
                message = StringUtils.format(languageHandler.getLocalizedMessage(locale, messageKey).getMessage(), formatArgs);

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
        broadcastLocalizedMessage(messageKey, recipients, new Object[0]);
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
     * @throws NullPointerException if messageKey, commandSender, or formatArgs is null.
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

        final String message = StringUtils.format(languageHandler.getLocalizedMessage(locale, messageKey).getMessage(),
                formatArgs);

        commandSender.sendMessage(message);
    }

    /**
     * Sends a localized Message to a given Player.
     *
     * @param messageKey    Message Key to send.
     * @param commandSender CommandSender to send the Message to.
     * @throws NullPointerException if messageKey or commandSender is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void sendLocalizedMessage(@Nonnull NamespacedKey messageKey, @Nonnull CommandSender commandSender)
    {
        sendLocalizedMessage(messageKey, commandSender, new Object[0]);
    }

    /**
     * Broadcasts a localized formatted title to the given Collection of Players.
     *
     * @param titleKey    NamespacedKey for the title. If null, sends no title.
     * @param subtitleKey NamespacedKey for the subtitle. If null, sends no subtitle.
     * @param fadeIn      time in ticks for titles to fade in.
     * @param stay        time in ticks for titles to stay.
     * @param fadeOut     time in ticks for titles to fade out.
     * @param recipients  Collection of Players to send the Title to.
     * @param formatArgs  String formatting arguments.
     * @throws NullPointerException if both titleKey and subtitleKey are null.
     * @throws NullPointerException if recipients is null.
     * @throws NullPointerException if formatArgs is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedTitle(@Nullable NamespacedKey titleKey, @Nullable NamespacedKey subtitleKey,
                                               int fadeIn, int stay, int fadeOut,
                                               @Nonnull Collection<? extends Player> recipients,
                                               @Nonnull Object... formatArgs)
    {
        Objects.requireNonNull(recipients, "Recipients cannot be null.");
        if (recipients.isEmpty()) throw new IllegalArgumentException("Recipients cannot be empty.");

        Objects.requireNonNull(formatArgs, "Format Args cannot be null.");

        if (titleKey == null && subtitleKey == null)
            throw new IllegalArgumentException("At least one key cannot be null.");

        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();

        final Map<String, String> titleMap = new HashMap<>();
        final Map<String, String> subtitleMap = new HashMap<>();

        for (Player player : recipients) {
            final String locale = getLocale(player);

            final String title;
            if (titleKey == null)
                title = null;
            else if (titleMap.containsKey(locale))
                title = titleMap.get(locale);
            else {
                title = StringUtils.format(languageHandler.getLocalizedMessage(locale, titleKey).getMessage(), formatArgs);
                titleMap.put(locale, title);
            }

            final String subtitle;
            if (subtitleKey == null)
                subtitle = null;
            else if (subtitleMap.containsKey(locale))
                subtitle = subtitleMap.get(locale);
            else {
                subtitle = StringUtils.format(languageHandler.getLocalizedMessage(locale, subtitleKey).getMessage(), formatArgs);
                subtitleMap.put(locale, subtitle);
            }

            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * Broadcasts a localized formatted title to all online Players.
     *
     * @param titleKey    NamespacedKey for the title. If null, sends no title.
     * @param subtitleKey NamespacedKey for the subtitle. If null, sends no subtitle.
     * @param fadeIn      time in ticks for titles to fade in.
     * @param stay        time in ticks for titles to stay.
     * @param fadeOut     time in ticks for titles to fade out.
     * @param formatArgs  String formatting arguments.
     * @throws NullPointerException if both titleKey and subtitleKey are null.
     * @throws NullPointerException if formatArgs is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedTitle(@Nullable NamespacedKey titleKey, @Nullable NamespacedKey subtitleKey,
                                               int fadeIn, int stay, int fadeOut, @Nonnull Object... formatArgs)
    {
        if (titleKey == null && subtitleKey == null)
            throw new IllegalArgumentException("At least one key cannot be null.");

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.size() > 0)
            broadcastLocalizedTitle(titleKey, subtitleKey, fadeIn, stay, fadeOut, onlinePlayers, formatArgs);
    }

    /**
     * Broadcasts a localized unformatted title to the given Collection of Players.
     *
     * @param titleKey    NamespacedKey for the title. If null, sends no title.
     * @param subtitleKey NamespacedKey for the subtitle. If null, sends no subtitle.
     * @param fadeIn      time in ticks for titles to fade in.
     * @param stay        time in ticks for titles to stay.
     * @param fadeOut     time in ticks for titles to fade out.
     * @param recipients  Collection of Players to send the Title to.
     * @throws NullPointerException if both titleKey and subtitleKey are null
     * @throws NullPointerException if recipients is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedTitle(@Nullable NamespacedKey titleKey, @Nullable NamespacedKey subtitleKey,
                                               int fadeIn, int stay, int fadeOut,
                                               @Nonnull Collection<? extends Player> recipients)
    {
        if (titleKey == null && subtitleKey == null)
            throw new IllegalArgumentException("At least one key cannot be null.");

        broadcastLocalizedTitle(titleKey, subtitleKey, fadeIn, stay, fadeOut, recipients, new Object[0]);
    }

    /**
     * Broadcasts a localized unformatted title to all online Players.
     *
     * @param titleKey    NamespacedKey for the title. If null, sends no title.
     * @param subtitleKey NamespacedKey for the subtitle. If null, sends no subtitle.
     * @param fadeIn      time in ticks for titles to fade in.
     * @param stay        time in ticks for titles to stay.
     * @param fadeOut     time in ticks for titles to fade out.
     * @throws NullPointerException if both titleKey and subtitleKey are null
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void broadcastLocalizedTitle(@Nullable NamespacedKey titleKey, @Nullable NamespacedKey subtitleKey,
                                               int fadeIn, int stay, int fadeOut)
    {
        if (titleKey == null && subtitleKey == null)
            throw new IllegalArgumentException("At least one key cannot be null.");

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.size() > 0)
            broadcastLocalizedTitle(titleKey, subtitleKey, fadeIn, stay, fadeOut, onlinePlayers);
    }

    /**
     * Sends a localized Title to the given Player.
     *
     * @param player      Player to send to.
     * @param titleKey    NamespacedKey for the title. If null, sends no title.
     * @param subtitleKey NamespacedKey for the subtitle. If null, sends no subtitle.
     * @param fadeIn      time in ticks for titles to fade in.
     * @param stay        time in ticks for titles to stay.
     * @param fadeOut     time in ticks for titles to fade out.
     * @param formatArgs  String formatting arguments.
     * @throws NullPointerException if both titleKey and subtitleKey are null
     * @throws NullPointerException if formatArgs is null.
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void sendLocalizedTitle(@Nonnull Player player, @Nullable NamespacedKey titleKey,
                                          @Nullable NamespacedKey subtitleKey, int fadeIn, int stay, int fadeOut,
                                          @Nonnull Object... formatArgs)
    {
        Objects.requireNonNull(player);
        if (titleKey == null && subtitleKey == null)
            throw new IllegalArgumentException("At least one key cannot be null.");

        Objects.requireNonNull(formatArgs, "Format Args cannot be null.");

        final LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        final String locale = getLocale(player);

        final String title;
        if (titleKey == null)
            title = null;
        else
            title = StringUtils.format(languageHandler.getLocalizedMessage(locale, titleKey).getMessage(), formatArgs);

        final String subtitle;
        if (subtitleKey == null)
            subtitle = null;
        else
            subtitle = StringUtils.format(languageHandler.getLocalizedMessage(locale, subtitleKey).getMessage(), formatArgs);

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }


    /**
     * Sends a localized Title to the given Player.
     *
     * @param player      Player to send to.
     * @param titleKey    NamespacedKey for the title. If null, sends no title.
     * @param subtitleKey NamespacedKey for the subtitle. If null, sends no subtitle.
     * @param fadeIn      time in ticks for titles to fade in.
     * @param stay        time in ticks for titles to stay.
     * @param fadeOut     time in ticks for titles to fade out.
     * @throws NullPointerException if both titleKey and subtitleKey are null
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static void sendLocalizedTitle(@Nonnull Player player, @Nullable NamespacedKey titleKey,
                                          @Nullable NamespacedKey subtitleKey, int fadeIn, int stay, int fadeOut)
    {
        sendLocalizedTitle(player, titleKey, subtitleKey, fadeIn, stay, fadeOut, new Object[0]);
    }

    /**
     * Returns a localized Message with the given CommandSender's Locale.
     *
     * @param messageKey    Message Key to retrieve.
     * @param commandSender CommandSender to get the Locale of.
     * @return A Localized Message String with the given CommandSender's Locale.
     * @throws NullPointerException if MessageKey or commandSender is null.
     * @since 1.0.0
     */
    @Nonnull
    @SuppressWarnings("unused")
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
     * @return A Localized Message String with the given Locale.
     * @throws NullPointerException if MessageKey or commandSender is null.
     * @since 1.0.0
     */
    @Nonnull
    @SuppressWarnings("unused")
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
    @Nonnull
    @SuppressWarnings("unused")
    public static String getLocale(@Nonnull CommandSender commandSender)
    {
        Objects.requireNonNull(commandSender, "Command Sender cannot be null.");

        HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();
        String locale;

        if (commandSender instanceof Player)
            locale = plugin.getLocaleCache().getLocale(((Player) commandSender).getUniqueId());

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
    @Nonnull
    @SuppressWarnings("unused")
    public static String getLocaleNoOverride(@Nonnull CommandSender commandSender)
    {
        Objects.requireNonNull(commandSender, "Command sender cannot be null.");

        HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();
        String locale;

        if (commandSender instanceof Player)
            locale = plugin.getLocaleCache().getLocaleNoOverride(((Player) commandSender).getUniqueId());

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
