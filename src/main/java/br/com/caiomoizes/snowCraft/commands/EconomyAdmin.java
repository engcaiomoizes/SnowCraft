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

public class EconomyAdmin implements CommandExecutor {

    private final EconomyManager economy;

    public EconomyAdmin(SnowCraft snowcraft) {
        this.economy = snowcraft.getEconomyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p) || args.length < 3) return false;

        String subCmd = args[0];
        Player to = Bukkit.getPlayer(args[1]);
        double amount;

        if (to == null) {
            p.sendMessage(Component.text("Jogador inválido!", NamedTextColor.RED));
            return true;
        }

        switch (subCmd) {
            case "set":
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    p.sendMessage(Component.text("Valor inválido!", NamedTextColor.RED));
                    return true;
                }

                economy.setBalance(to, amount);

                p.sendMessage(
                        Component.text("Você definiu o saldo de ", NamedTextColor.AQUA)
                                .append(Component.text(to.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                                .append(Component.text(" para ", NamedTextColor.AQUA))
                                .append(Component.text(amount, NamedTextColor.GREEN, TextDecoration.BOLD))
                );

                to.sendMessage(
                        Component.text("Um administrador definiu seu saldo para ", NamedTextColor.AQUA)
                                .append(Component.text(amount, NamedTextColor.GREEN, TextDecoration.BOLD))
                );
                break;
            case "add":
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    p.sendMessage(Component.text("Valor inválido!", NamedTextColor.RED));
                    return true;
                }

                if (economy.deposit(to, amount)) {
                    p.sendMessage(
                            Component.text("Você depositou ", NamedTextColor.AQUA)
                                    .append(Component.text(amount, NamedTextColor.GREEN, TextDecoration.BOLD))
                                    .append(Component.text(" na conta de ", NamedTextColor.AQUA))
                                    .append(Component.text(to.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                    );

                    to.sendMessage(
                            Component.text("Um administrador depositou ", NamedTextColor.AQUA)
                                    .append(Component.text(amount, NamedTextColor.GREEN, TextDecoration.BOLD))
                                    .append(Component.text(" na sua conta.", NamedTextColor.AQUA))
                    );
                }
                break;
            case "remove":
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    p.sendMessage(Component.text("Valor inválido!", NamedTextColor.RED));
                    return true;
                }

                if (economy.withdraw(to, amount)) {
                    p.sendMessage(
                            Component.text("Você removeu ", NamedTextColor.AQUA)
                                    .append(Component.text(amount, NamedTextColor.RED, TextDecoration.BOLD))
                                    .append(Component.text(" da conta de ", NamedTextColor.AQUA))
                                    .append(Component.text(to.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                    );

                    to.sendMessage(
                            Component.text("Um administrador removeu ", NamedTextColor.AQUA)
                                    .append(Component.text(amount, NamedTextColor.RED, TextDecoration.BOLD))
                                    .append(Component.text(" de sua conta.", NamedTextColor.AQUA))
                    );
                } else {
                    p.sendMessage(
                            Component.text(to.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)
                                    .append(Component.text(" não possui este saldo em conta.", NamedTextColor.RED, TextDecoration.BOLD))
                    );
                }
                break;
            default:
                p.sendMessage(Component.text("Comando inválido!", NamedTextColor.DARK_RED));
                break;
        }

        return true;
    }
}