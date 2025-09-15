package br.com.caiomoizes.snowCraft.events;

import br.com.caiomoizes.snowCraft.SnowCraft;
import br.com.caiomoizes.snowCraft.apis.LoginAPI;
import br.com.caiomoizes.snowCraft.customdisplay.CustomDisplayManager;
import br.com.caiomoizes.snowCraft.factions.FactionsManager;
import br.com.caiomoizes.snowCraft.factions.Region;
import br.com.caiomoizes.snowCraft.tablist.TabListManager;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class Events implements Listener {

    private final SnowCraft snowcraft;

    private final TabListManager tabListManager;

    private final FactionsManager factions;
    private final CustomDisplayManager display;

    public Events(SnowCraft snowcraft) {
        this.snowcraft = snowcraft;
        this.tabListManager = new TabListManager(snowcraft);
        this.factions = snowcraft.getFactionsManager();
        this.display = snowcraft.getDisplayManager();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.joinMessage(Component.text(p.getName() + " entrou!", NamedTextColor.GREEN));
        p.sendMessage(Component.text("Bem-Vindo ao SnowCraft!", NamedTextColor.BLUE));

        Title.Times times = Title.Times.times(
                Duration.ofMillis(500),
                Duration.ofMillis(2000),
                Duration.ofMillis(500)
        );

        Title title = Title.title(
                Component.text("Bem-Vindo ao SnowCraft!", NamedTextColor.AQUA),
                Component.text("www.caiomoizes.com.br", NamedTextColor.LIGHT_PURPLE),
                times
        );

        p.showTitle(title);
//        p.sendActionBar(Component.text("Bem-vindo ao SnowCraft!", NamedTextColor.DARK_AQUA, TextDecoration.BOLD));
        BossBar bb = BossBar.bossBar(
                Component.text("SnowCraft", NamedTextColor.DARK_AQUA, TextDecoration.BOLD),
                0.5f,
                BossBar.Color.PURPLE,
                BossBar.Overlay.PROGRESS
        );
        p.showBossBar(bb);

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Title")).build())
                .type(DialogType.notice())
        );
        p.showDialog(dialog);

        display.setDisplayName(p);
        p.displayName(display.getDisplayName(p));
        p.playerListName(display.getDisplayName(p));

        tabListManager.setCustomTabList(p);

        if (LoginAPI.requireLogin()) {
            if (!LoginAPI.estaRegistrado(p)) {
                p.sendMessage(Component.text("Registre-se, por favor: /register <senha> <confirmaSenha>", NamedTextColor.GOLD));
            } else {
                p.sendMessage(Component.text("Logue-se, por favor: /login <senha>", NamedTextColor.DARK_AQUA));
            }

            (new BukkitRunnable() {
                @Override
                public void run() {
                    if (!LoginAPI.estaLogado(p))
                        p.kick(Component.text("Você demorou muito para logar!", NamedTextColor.RED));
                }
            }).runTaskLater(snowcraft, 2400L);
        }

        // Caso o jogador não tiver saldo registrado, lhe é creditado um valor inicial
        snowcraft.getEconomyManager().creditInitialValue(p);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        LoginAPI.deslogar(p);
        e.quitMessage(Component.text(p.getName() + " saiu!", NamedTextColor.RED));
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncChatEvent e) {
        Player p = e.getPlayer();
        Component message = e.message().color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false);

        Component prefix = display.getDisplayName(p);
        Component finalMessage = prefix.append(Component.text(": ", NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(message);

        e.renderer((source, sourceDisplayName, msg, viewers) -> finalMessage);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (!LoginAPI.estaLogado(p) && LoginAPI.requireLogin())
            p.teleport(e.getFrom());

        if (factions.hasFaction(p)) {
            String faction = factions.getFactionName(p);
            Location location = p.getLocation();

            Region region = factions.getRegion(faction);

            // Verifica se a faction possui região definida no mundo
            if (region != null && region.getWorld() == location.getWorld()) {
                // Verifica se está dentro do range
                double x = region.getCenter().getX();
                double y = region.getCenter().getY();
                double z = region.getCenter().getZ();
                int range = region.getRange();

                if (Math.abs(location.getX() - x) < range + 1 && Math.abs(location.getZ() - z) < range + 1) {
                    if (!factions.isInRegion(p)) {
                        factions.addPlayerInRegion(p, faction);
                        p.sendMessage(Component.text("Bem-vindo à sede de " + faction));
                    }
                } else {
                    if (factions.isInRegion(p)) {
                        factions.removePlayerInRegion(p, faction);
                        p.sendMessage(Component.text("Saindo da região..."));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!LoginAPI.estaLogado(p) && LoginAPI.requireLogin())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        Location blockLocation = block.getLocation();

        List<String> factionList = factions.getFactionsList();
        for (String faction : factionList) {
            Region region = factions.getRegion(faction);

            // Verifica se o jogador pertece a esta faction
            if (factions.isInFaction(p, faction)) continue;

            // Verifica se a faction possui região definida
            if (region == null) continue;

            // Verifica se o mundo é o mesmo
            if (region.getWorld() != blockLocation.getWorld()) continue;

            // Verifica se está dentro do range
            double x = region.getCenter().getX();
            double y = region.getCenter().getY();
            double z = region.getCenter().getZ();
            int range = region.getRange();

            if (Math.abs(blockLocation.getX() - x) < range + 1 && Math.abs(blockLocation.getZ() - z) < range + 1) {
                // Está dentro da região da faction
                e.setCancelled(true);
                p.sendMessage(Component.text("Você não pode quebrar blocos na região de uma faction."));
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Location blockLocation = e.getBlockPlaced().getLocation();

        List<String> factionList = factions.getFactionsList();
        for (String faction : factionList) {
            Region region = factions.getRegion(faction);

            // Verifica se o jogador pertece a esta faction
            if (factions.isInFaction(p, faction)) continue;

            // Verifica se a faction possui região definida
            if (region == null) continue;

            // Verifica se o mundo é o mesmo
            if (region.getWorld() != blockLocation.getWorld()) continue;

            // Verifica se está dentro do range
            double x = region.getCenter().getX();
            double y = region.getCenter().getY();
            double z = region.getCenter().getZ();
            int range = region.getRange();

            if (Math.abs(blockLocation.getX() - x) < range + 1 && Math.abs(blockLocation.getZ() - z) < range + 1) {
                // Está dentro da região da faction
                e.setCancelled(true);
                p.sendMessage(Component.text("Você não pode colocar blocos na região de uma faction."));
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
    }

    @EventHandler
    public void onPlayerCustomClickEvent(PlayerCustomClickEvent e) {
        Key key = e.getIdentifier();
        PlayerGameConnection connection = (PlayerGameConnection) e.getCommonConnection();
        Player p = connection.getPlayer();

        DialogResponseView responseView = e.getDialogResponseView();

        switch (key.asString()) {
            case "snowcraft:faction/create":
                String name = responseView.getText("name");
                if (name != null) {
                    if (factions.existsFaction(name))
                        p.sendMessage(Component.text("Já existe uma faction com este nome.", NamedTextColor.GOLD, TextDecoration.BOLD));
                    else {
                        factions.createFaction(name, p);
                        p.sendMessage(
                                Component.text("Você criou a faction ", NamedTextColor.WHITE, TextDecoration.BOLD)
                                        .append(Component.text(name, NamedTextColor.YELLOW, TextDecoration.BOLD))
                                        .append(Component.text(".", NamedTextColor.WHITE, TextDecoration.BOLD))
                        );
                    }
                } else
                    p.sendMessage(Component.text("Você deve informar o nome da faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                break;
            case "snowcraft:faction/delete":
                String faction = factions.getFactionName(p);
                if (!factions.isOwner(p, faction))
                    p.sendMessage(Component.text("Apenas o líder pode deletar a faction.", NamedTextColor.GOLD, TextDecoration.BOLD));
                else {
                    factions.deleteFaction(faction);
                    p.sendMessage(Component.text("Faction deletada com sucesso!", NamedTextColor.GREEN, TextDecoration.BOLD));
                    display.setDisplayName(p);
                    p.displayName(display.getDisplayName(p));
                    p.playerListName(display.getDisplayName(p));
                }
                break;
            case "snowcraft:faction/accept":
                String invitedFaction = factions.getFactionByInvited(p);
                factions.join(p);
                p.sendMessage(
                        Component.text("Bem-vindo a ", NamedTextColor.WHITE, TextDecoration.BOLD)
                                .append(Component.text(invitedFaction, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                                .append(Component.text("! É muito bom ter você aqui.", NamedTextColor.WHITE, TextDecoration.BOLD))
                );
                break;
            case "snowcraft:faction/decline":
                String declinedFaction = factions.getFactionByInvited(p);
                factions.decline(declinedFaction, p);
                p.sendMessage(
                        Component.text("Você recusou o convite para entrar em ", NamedTextColor.WHITE, TextDecoration.BOLD)
                                .append(Component.text(declinedFaction, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD))
                                .append(Component.text(".", NamedTextColor.WHITE, TextDecoration.BOLD))
                );
                break;
            default:
                break;
        }
    }

}
