package br.com.caiomoizes.snowCraft.economy;

import br.com.caiomoizes.snowCraft.CustomConfig;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final HashMap<UUID, Double> cache = new HashMap<>();
    private final CustomConfig config;

    private final double saldoInicial;

    public EconomyManager(CustomConfig config, double saldoInicial) {
        this.config = config;
        this.saldoInicial = saldoInicial;
        loadAll();
    }

    public double getBalance(Player p) {
        return cache.getOrDefault(p.getUniqueId(), 0.0);
    }

    public void setBalance(Player p, double amount) {
        cache.put(p.getUniqueId(), amount);
    }

    public boolean deposit(Player p, double amount) {
        if (amount < 0) return false;
        setBalance(p, getBalance(p) + amount);
        return true;
    }

    public boolean withdraw(Player p, double amount) {
        if (getBalance(p) >= amount) {
            setBalance(p, getBalance(p) - amount);
            return true;
        }
        return false;
    }

    public boolean transfer(Player from, Player to, double amount) {
        if (withdraw(from, amount)) {
            deposit(to, amount);
            return true;
        }
        return false;
    }

    public boolean exists(Player p) {
        return cache.containsKey(p.getUniqueId());
    }

    public void creditInitialValue(Player p) {
        if (!exists(p))
            setBalance(p, saldoInicial);
    }

    public void saveAll() {
        for (Map.Entry<UUID, Double> entry : cache.entrySet())
            config.get().set("saldo." + entry.getKey(), entry.getValue());
        config.save();
    }

    private void loadAll() {
        if (config.get().contains("saldo")) {
            for (String key : config.get().getConfigurationSection("saldo").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                double saldo = config.get().getDouble("saldo." + key);
                cache.put(uuid, saldo);
            }
        }
    }

    public void reload() {
        cache.clear();
        config.reload();
        loadAll();
    }

}
