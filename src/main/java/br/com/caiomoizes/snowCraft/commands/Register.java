package br.com.caiomoizes.snowCraft.commands;

import br.com.caiomoizes.snowCraft.apis.LoginAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Register implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player p) {
            if (!p.hasPermission("register.use")) {
                p.sendMessage(Component.text("Você não tem permissão para usar este comando.", NamedTextColor.RED));
                return true;
            }

            if (strings.length == 0) return false;

            if (!LoginAPI.estaRegistrado(p)) {
                String senha = strings[0];

                if (strings.length < 2)
                    p.sendMessage(Component.text("Digite a confirmação da senha.", NamedTextColor.GOLD));
                else {
                    String confirmaSenha = strings[1];

                    if (senha.equals(confirmaSenha)) {
                        LoginAPI.registrar(p, senha);
                        p.sendMessage(Component.text("Você se registrou com sucesso!", NamedTextColor.GREEN));
                        LoginAPI.logar(p);
                    } else {
                        p.sendMessage(Component.text("As senhas não batem!", NamedTextColor.RED));
                    }
                }
            } else {
                p.sendMessage(Component.text("Você já está registrado!", NamedTextColor.LIGHT_PURPLE));
            }
        }

        return true;
    }
}