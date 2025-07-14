package br.com.caiomoizes.snowCraft;

import br.com.caiomoizes.snowCraft.events.Events;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class SnowCraft extends JavaPlugin {

    private static SnowCraft instance;

    private CustomConfig users;

    public static SnowCraft getInstance() {
        return instance;
    }

    public static void setInstance(SnowCraft instance) {
        SnowCraft.instance = instance;
    }

    public CustomConfig getUsers() {
        return this.users;
    }

    @Override
    public void onEnable() {
        setInstance(this);

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SnowCraft] Ativado!", NamedTextColor.GREEN));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(), this);

        this.users = new CustomConfig("users.yml");
        this.users.get().options().copyDefaults(true);
        this.users.save();

        runnable();
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SnowCraft] Desativado!", NamedTextColor.RED));
    }

    public void runnable() {
        (new BukkitRunnable() {
            int count = 0;

            World world = SnowCraft.this.getServer().getWorld("New World");

            List<Entity> entList;

            @Override
            public void run() {
                this.count++;
                switch (this.count) {
                    case 14:
                        Bukkit.broadcast(
                                Component.text("Limpando itens do chão em ", NamedTextColor.YELLOW)
                                        .append(Component.text("3 minutos...", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        );
                        break;
                    case 16:
                        Bukkit.broadcast(
                                Component.text("Limpando itens do chão em ", NamedTextColor.YELLOW)
                                        .append(Component.text("2 minutos...", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        );
                        break;
                    case 18:
                        Bukkit.broadcast(
                                Component.text("Limpando itens do chão em ", NamedTextColor.YELLOW)
                                        .append(Component.text("1 minuto...", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        );
                        break;
                    case 19:
                        Bukkit.broadcast(
                                Component.text("Limpando itens do chão em ", NamedTextColor.YELLOW)
                                        .append(Component.text("30 segundos...", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        );
                        break;
                    case 20:
                        Bukkit.broadcast(Component.text("Limpando itens do chão...", NamedTextColor.GOLD, TextDecoration.BOLD));
                        this.entList = this.world.getEntities();
                        for (Entity current : this.entList) {
                            if (current instanceof Item)
                                current.remove();
                        }
                        this.count = 0;
                        break;
                }
            }
        }).runTaskTimer(this, 0L, 600L);
    }
}
