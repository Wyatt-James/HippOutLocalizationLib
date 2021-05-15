package com.hippout.hippoutlocalizationlib.commands;

import org.bukkit.command.*;

import javax.annotation.*;
import java.util.*;

/**
 * Tab Completer for an empty Tab Completion.
 *
 * @author Wyatt Kalmer
 * @since 1.0.0
 */
public class TabCompleterEmpty implements TabCompleter {
    public static final TabCompleterEmpty INSTANCE = new TabCompleterEmpty();

    /**
     * Private constructor. Use INSTANCE instead.
     *
     * @since 1.0.0
     */
    private TabCompleterEmpty()
    {
        // Nothing
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias,
                                      @Nonnull String[] args)
    {
        return new LinkedList<>();
    }
}
