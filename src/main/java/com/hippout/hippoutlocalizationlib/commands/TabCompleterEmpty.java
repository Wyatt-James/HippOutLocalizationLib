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
     * Constructs a TabCompleterempty.
     *
     * @since 1.0.0
     */
    private TabCompleterEmpty()
    {
        // Nothing
    }

    /**
     * Handles Tab-completion.
     *
     * @param sender  CommandSender.
     * @param command Command.
     * @param alias   Alias String.
     * @param args    Arguments.
     * @return A List of Tab Completions.
     * @since 1.0.0
     */
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias,
                                      @Nonnull String[] args)
    {
        return new LinkedList<>();
    }
}
