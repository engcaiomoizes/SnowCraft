package br.com.caiomoizes.snowCraft.tablist;

import br.com.caiomoizes.snowCraft.SnowCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class TabListManager {

    private final SnowCraft snowcraft;

    public TabListManager(SnowCraft snowcraft) {
        this.snowcraft = snowcraft;
    }

    public void setCustomTabList(Player p) {
        FileConfiguration config = snowcraft.getTablist().get();

        List<String> headerLines = config.getStringList("tablist.header");
        List<String> footerLines = config.getStringList("tablist.footer");

        headerLines.addFirst("");
        headerLines.addLast("");
        footerLines.addFirst("");
        footerLines.addLast("");

        String headerRaw = String.join("\n", headerLines)
                .replace("%player%", p.getName())
                .replace("%online%", String.valueOf(snowcraft.getServer().getOnlinePlayers().size()));

        String footerRaw = String.join("\n", footerLines)
                .replace("%player%", p.getName())
                .replace("%online%", String.valueOf(snowcraft.getServer().getOnlinePlayers().size()));

        Component header = LegacyComponentSerializer.legacyAmpersand().deserialize(headerRaw);
        Component footer = LegacyComponentSerializer.legacyAmpersand().deserialize(footerRaw);

        p.sendPlayerListHeaderAndFooter(header, footer);
    }

}
