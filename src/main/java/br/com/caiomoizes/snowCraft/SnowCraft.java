package br.com.caiomoizes.snowCraft;

import br.com.caiomoizes.snowCraft.commands.*;
import br.com.caiomoizes.snowCraft.economy.EconomyManager;
import br.com.caiomoizes.snowCraft.events.Events;
import io.papermc.paper.command.brigadier.BasicCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
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

        saveDefaultConfig();

        // Carrega saldo inicial da config
        double saldoInicial = getConfig().getDouble("saldo-inicial", 200.0);

        this.economy = new CustomConfig("economy.yml");
        this.economyManager = new EconomyManager(economy, saldoInicial);

        // Comandos com CommandExecutor
        registerCommand(
                "register",
                "Registrar sua conta",
                List.of("register", "registrar"),
                "register.use",
                "/register <senha> <confirmaSenha>",
                new Register()
        );
        registerCommand(
                "login",
                "Logar na sua conta",
                List.of("login", "logar"),
                "login.use",
                "/login <senha>",
                new Login()
        );
        registerCommand(
                "saldo",
                "Exibir saldo da conta",
                List.of("saldo", "bal"),
                "saldo.use",
                "/saldo",
                new Saldo(this)
        );
        registerCommand(
                "pay",
                "Transferir para um jogador",
                List.of("pay", "pagar"),
                "pay.use",
                "/pay <jogador> <valor>",
                new Pay(this)
        );
        registerCommand(
                "ecoadm",
                "Comandos Economy para Administradores",
                List.of("ecoadm", "ecoadmin"),
                "ecoadm.use",
                "/ecoadm <subcomando> <jogador> <valor>",
                new EconomyAdmin(this)
        );

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(this), this);

        this.users = new CustomConfig("users.yml");
        this.users.get().options().copyDefaults(true);
        this.users.save();

        saveResource("tablist.yml", false);

        this.tablist = new CustomConfig("tablist.yml");
        //this.tablist.get().options().copyDefaults(true);
        //this.tablist.save();

        runnable();
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[SnowCraft] Desativado!", NamedTextColor.RED));

        if (economyManager != null)
            economyManager.saveAll();
    }

    public void registerCommand(String name, String description, List<String> aliases, String permission, String usage, CommandExecutor executor) {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            BukkitCommand command = new BukkitCommand(name) {
                @Override
                public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NotNull [] args) {
                    return executor.onCommand(sender, this, label, args);
                }
            };

            command.setDescription(description);
            command.setAliases(aliases);
            command.setPermission(permission);
            command.setUsage(usage);

            commandMap.register("snowcraft", command);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
