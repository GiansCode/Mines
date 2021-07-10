package io.alerium.mines.commands;

import io.alerium.mines.MinesPlugin;
import io.alerium.mines.guis.BalanceGUI;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class MinesCommand implements CommandExecutor {

    private final MinesPlugin plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return true;

        BalanceGUI.open(plugin, (Player) sender);
        return true;
    }

}
