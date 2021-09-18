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
    public static final String LANGUAGE_DIRECTORY = "languages";

    private static HippOutLocalizationLib instance;

    private KeyRegistry keyRegistry;

    private Configuration configuration;
    private LanguageHandler languageHandler;
    private LocaleCache localeCache;

    private EventListener eventListener;

    @Override
    public void onEnable()
    {
        getLogger().info("HippOutLocalizationLib has been enabled.");

        instance = this;
        saveDefaultConfig();
        saveResource(LANGUAGE_DIRECTORY + File.separator + "en.yml", false);

        try {
            this.configuration = new Configuration(this);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Configuration file config.yml. Plugin load failed. " +
                    "Contact the plugin vendor for assistance.");
        }

        this.languageHandler = new LanguageHandler(this, configuration.DEFAULT_LOCALE);

        this.localeCache = new LocaleCache(this, Bukkit.getOnlinePlayers());
        if (configuration.SAVE_AND_LOAD_LOCALE_OVERRIDES)
            loadLocaleCacheFromDisk(Configuration.LOCALE_CACHE_FILE_NAME);

        this.eventListener = new EventListener(this);

        getServer().getPluginManager().registerEvents(this.eventListener, this);

        LanguageLoader languageLoader = new LanguageLoader(this, LANGUAGE_DIRECTORY,
                configuration.SUPPRESS_SECTION_WARNINGS);

        for (String fileName : configuration.getLanguageFileDefinitions()) {
            try {
                languageLoader.loadLanguageFile(fileName);
            } catch (IOException e) {
                getLogger().warning(fileName + " could not be found or loaded inside the languages directory.");
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                getLogger().warning(fileName + " was not a valid YAML configuration file.");
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

    @Override
    public void onDisable()
    {
        getLogger().info("HippOutLocalizationLib has been disabled.");

        if (configuration.SAVE_AND_LOAD_LOCALE_OVERRIDES)
            saveLocaleCacheToDisk(Configuration.LOCALE_CACHE_FILE_NAME);
    }

    @Override
    public void onLoad()
    {
        // Nothing
    }

    // --------------- Helpers ---------------

    /**
     * Saves the LocaleCache to disk.
     *
     * @param localeCacheFileName File Name to save to.
     * @throws NullPointerException  if localeCacheFileName is null.
     * @throws IllegalStateException if localeCacheFileName is empty.
     * @since 1.0.0
     */
    private void saveLocaleCacheToDisk(@Nonnull String localeCacheFileName)
    {
        Objects.requireNonNull(localeCacheFileName, "Locale Cache File Name cannot be null.");
        if (localeCacheFileName.isEmpty())
            throw new IllegalArgumentException("Locale Cache File Name cannot be empty.");

        getLogger().info(String.format("Attempting to save Locale Cache to file %s.", localeCacheFileName));

        // Load File
        final File cacheFile = new File(getDataFolder(), localeCacheFileName);

        boolean exists = cacheFile.exists();
        if (!exists) {
            try {
                cacheFile.createNewFile();
                exists = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (exists) {
            if (cacheFile.canRead() && cacheFile.canWrite()) {
                final YamlConfiguration localeCacheConfig = YamlConfiguration.loadConfiguration(cacheFile);
                localeCacheConfig.options().header(Configuration.LOCALE_CACHE_HEADER);
                localeCache.writeOverrides(localeCacheConfig);

                try {
                    localeCacheConfig.save(cacheFile);
                } catch (IOException e) {
                    getLogger().warning(String.format("Could not save Player Locale Overrides to file %s", localeCacheFileName));
                    e.printStackTrace();
                }
            }
        } else {
            getLogger().warning(String.format("Could not find or create config file %s. Saving Locale Cache " +
                    "aborted.", localeCacheFileName));
        }
    }

    /**
     * Loads the LocaleCache from disk.
     *
     * @param localeCacheFileName File Name to load from.
     * @throws NullPointerException  if localeCacheFileName is null.
     * @throws IllegalStateException if localeCacheFileName is empty.
     * @since 1.0.0
     */
    private void loadLocaleCacheFromDisk(@Nonnull String localeCacheFileName)
    {
        Objects.requireNonNull(localeCacheFileName, "Locale Cache File Name cannot be null.");
        if (localeCacheFileName.isEmpty())
            throw new IllegalArgumentException("Locale Cache File Name cannot be empty.");

        getLogger().info(String.format("Attempting to load Locale Cache from file %s.", localeCacheFileName));

        // Load file
        final File configFile = new File(getDataFolder(), localeCacheFileName);

        boolean exists = configFile.exists();
        if (!exists) {
            try {
                configFile.createNewFile();
                exists = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (exists) {
            try {
                final YamlConfiguration localeCacheConfig = new YamlConfiguration();
                localeCacheConfig.load(configFile); // Load explicitly for exceptions.

                this.localeCache.loadOverrides(localeCacheConfig);
            } catch (IOException | InvalidConfigurationException e) {
                getLogger().warning(String.format("Could not load Player Locale Overrides from file %s",
                        localeCacheFileName));
                e.printStackTrace();
            }
        } else {
            getLogger().warning(String.format("Could not find or create config file %s. Loading Locale Cache " +
                    "aborted.", localeCacheFileName));
        }
    }


    // --------------- Getters and Setters ---------------

    /**
     * Returns the KeyRegistry of the current HippOutLocalizationLib instance.
     *
     * @return The KeyRegistry of the current HippOutLocalizationLib instance.
     * @since 1.0.0
     */
    @Nonnull
    public static KeyRegistry getKeyRegistry()
    {
        return instance.keyRegistry;
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
    public LocaleCache getLocaleCache()
    {
        return localeCache;
    }
}
