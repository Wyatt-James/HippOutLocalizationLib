package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.api.*;
import com.hippout.hippoutlocalizationlib.commands.*;
import com.hippout.hippoutlocalizationlib.language.*;
import com.hippout.hippoutlocalizationlib.locale.EventListener;
import com.hippout.hippoutlocalizationlib.locale.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.*;

import javax.annotation.*;
import java.io.*;
import java.util.*;

/**
 * A Bukkit Plugin API to make localizing plugins easy.
 *
 * @author Wyatt Kalmer.
 * @since 1.0.0
 */
public class HippOutLocalizationLib extends JavaPlugin {
    public static final String FAILSAFE_LOCALE = "failsafe_lc";

    private static HippOutLocalizationLib instance;

    private KeyRegistry keyRegistry;

    private Configuration configuration;
    private LanguageHandler languageHandler;
    private LocaleCache localeCache;

    private EventListener eventListener;

    /**
     * Returns the Key Registry of the current HippOutLocalizationLib instance.
     *
     * @return The Key Registry of the current HippOutLocalizationLib instance.
     * @since 1.0.0
     */
    public static KeyRegistry getKeyRegistry()
    {
        return instance.keyRegistry;
    }

    @Override
    public void onLoad()
    {
        // Nothing
    }

    @Override
    public void onDisable()
    {
        getLogger().info("HippOutLocalizationLib has been disabled.");
    }

    /**
     * Returns the current instance of HippOutLocalizationLib.
     *
     * @return the current instance of HippOutLocalizationLib.
     * @since 1.0.0
     */
    @Nonnull
    public static HippOutLocalizationLib getPlugin()
    {
        return instance;
    }

    /**
     * Returns the current Configuration.
     *
     * @return the current Configuration.
     * @since 1.0.0
     */
    @Nonnull
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * Returns the current LanguageHandler.
     *
     * @return the current LanguageHandler.
     * @since 1.0.0
     */
    @Nonnull
    public LanguageHandler getLanguageHandler()
    {
        return languageHandler;
    }

    /**
     * Returns the current LocaleCache.
     *
     * @return the current LocaleCache.
     * @since 1.0.0
     */
    @Nonnull
    public LocaleCache getPlayerLocaleCache()
    {
        return localeCache;
    }

    @Override
    public void onEnable()
    {
        getLogger().info("HippOutLocalizationLib has been enabled.");

        instance = this;
        saveDefaultConfig();
        saveResource("languages" + File.separator + "en.yml", false);

        try {
            this.configuration = new Configuration(this);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Configuration file config.yml. Plugin load failed. " +
                    "Contact the plugin vendor for assistance.");
        }

        this.languageHandler = new LanguageHandler(this, configuration.DEFAULT_LOCALE);
        this.localeCache = new LocaleCache(this, Bukkit.getOnlinePlayers());
        this.eventListener = new EventListener(this);

        getServer().getPluginManager().registerEvents(this.eventListener, this);

        LanguageLoader languageLoader = new LanguageLoader(this, "languages");

        for (String language : configuration.getLanguageFileDefinitions()) {
            try {
                final FileConfiguration fc = languageLoader.loadLanguageConfig(language + ".yml");
                final String[] aliases = fc.getStringList("config.aliases").toArray(new String[0]);
                final ConfigurationSection messageSection = Objects.requireNonNull(fc.getConfigurationSection(
                        "messages"), "ConfigurationSection messageSection could not be found in language" +
                        " file " + language);

                languageLoader.loadLanguage(messageSection, aliases);
            } catch (IOException e) {
                getLogger().warning(language + " could not be found inside the languages directory.");
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                getLogger().warning(language + " was not a valid YAML configuration file.");
                e.printStackTrace();
            } catch (NullPointerException e) {
                getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }

        keyRegistry = new KeyRegistry();

        final PluginCommand pCommandSetLocaleOverride = getCommand("setlocaleoverride");
        final PluginCommand pCommandRemoveLocaleOverride = getCommand("removelocaleoverride");

        if (configuration.ENABLE_LOCALE_OVERRIDES) {
            final CommandSetLocaleOverride commandSetLocaleOverride = new CommandSetLocaleOverride();
            pCommandSetLocaleOverride.setExecutor(commandSetLocaleOverride);
            pCommandSetLocaleOverride.setTabCompleter(commandSetLocaleOverride);


            final CommandRemoveLocaleOverride commandRemoveLocaleOverride = new CommandRemoveLocaleOverride();
            pCommandRemoveLocaleOverride.setExecutor(commandRemoveLocaleOverride);
            pCommandRemoveLocaleOverride.setTabCompleter(commandRemoveLocaleOverride);
        } else {
            pCommandSetLocaleOverride.setExecutor(DisabledCommand.instance);
            pCommandSetLocaleOverride.setTabCompleter(TabCompleterEmpty.INSTANCE);

            pCommandRemoveLocaleOverride.setExecutor(DisabledCommand.instance);
            pCommandRemoveLocaleOverride.setTabCompleter(TabCompleterEmpty.INSTANCE);
        }

        final PluginCommand pCommandLocale = getCommand("locale");
        final CommandLocale commandLocale = new CommandLocale();
        pCommandLocale.setExecutor(commandLocale);
        pCommandLocale.setTabCompleter(commandLocale);
    }
}
