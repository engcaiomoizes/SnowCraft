package br.com.caiomoizes.snowCraft.commands;

import br.com.caiomoizes.snowCraft.SnowCraft;
import br.com.caiomoizes.snowCraft.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Pay implements CommandExecutor {

    private final EconomyManager economy;

    public Pay(SnowCraft snowcraft) {
        this.economy = snowcraft.getEconomyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player from) || args.length != 2) return false;

        Player to = Bukkit.getPlayer(args[0]);
        double amount;

        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException ex) {
            from.sendMessage(Component.text("Valor inválido!", NamedTextColor.RED));
            return true;
        }

        if (to == null || to.equals(from)) {
            from.sendMessage(Component.text("Jogador inválido!", NamedTextColor.RED));
            return true;
        }

        if (economy.transfer(from, to, amount)) {
            from.sendMessage(
                    Component.text("Você enviou ", NamedTextColor.GRAY)
                            .append(Component.text(amount, NamedTextColor.GOLD, TextDecoration.BOLD))
                            .append(Component.text(" para ", NamedTextColor.GRAY))
                            .append(Component.text(to.getName(), NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            );
            to.sendMessage(
                    Component.text("Você recebeu ", NamedTextColor.GRAY)
                            .append(Component.text(amount, NamedTextColor.GOLD, TextDecoration.BOLD))
                            .append(Component.text(" de ", NamedTextColor.GRAY))
                            .append(Component.text(from.getName(), NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            );
        } else {
            from.sendMessage(Component.text("Saldo insuficiente!", NamedTextColor.DARK_RED));
        }

        return true;
    }
}