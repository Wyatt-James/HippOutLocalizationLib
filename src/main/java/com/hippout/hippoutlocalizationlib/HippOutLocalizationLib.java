package com.hippout.hippoutlocalizationlib;

import com.hippout.hippoutlocalizationlib.language.*;
import com.hippout.hippoutlocalizationlib.locale.*;
import org.bukkit.plugin.java.*;

import javax.annotation.*;

/**
 * A Bukkit Plugin API to make localizing text easy.
 *
 * @author Wyatt Kalmer.
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

        this.configuration = new Configuration(this);
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
     */
    @Nonnull
    public PlayerLocaleCache getPlayerLocaleCache()
    {
        return playerLocaleCache;
    }
}
