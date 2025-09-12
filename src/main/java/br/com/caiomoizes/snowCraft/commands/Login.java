package br.com.caiomoizes.snowCraft.commands;

import br.com.caiomoizes.snowCraft.apis.LoginAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Login implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player p) {
            if (strings.length == 0) return false;

            if (!LoginAPI.estaLogado(p)) {
                String senha = strings[0];

                if (senha.equals(LoginAPI.getSenha(p))) {
                    LoginAPI.logar(p);
                    p.sendMessage(Component.text("Logado com sucesso!", NamedTextColor.GREEN));
                } else {
                    p.kick(Component.text("Senha incorreta!", NamedTextColor.RED));
                }
            } else {
                p.sendMessage(Component.text("Você já está logado!", NamedTextColor.LIGHT_PURPLE));
            }
        }

        return true;
    }
}