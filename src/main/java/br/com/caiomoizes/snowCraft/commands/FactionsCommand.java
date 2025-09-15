package br.com.caiomoizes.snowCraft.commands;

import br.com.caiomoizes.snowCraft.SnowCraft;
import br.com.caiomoizes.snowCraft.customdisplay.CustomDisplayManager;
import br.com.caiomoizes.snowCraft.factions.FactionsManager;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FactionsCommand implements CommandExecutor {

    private final FactionsManager factions;
    private final CustomDisplayManager displayManager;

    public FactionsCommand(SnowCraft snowcraft) {
        this.factions = snowcraft.getFactionsManager();
        this.displayManager = snowcraft.getDisplayManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p) || args.length < 1) return false;

        String subCmd = args[0];

        switch (subCmd) {
            case "list":
                ListCommand(p);
                break;
            case "create":
                DialogCreateCommand(p);
                break;
            case "delete":
                if (factions.hasFaction(p))
                    DialogDeleteCommand(p);
                else
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                break;
            case "invite":
                if (factions.hasFaction(p))
                    InviteCommand(p, args);
                else
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                break;
            case "kick":
                if (factions.hasFaction(p))
                    KickCommand(p, args);
                else
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                break;
            case "region":
                if (factions.hasFaction(p))
                    RegionCommand(p, args);
                else
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                break;
            case "acronym":
                if (factions.hasFaction(p))
                    AcronymCommand(p, args);
                else
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                break;
            case "leave":
                if (factions.hasFaction(p))
                    LeaveCommand(p);
                else
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                break;
            case "owner":
                if (factions.hasFaction(p))
                    OwnerCommand(p, args);
                else
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                break;
            case "teste":
                //
                break;
            default:
                break;
        }

        return true;
    }

    /* NOVOS COMANDOS COM USO DE DIALOGS */
    private void DialogCreateCommand(Player p) {
        if (factions.hasFaction(p))
            p.sendMessage(Component.text("Você já está em uma faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
        else {
            Dialog dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(Component.text("Criar Faction"))
                            .inputs(List.of(
                                    DialogInput.text("name", Component.text("Nome")).build()
                            ))
                            .build()
                    )
                    .type(DialogType.confirmation(
                            ActionButton.builder(Component.text("Criar"))
                                    .action(DialogAction.customClick(Key.key("snowcraft:faction/create"), null))
                                    .build(),
                            ActionButton.builder(Component.text("Cancelar"))
                                    .build()
                    ))
            );
            p.showDialog(dialog);
        }
    }

    private void DialogDeleteCommand(Player p) {
        if (!factions.hasFaction(p))
            p.sendMessage(Component.text("Você não está em uma faction.", NamedTextColor.RED, TextDecoration.BOLD));
        else {
            String faction = factions.getFactionName(p);
            if (!factions.isOwner(p, faction))
                p.sendMessage(Component.text("Apenas o líder pode deletar a faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
            else {
                Dialog dialog = Dialog.create(builder -> builder.empty()
                        .base(DialogBase.builder(Component.text("Deletar Faction"))
                                .body(List.of(
                                        DialogBody.plainMessage(
                                                Component.text("Deseja realmente deletar ", NamedTextColor.GOLD, TextDecoration.BOLD)
                                                        .append(Component.text(faction, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                                                        .append(Component.text("?", NamedTextColor.GOLD, TextDecoration.BOLD))
                                        )
                                ))
                                .build()
                        )
                        .type(DialogType.confirmation(
                                ActionButton.builder(Component.text("Sim"))
                                        .action(DialogAction.customClick(Key.key("snowcraft:faction/delete"), null))
                                        .build(),
                                ActionButton.builder(Component.text("Não"))
                                        .build()
                        ))
                );
                p.showDialog(dialog);
            }
        }
    }
    /* ============================================================================================================== */

    private void ListCommand(Player p) {
        p.sendMessage(Component.text("Lista de Factions:", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        List<String> list = factions.getFactionsList();
        for (String faction : list)
            p.sendMessage(Component.text(faction, NamedTextColor.WHITE, TextDecoration.BOLD));
    }

    private void CreateCommand(Player p, String[] args) {
        if (factions.hasFaction(p))
            p.sendMessage(Component.text("Você já está em uma faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
        else {
            if (args.length < 2)
                p.sendMessage(Component.text("Informe o nome da faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
            else {
                String name = args[1];
                if (factions.existsFaction(name))
                    p.sendMessage(Component.text("Já existe uma faction com este nome.", NamedTextColor.GOLD, TextDecoration.BOLD));
                else {
                    factions.createFaction(name, p);
                    p.sendMessage(Component.text("Faction criada com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                    displayManager.setDisplayName(p);
                    p.displayName(displayManager.getDisplayName(p));
                    p.playerListName(displayManager.getDisplayName(p));
                }
            }
        }
    }

    private void DeleteCommand(Player p) {
        if (!factions.hasFaction(p))
            p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
        else {
            String faction = factions.getFactionName(p);
            if (!factions.isOwner(p, faction))
                p.sendMessage(Component.text("Apenas o líder pode deletar a faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
            else {
                factions.deleteFaction(factions.getFactionName(p));
                p.sendMessage(Component.text("Faction deletada com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                displayManager.setDisplayName(p);
                p.displayName(displayManager.getDisplayName(p));
                p.playerListName(displayManager.getDisplayName(p));
            }
        }
    }

    private void InviteCommand(Player p, String[] args) {
        if (args.length < 2)
            p.sendMessage(Component.text("Informe o jogador que deseja convidar.", NamedTextColor.GOLD, TextDecoration.BOLD));
        else {
            if (p.getName().equals(args[1]))
                p.sendMessage(Component.text("Você não pode convidar a si mesmo.", NamedTextColor.RED, TextDecoration.BOLD));
            else {
                if (!factions.hasFaction(p))
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                else {
                    String faction = factions.getFactionName(p);
                    if (!factions.isOwner(p, faction))
                        p.sendMessage(Component.text("Apenas o líder pode convidar um jogador.", NamedTextColor.GOLD, TextDecoration.BOLD));
                    else {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null && target.isOnline()) {
                            if (factions.hasFaction(target))
                                p.sendMessage(Component.text("Este jogador já está em uma faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                            else {
                                factions.inviteMember(target, faction);
                                p.sendMessage(
                                        Component.text(target.getName(), NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                                                .append(Component.text(" foi convidado! Aguardando resposta...", NamedTextColor.WHITE, TextDecoration.BOLD))
                                );

                                Dialog dialog = Dialog.create(builder -> builder.empty()
                                        .base(DialogBase.builder(Component.text("Convite"))
                                                .body(List.of(
                                                        DialogBody.plainMessage(
                                                                Component.text(p.getName(), NamedTextColor.DARK_AQUA, TextDecoration.BOLD)
                                                                        .append(Component.text(" te convidou para ", NamedTextColor.WHITE, TextDecoration.BOLD))
                                                                        .append(Component.text(faction, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                                                                        .append(Component.text(".", NamedTextColor.GOLD, TextDecoration.BOLD))
                                                        )
                                                ))
                                                .build()
                                        )
                                        .type(DialogType.confirmation(
                                                ActionButton.builder(Component.text("Aceitar"))
                                                        .action(DialogAction.customClick(Key.key("snowcraft:faction/accept"), null))
                                                        .build(),
                                                ActionButton.builder(Component.text("Recusar"))
                                                        .action(DialogAction.customClick(Key.key("snowcraft:faction/decline"), null))
                                                        .build()
                                        ))
                                );
                                target.showDialog(dialog);
                            }
                        } else {
                            p.sendMessage(Component.text("Este jogador não existe ou está offline.", NamedTextColor.RED));
                        }
                    }
                }
            }
        }
    }

    private void KickCommand(Player p, String[] args) {
        if (args.length < 2)
            p.sendMessage(Component.text("Informe o jogador que deseja expulsar.", NamedTextColor.GOLD, TextDecoration.BOLD));
        else {
            if (p.getName().equals(args[1]))
                p.sendMessage(Component.text("Você não expulsar a si mesmo.", NamedTextColor.RED, TextDecoration.BOLD));
            else {
                if (!factions.hasFaction(p))
                    p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                else {
                    String faction = factions.getFactionName(p);
                    if (!factions.isOwner(p, faction))
                        p.sendMessage(Component.text("Apenas o líder pode expulsar um jogador.", NamedTextColor.GOLD, TextDecoration.BOLD));
                    else {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            if (!factions.hasFaction(target) || !factions.hasMember(faction, target))
                                p.sendMessage(Component.text("Este jogador não está em sua faction.", NamedTextColor.RED, TextDecoration.BOLD));
                            else {
                                factions.removeMember(faction, target);
                                p.sendMessage(
                                        Component.text(target.getName(), NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                                                .append(Component.text(" foi removido da faction.", NamedTextColor.GOLD, TextDecoration.BOLD))
                                );
                                displayManager.setDisplayName(target);p.displayName(displayManager.getDisplayName(target));
                                p.playerListName(displayManager.getDisplayName(target));
                            }
                        } else {
                            p.sendMessage(Component.text("Jogador inválido!", NamedTextColor.RED, TextDecoration.BOLD));
                        }
                    }
                }
            }
        }
    }

    private void RegionCommand(Player p, String[] args) {
        if (args.length < 2)
            p.sendMessage(Component.text(""));
        else {
            String cmd = args[1];
            String faction = factions.getFactionName(p);
            switch (cmd) {
                case "set":
                    if (args.length < 3)
                        p.sendMessage(Component.text("/faction region set <range>"));
                    else {
                        String range = args[2];
                        if (!factions.hasFaction(p))
                            p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                        else {
                            if (!factions.isOwner(p, faction))
                                p.sendMessage(Component.text("Apenas o líder poder definir a região.", NamedTextColor.GOLD, TextDecoration.BOLD));
                            else {
                                if (factions.hasRegion(faction))
                                    p.sendMessage(Component.text("Sua faction já possui uma região.", NamedTextColor.GOLD, TextDecoration.BOLD));
                                else {
                                    try {
                                        int num = Integer.parseInt(range);
                                        factions.setRegion(p, faction, num);
                                        p.sendMessage(Component.text("Região definida!", NamedTextColor.YELLOW, TextDecoration.BOLD));
                                    } catch (NumberFormatException e) {
                                        p.sendMessage(Component.text("Range inválido! Informe um valor válido para o range.", NamedTextColor.RED, TextDecoration.BOLD));
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "confirm":
                    if (!factions.hasFaction(p))
                        p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                    else {
                        if (!factions.isOwner(p, faction))
                            p.sendMessage(Component.text("Apenas o líder poder definir a região.", NamedTextColor.GOLD, TextDecoration.BOLD));
                        else {
                            if (factions.hasRegion(faction))
                                p.sendMessage(Component.text("Sua faction já possui uma região.", NamedTextColor.GOLD, TextDecoration.BOLD));
                            else {
                                if (!factions.regionIsSetted(faction))
                                    p.sendMessage(Component.text("Você ainda não definiu uma região.", NamedTextColor.RED, TextDecoration.BOLD));
                                else {
                                    factions.confirmRegion(faction);
                                    p.sendMessage(Component.text("Região definida com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                                }
                            }
                        }
                    }
                    break;
                case "delete":
                    if (!factions.hasFaction(p))
                        p.sendMessage(Component.text("Você não está em nenhuma faction.", NamedTextColor.RED, TextDecoration.BOLD));
                    else {
                        if (!factions.isOwner(p, faction))
                            p.sendMessage(Component.text("Apenas o líder pode deletar a região.", NamedTextColor.GOLD, TextDecoration.BOLD));
                        else {
                            if (!factions.hasRegion(faction))
                                p.sendMessage(Component.text("Sua faction não possui uma região.", NamedTextColor.RED, TextDecoration.BOLD));
                            else {
                                factions.deleteRegion(faction);
                                p.sendMessage(Component.text("Região deletada com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                            }
                        }
                    }
                    break;
                case "setspawn":
                    if (!factions.isOwner(p, faction))
                        p.sendMessage(Component.text("Apenas o líder pode definir o spawn da faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                    else {
                        if (!factions.hasRegion(faction))
                            p.sendMessage(Component.text("Sua faction ainda não tem uma região.", NamedTextColor.RED, TextDecoration.BOLD));
                        else {
                            if (!factions.isInRegion(p))
                                p.sendMessage(Component.text("Você não está dentro da região de sua faction.", NamedTextColor.RED, TextDecoration.BOLD));
                            else {
                                factions.setSpawn(p, faction);
                                p.sendMessage(Component.text("Spawn definido com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                            }
                        }
                    }
                    break;
                case "spawn":
                    if (!factions.hasRegion(faction))
                        p.sendMessage(Component.text("Sua faction ainda não tem uma região.", NamedTextColor.RED, TextDecoration.BOLD));
                    else if (!factions.hasSpawn(faction))
                        p.sendMessage(Component.text("A região da sua faction não possui um spawn definido.", NamedTextColor.RED, TextDecoration.BOLD));
                    else {
                        p.teleport(factions.getSpawn(faction));
                        p.sendMessage(Component.text("Teleportado para o spawn!", NamedTextColor.GREEN, TextDecoration.BOLD));
                    }
                    break;
                default:
                    p.sendMessage(Component.text("Comando inválido!", NamedTextColor.RED, TextDecoration.BOLD));
                    break;
            }
        }
    }

    private void AcronymCommand(Player p, String[] args) {
        if (args.length < 2)
            p.sendMessage(Component.text("Informe a sigla desejada (3 letras).", NamedTextColor.GOLD, TextDecoration.BOLD));
        else {
            String faction = factions.getFactionName(p);
            String acronym = args[1];
            if (acronym.length() != 3)
                p.sendMessage(Component.text("A sigla deve conter exatamente 3 letras.", NamedTextColor.RED, TextDecoration.BOLD));
            else {
                if (!factions.isOwner(p, faction))
                    p.sendMessage(Component.text("Apenas o líder pode definir a sigla da faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                else {
                    factions.setAcronym(faction, acronym);
                    p.sendMessage(Component.text("Sigla definida com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                    // ALTERAR O DISPLAY NAME DE TODOS OS JOGADORES DA FACTION
                    displayManager.setDisplayName(p);
                    p.displayName(displayManager.getDisplayName(p));
                    p.playerListName(displayManager.getDisplayName(p));
                }
            }
        }
    }

    private void LeaveCommand(Player p) {
        String faction = factions.getFactionName(p);
        if (factions.isOwner(p, faction)) {
            p.sendMessage(
                    Component.text("Você não pode sair da faction que você é dono.", NamedTextColor.RED, TextDecoration.BOLD)
            );
            p.sendMessage(
                    Component.text("Transfira a faction para um membro usando ", NamedTextColor.WHITE, TextDecoration.BOLD)
                            .append(Component.text("/faction owner <player>", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
            );
        } else {
            factions.leave(p, faction);
            p.sendMessage(
                    Component.text("Você saiu de ", NamedTextColor.WHITE, TextDecoration.BOLD)
                            .append(Component.text(faction, NamedTextColor.YELLOW, TextDecoration.BOLD))
                            .append(Component.text(".", NamedTextColor.WHITE, TextDecoration.BOLD))
            );
            displayManager.setDisplayName(p);
            p.displayName(displayManager.getDisplayName(p));
            p.playerListName(displayManager.getDisplayName(p));
        }
    }

    private void OwnerCommand(Player p, String[] args) {
        String faction = factions.getFactionName(p);
        if (!factions.isOwner(p, faction))
            p.sendMessage(Component.text("Apenas o líder pode definir um novo dono.", NamedTextColor.RED, TextDecoration.BOLD));
        else {
            if (args.length < 2)
                p.sendMessage(Component.text("Informe o membro que deseja tornar líder.", NamedTextColor.GOLD, TextDecoration.BOLD));
            else {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null)
                    p.sendMessage(Component.text("Jogador inválido!", NamedTextColor.RED, TextDecoration.BOLD));
                else {
                    if (!factions.isInFaction(target, faction))
                        p.sendMessage(Component.text("Este jogador não está em sua faction.", NamedTextColor.RED, TextDecoration.BOLD));
                    else {
                        factions.setOwner(p, target, faction);
                        p.sendMessage(
                                Component.text("Você definiu ", NamedTextColor.WHITE, TextDecoration.BOLD)
                                        .append(Component.text(target.getName(), NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" como novo líder de ", NamedTextColor.WHITE, TextDecoration.BOLD))
                                        .append(Component.text(faction, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                                        .append(Component.text(".", NamedTextColor.WHITE, TextDecoration.BOLD))
                        );
                        target.sendMessage(
                                Component.text("Você é o novo líder de ", NamedTextColor.WHITE, TextDecoration.BOLD)
                                        .append(Component.text(faction, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                                        .append(Component.text(".", NamedTextColor.WHITE, TextDecoration.BOLD))
                        );
                    }
                }
            }
        }
    }
}