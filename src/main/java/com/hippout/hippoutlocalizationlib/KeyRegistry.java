package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.language.*;
import org.bukkit.*;

/**
 * An object that contains keys for all messages in HippOutLocalizationLib.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class KeyRegistry {
    public final NamespacedKey INVALID_FORMAT_LOCALE, COMMAND_DISABLED, PLAYER_NOT_FOUND;

    public final NamespacedKey TARGETARGS_INVALID_TYPE, TARGETARGS_INVALID_FORMAT;

    public final NamespacedKey COM_LOCALE_USAGE, COM_LOCALE_SUCCESS, COM_LOCALE_SUCCESS_OVERRIDE, COM_LOCALE_NO_LOCALE;

    public final NamespacedKey COM_SETLOCALEOVERRIDE_USAGE, COM_SETLOCALEOVERRIDE_SUCCESS;

    public final NamespacedKey COM_REMLOCALEOVERRIDE_USAGE, COM_REMLOCALEOVERRIDE_SUCCESS,
            COM_REMLOCALEOVERRIDE_NO_OVERRIDE;

    /**
     * Constructs a KeyRegistry.
     *
     * @since 1.0.0
     */
    public KeyRegistry()
    {
        LanguageHandler languageHandler = HippOutLocalizationLib.getPlugin().getLanguageHandler();
        HippOutLocalizationLib plugin = HippOutLocalizationLib.getPlugin();

        INVALID_FORMAT_LOCALE = languageHandler.getKey(plugin, "invalid_format_locale");
        COMMAND_DISABLED = languageHandler.getKey(plugin, "command_disabled");
        PLAYER_NOT_FOUND = languageHandler.getKey(plugin, "player_not_found");

        TARGETARGS_INVALID_TYPE = languageHandler.getKey(plugin, "targetargs.invalid_type");
        TARGETARGS_INVALID_FORMAT = languageHandler.getKey(plugin, "targetargs.invalid_format");

        COM_LOCALE_USAGE = languageHandler.getKey(plugin, "command.locale.usage");
        COM_LOCALE_SUCCESS = languageHandler.getKey(plugin, "command.locale.success");
        COM_LOCALE_SUCCESS_OVERRIDE = languageHandler.getKey(plugin, "command.locale.success_override");
        COM_LOCALE_NO_LOCALE = languageHandler.getKey(plugin, "command.locale.no_locale");

        COM_SETLOCALEOVERRIDE_USAGE = languageHandler.getKey(plugin, "command.setlocaleoverride.usage");
        COM_SETLOCALEOVERRIDE_SUCCESS = languageHandler.getKey(plugin, "command.setlocaleoverride.success");

        COM_REMLOCALEOVERRIDE_USAGE = languageHandler.getKey(plugin, "command.removelocaleoverride.usage");
        COM_REMLOCALEOVERRIDE_SUCCESS = languageHandler.getKey(plugin, "command.removelocaleoverride.success");
        COM_REMLOCALEOVERRIDE_NO_OVERRIDE = languageHandler.getKey(plugin, "command.removelocaleoverride.no_override");
    }
}
