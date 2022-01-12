package com.hippout.hippoutlocalizationlib;

import org.bukkit.*;

import javax.annotation.*;
import java.util.*;

/**
 * An object that contains keys for all messages in HippOutLocalizationLib.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class KeyRegistry {
    public final NamespacedKey TEST_MESSAGE;

    public final NamespacedKey INVALID_FORMAT_LOCALE, COMMAND_DISABLED, PLAYER_NOT_FOUND;

    public final NamespacedKey TARGETARGS_INVALID_TYPE, TARGETARGS_INVALID_FORMAT;

    public final NamespacedKey COM_GENERIC_PERMISSION_ERROR, COM_GENERIC_PERMISSION_ERROR_MANAGE_SELF,
            COM_GENERIC_PERMISSION_ERROR_MANAGE_ALL, COM_GENERIC_PERMISSION_ERROR_CHECK_SELF,
            COM_GENERIC_PERMISSION_ERROR_CHECK_ALL;

    public final NamespacedKey COM_LOCALE_USAGE, COM_LOCALE_SUCCESS, COM_LOCALE_SUCCESS_OVERRIDE;

    public final NamespacedKey COM_SETLOCALEOVERRIDE_USAGE, COM_SETLOCALEOVERRIDE_SUCCESS;

    public final NamespacedKey COM_REMLOCALEOVERRIDE_USAGE, COM_REMLOCALEOVERRIDE_SUCCESS,
            COM_REMLOCALEOVERRIDE_NO_OVERRIDE;

    private final HippOutLocalizationLib plugin;

    /**
     * Constructs a KeyRegistry.
     *
     * @since 1.0.0
     */
    public KeyRegistry()
    {
        this.plugin = HippOutLocalizationLib.getPlugin();

        TEST_MESSAGE = getKey("test_message");

        INVALID_FORMAT_LOCALE = getKey("invalid_format_locale");
        COMMAND_DISABLED = getKey("command_disabled");
        PLAYER_NOT_FOUND = getKey("player_not_found");

        TARGETARGS_INVALID_TYPE = getKey("targetargs.invalid_type");
        TARGETARGS_INVALID_FORMAT = getKey("targetargs.invalid_format");

        COM_GENERIC_PERMISSION_ERROR = getKey("command.generic.permission_error");
        COM_GENERIC_PERMISSION_ERROR_MANAGE_SELF = getKey("command.generic.permission_error_manage_self");
        COM_GENERIC_PERMISSION_ERROR_MANAGE_ALL = getKey("command.generic.permission_error_manage_all");
        COM_GENERIC_PERMISSION_ERROR_CHECK_SELF = getKey("command.generic.permission_error_check_self");
        COM_GENERIC_PERMISSION_ERROR_CHECK_ALL = getKey("command.generic.permission_error_check_all");

        COM_LOCALE_USAGE = getKey("command.locale.usage");
        COM_LOCALE_SUCCESS = getKey("command.locale.success");
        COM_LOCALE_SUCCESS_OVERRIDE = getKey("command.locale.success_override");

        COM_SETLOCALEOVERRIDE_USAGE = getKey("command.setlocaleoverride.usage");
        COM_SETLOCALEOVERRIDE_SUCCESS = getKey("command.setlocaleoverride.success");

        COM_REMLOCALEOVERRIDE_USAGE = getKey("command.removelocaleoverride.usage");
        COM_REMLOCALEOVERRIDE_SUCCESS = getKey("command.removelocaleoverride.success");
        COM_REMLOCALEOVERRIDE_NO_OVERRIDE = getKey("command.removelocaleoverride.no_override");
    }

    /**
     * Gets an existing NamespacedKey with the HippOutLocalizationLib plugin.
     *
     * @param key Key to search for.
     * @return The found key.
     * @throws IllegalArgumentException if the requested NamespacedKey could not be found.
     * @since 1.0.0
     */
    private NamespacedKey getKey(@Nonnull String key)
    {
        Objects.requireNonNull(key, "Key cannot be null.");

        return HippOutLocalizationLib.getPlugin().getLanguageHandler().getExistingKey(plugin, key);
    }
}
