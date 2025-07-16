package br.com.caiomoizes.snowCraft.commands;

import br.com.caiomoizes.snowCraft.SnowCraft;
import br.com.caiomoizes.snowCraft.factions.FactionsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FactionsCommand implements CommandExecutor {

    private final FactionsManager factions;

    public FactionsCommand(SnowCraft snowcraft) {
        this.factions = snowcraft.getFactionsManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p) || args.length < 1) return false;

        String subCmd = args[0];

        switch (subCmd) {
            case "list":
                p.sendMessage(
                        Component.text("Lista de Factions:", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                                .appendNewline()
                                .append(Component.text("", NamedTextColor.WHITE))
                );
                break;
            case "create":
                if (args.length < 2)
                    p.sendMessage(Component.text("Informe o nome da faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                else {
                    String name = args[1];
                    //
                }
                break;
            case "delete":
                if (factions.isOwner(p)) {
                    factions.deleteFaction(factions.getFactionName(p));

                    p.sendMessage(Component.text("Faction deletada com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                } else {
                    if (factions.hasFaction(p))
                        p.sendMessage(Component.text("Apenas o líder pode deletar a faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                    else
                        p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                }
                break;
            case "invite":
                if (args.length < 2)
                    p.sendMessage(Component.text("Informe o jogador que deseja convidar.", NamedTextColor.GOLD, TextDecoration.BOLD));
                else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (factions.isOwner(p)) {
                        if (target == null)
                            p.sendMessage(Component.text("Jogador inválido!", NamedTextColor.RED, TextDecoration.BOLD));
                        else {
                            factions.inviteMember(target, factions.getFactionName(p));
                            p.sendMessage(
                                    Component.text(target.getName(), NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                                            .append(Component.text(" foi convidado! Aguardando resposta...", NamedTextColor.WHITE, TextDecoration.BOLD))
                            );
                        }
                    } else {
                        if (factions.hasFaction(p))
                            p.sendMessage(Component.text("Apenas o líder pode convidar um jogador.", NamedTextColor.GOLD, TextDecoration.BOLD));
                        else
                            p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                    }
                }
                break;
            case "kick":
                if (args.length < 2)
                    p.sendMessage(Component.text("Informe o jogador que deseja expulsar.", NamedTextColor.GOLD, TextDecoration.BOLD));
                else {
                    String target = args[1];
                    if (factions.isOwner(p)) {
                        if (factions.hasMember(factions.getFactionName(p), target)) {
                            factions.removeMember(target);

                            p.sendMessage(
                                    Component.text(target, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                                            .append(Component.text(" foi removido da faction.", NamedTextColor.GOLD, TextDecoration.BOLD))
                            );
                        } else {
                            p.sendMessage(Component.text("Este jogador não faz parte da faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                        }
                    } else {
                        if (factions.hasFaction(p))
                            p.sendMessage(Component.text("Apenas o líder pode convidar um jogador.", NamedTextColor.GOLD, TextDecoration.BOLD));
                        else
                            p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                    }
                }
                break;
        }

        return true;
    }
}
