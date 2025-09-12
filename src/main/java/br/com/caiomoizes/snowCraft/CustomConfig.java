package br.com.caiomoizes.snowCraft;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomConfig {

    private File file;

    private FileConfiguration config;

    public CustomConfig(String name) {
        this.file = new File(Bukkit.getServer().getPluginManager().getPlugin("SnowCraft").getDataFolder(), name);
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException ex) {}
        }
        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
    }

    public FileConfiguration get() {
        return this.config;
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException ex) {
            System.out.println("Couldn't save file.");
        }
    }

    public void reload() {
        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
    }

}
