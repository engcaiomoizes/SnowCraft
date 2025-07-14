package br.com.caiomoizes.snowCraft.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class TabListHandler {

    public static void setTabListHeaderFooter(Player p) {
        Component header = Component.text("Bem-Vindo ao SnowCraft!", NamedTextColor.AQUA, TextDecoration.BOLD);
        Component footer = Component.text("Jogadores online: " + p.getServer().getOnlinePlayers().size(), NamedTextColor.DARK_PURPLE);

        p.sendPlayerListHeaderAndFooter(header, footer);
    }

    public static void setPlayerListName(Player p) {
        p.playerListName(Component.text(p.getName(), NamedTextColor.WHITE, TextDecoration.BOLD));
    }

}
