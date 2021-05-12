package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.language.*;
import com.hippout.hippoutlocalizationlib.locale.*;
import org.bukkit.plugin.java.*;

import javax.annotation.*;

/**
 * A Bukkit Plugin API to make localizing plugins easy.
 *
 * @author Wyatt Kalmer.
 * @since 1.0.0
 */
public class HippOutLocalizationLib extends JavaPlugin {
    public static final String FAILSAFE_LOCALE = "failsafe_lc";

    private static HippOutLocalizationLib instance;

    private Configuration configuration;
    private LanguageHandler languageHandler;
    private PlayerLocaleCache playerLocaleCache;

    private EventListener eventListener;

    @Override
    public void onEnable()
    {
        getLogger().info("HippOutLocalizationLib has been enabled.");

        instance = this;
        saveDefaultConfig();

        try {
            this.configuration = new Configuration(this);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Configuration file config.yml. Plugin load failed. " +
                    "Contact the plugin vendor for assistance.");
        }

        this.languageHandler = new LanguageHandler(this, configuration.DEFAULT_LOCALE);
        this.playerLocaleCache = new PlayerLocaleCache(this);
        this.eventListener = new EventListener(this);

        getServer().getPluginManager().registerEvents(this.eventListener, this);
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
     * Returns the current PlayerLocaleCache.
     *
     * @return the current PlayerLocaleCache.
     * @since 1.0.0
     */
    @Nonnull
    public PlayerLocaleCache getPlayerLocaleCache()
    {
        return playerLocaleCache;
    }
}
