package br.com.caiomoizes.snowCraft.events;

import br.com.caiomoizes.snowCraft.SnowCraft;
import br.com.caiomoizes.snowCraft.apis.LoginAPI;
import br.com.caiomoizes.snowCraft.tablist.TabListManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class Events implements Listener {

    private final SnowCraft snowcraft;

    private final TabListManager tabListManager;

    public Events(SnowCraft snowcraft) {
        this.snowcraft = snowcraft;
        this.tabListManager = new TabListManager(snowcraft);
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

        tabListManager.setCustomTabList(p);

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

        // Caso o jogador não tiver saldo registrado, lhe é creditado um valor inicial de 200
        if (!snowcraft.getEconomyManager().exists(p))
            snowcraft.getEconomyManager().setBalance(p, 200);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.quitMessage(Component.text(p.getName() + " saiu!", NamedTextColor.RED));
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (!LoginAPI.estaLogado(p))
            p.teleport(e.getFrom());
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!LoginAPI.estaLogado(p))
                e.setCancelled(true);
        }
    }

}
