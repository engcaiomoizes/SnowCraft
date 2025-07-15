package br.com.caiomoizes.snowCraft;

import br.com.caiomoizes.snowCraft.commands.Login;
import br.com.caiomoizes.snowCraft.commands.Pay;
import br.com.caiomoizes.snowCraft.commands.Register;
import br.com.caiomoizes.snowCraft.commands.Saldo;
import br.com.caiomoizes.snowCraft.economy.EconomyManager;
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
    private CustomConfig tablist;
    private CustomConfig economy;

    private EconomyManager economyManager;

    public static SnowCraft getInstance() {
        return instance;
    }

    public static void setInstance(SnowCraft instance) {
        SnowCraft.instance = instance;
    }

    public CustomConfig getUsers() {
        return this.users;
    }

    public CustomConfig getTablist() {
        return this.tablist;
    }

    public CustomConfig getEconomy() { return this.economy; }

    public EconomyManager getEconomyManager() {
        return this.economyManager;
    }

    @Override
    public void onEnable() {
        setInstance(this);

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SnowCraft] Ativado!", NamedTextColor.GREEN));

        getCommand("register").setExecutor(new Register());
        getCommand("login").setExecutor(new Login());

        getCommand("saldo").setExecutor(new Saldo(this));
        getCommand("pay").setExecutor(new Pay(this));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(this), this);

        this.economyManager = new EconomyManager(economy);

        this.users = new CustomConfig("users.yml");
        this.users.get().options().copyDefaults(true);
        this.users.save();

        saveResource("tablist.yml", false);

        this.tablist = new CustomConfig("tablist.yml");
        //this.tablist.get().options().copyDefaults(true);
        //this.tablist.save();

        this.economy = new CustomConfig("economy.yml");

        runnable();
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SnowCraft] Desativado!", NamedTextColor.RED));

        if (economyManager != null)
            economyManager.saveAll();
    }

    public void runnable() {
        (new BukkitRunnable() {
            int count = 0;

            World world = SnowCraft.this.getServer().getWorld("world");

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

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            economyManager.saveAll();
            getLogger().info("[Economy] Dados salvos automaticamente.");
        }, 5 * 60 * 20L, 5 * 60 * 20L);
    }
}
