package br.com.caiomoizes.snowCraft.events;

import br.com.caiomoizes.snowCraft.tablist.TabListHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(Component.text("Bem-Vindo ao SnowCraft!", NamedTextColor.BLUE));
        TabListHandler.setTabListHeaderFooter(p);
        TabListHandler.setPlayerListName(p);
    }

}
