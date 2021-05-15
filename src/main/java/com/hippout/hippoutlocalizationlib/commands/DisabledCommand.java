package com.hippout.hippoutlocalizationlib.commands;

import com.hippout.hippoutlocalizationlib.*;
import com.hippout.hippoutlocalizationlib.api.*;
import org.bukkit.command.*;

import javax.annotation.*;

/**
 * A Command that has been disabled.
 *
 * @since 1.0.0
 */
public class DisabledCommand implements CommandExecutor {
    public static final DisabledCommand instance = new DisabledCommand();

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label,
                             @Nonnull String[] args)
    {
        Macros.sendLocalizedMessage(HippOutLocalizationLib.getKeyRegistry().COMMAND_DISABLED, sender);
        return true;
    }
}
