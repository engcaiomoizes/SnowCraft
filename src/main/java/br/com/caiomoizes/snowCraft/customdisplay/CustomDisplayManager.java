package br.com.caiomoizes.snowCraft.customdisplay;

import br.com.caiomoizes.snowCraft.SnowCraft;
import br.com.caiomoizes.snowCraft.factions.FactionsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CustomDisplayManager {

    private final FactionsManager factions;

    public CustomDisplayManager(SnowCraft plugin) {
        this.factions = plugin.getFactionsManager();
    }

    private final Map<String, TextComponent> displayName = new HashMap<>();

    public void setDisplayName(Player p) {
        TextComponent display;
        if (factions.hasFaction(p)) {
            String faction = factions.getFactionName(p);
            String acronym = factions.getAcronym(faction);

            TextComponent prefix = Component.text("[" + acronym + "]", NamedTextColor.DARK_AQUA, TextDecoration.BOLD);

            TextComponent name = Component.text(p.getName(), NamedTextColor.YELLOW, TextDecoration.BOLD);

            display = prefix.append(name);
        } else {
            display = Component.text(p.getName(), NamedTextColor.YELLOW, TextDecoration.BOLD);
        }
        displayName.put(p.getName(), display);
    }

    public TextComponent getDisplayName(Player p) {
        return displayName.get(p.getName());
    }

}
