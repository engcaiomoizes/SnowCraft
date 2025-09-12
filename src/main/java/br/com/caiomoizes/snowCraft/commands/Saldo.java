package br.com.caiomoizes.snowCraft.commands;

import br.com.caiomoizes.snowCraft.SnowCraft;
import br.com.caiomoizes.snowCraft.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Saldo implements CommandExecutor {

    private final EconomyManager economy;

    public Saldo(SnowCraft snowcraft) {
        this.economy = snowcraft.getEconomyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player p)) return true;

        double saldo = economy.getBalance(p);
        p.sendMessage(
                Component.text("Seu saldo: ", NamedTextColor.LIGHT_PURPLE)
                        .append(Component.text(saldo, NamedTextColor.GREEN, TextDecoration.BOLD))
        );

        return true;
    }
}