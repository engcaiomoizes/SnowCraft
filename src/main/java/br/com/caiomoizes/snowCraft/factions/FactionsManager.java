package br.com.caiomoizes.snowCraft.factions;

import br.com.caiomoizes.snowCraft.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionsManager {

    private final CustomConfig config;

    private final Map<String, String> invitedPlayers = new HashMap<>();
    private final Map<String, Region> tempRegions = new HashMap<>();
    private final Map<String, Player> playersInRegion = new HashMap<>();

    public FactionsManager(CustomConfig config) {
        this.config = config;
    }

    public List<String> getFactionsList() {
        return config.get().getStringList("list");
    }

    public void createFaction(String name, Player owner) {
        config.get().getConfigurationSection("factions").createSection(name);
        config.get().getConfigurationSection("factions").getConfigurationSection(name).set("owner", owner.getName());
        config.get().getConfigurationSection("players").set(owner.getName(), name);
        config.get().getConfigurationSection("factions").getConfigurationSection(name).set("members", null);

        List<String> list = config.get().getStringList("list");
        list.add(name);
        config.get().set("list", list);

        save();
    }

    public void setAcronym(String faction, String acronym) {
        config.get().getConfigurationSection("factions").getConfigurationSection(faction).set("acronym", acronym);
        save();
    }

    public String getAcronym(String faction) {
        return config.get().getConfigurationSection("factions").getConfigurationSection(faction).getString("acronym");
    }

    public void deleteFaction(String faction) {
        List<String> members = getMembers(faction);
        for (String member : members)
            config.get().getConfigurationSection("players").set(member, null);

        String owner = getOwner(faction);
        config.get().getConfigurationSection("factions").set(faction, null);
        config.get().getConfigurationSection("players").set(owner, null);

        List<String> list = config.get().getStringList("list");
        list.remove(faction);
        config.get().set("list", list);

        save();
    }

    public void inviteMember(Player target, String name) {
        invitedPlayers.put(target.getName(), name);
    }

    public void leave(Player p, String faction) {
        List<String> members = config.get().getConfigurationSection("factions").getConfigurationSection(faction).getStringList("members");
        members.remove(p.getName());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction).set("members", members);
        config.get().getConfigurationSection("players").set(p.getName(), null);
        save();
    }

    public void removeMember(String faction, Player target) {
        List<String> members = config.get().getConfigurationSection("factions").getConfigurationSection(faction).getStringList("members");
        members.remove(target.getName());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction).set("members", members);
        config.get().getConfigurationSection("players").set(target.getName(), null);
        save();
    }

    public List<String> getMembers(String faction) {
        return config.get().getConfigurationSection("factions").getConfigurationSection(faction).getStringList("members");
    }

    public void addPlayerInRegion(Player p, String faction) {
        playersInRegion.put(faction, p);
    }

    public void removePlayerInRegion(Player p, String faction) {
        playersInRegion.remove(faction, p);
    }

    public Map<String, Player> getPlayersInRegion() {
        return playersInRegion;
    }

    public boolean isInRegion(Player p) {
        return playersInRegion.containsValue(p);
    }

    public String getOwner(String faction) {
        return config.get().getConfigurationSection("factions").getConfigurationSection(faction).getString("owner");
    }

    public void setOwner(Player p, Player target, String faction) {
        config.get().getConfigurationSection("factions").getConfigurationSection(faction).set("owner", target.getName());
        List<String> members = config.get().getConfigurationSection("factions").getConfigurationSection(faction).getStringList("members");
        members.remove(target.getName());
        members.add(p.getName());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction).set("members", members);
        save();
    }

    public boolean isInFaction(Player p, String faction) {
        return config.get().getConfigurationSection("players").getString(p.getName()).equals(faction);
    }

    public void setSpawn(Player p, String faction) {
        if (!config.get().getConfigurationSection("factions").getConfigurationSection(faction).getConfigurationSection("region").contains("spawn"))
            config.get().getConfigurationSection("factions").getConfigurationSection(faction).getConfigurationSection("region").createSection("spawn");

        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").getConfigurationSection("spawn")
                .set("x", p.getLocation().getX());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").getConfigurationSection("spawn")
                .set("y", p.getLocation().getY());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").getConfigurationSection("spawn")
                .set("z", p.getLocation().getZ());

        save();
    }

    public boolean hasSpawn(String faction) {
        return config.get().getConfigurationSection("factions").getConfigurationSection(faction).getConfigurationSection("region").contains("spawn");
    }

    public Location getSpawn(String faction) {
        return new Location(
                getRegion(faction).getWorld(),
                config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                        .getConfigurationSection("region").getConfigurationSection("spawn").getDouble("x"),
                config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                        .getConfigurationSection("region").getConfigurationSection("spawn").getDouble("y"),
                config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                        .getConfigurationSection("region").getConfigurationSection("spawn").getDouble("z")
        );
    }

    public boolean hasRegion(String faction) {
        return config.get().getConfigurationSection("factions").getConfigurationSection(faction).contains("region");
    }

    public Region getRegion(String faction) {
        if (!hasRegion(faction)) return null;
        Region region = new Region();
        region.setCenter(
                new Location(
                        Bukkit.getWorld("world"),
                        config.get().getConfigurationSection("factions").getConfigurationSection(faction).getDouble("region.x"),
                        config.get().getConfigurationSection("factions").getConfigurationSection(faction).getDouble("region.y"),
                        config.get().getConfigurationSection("factions").getConfigurationSection(faction).getDouble("region.z")
                )
        );
        region.setWorld(Bukkit.getWorld(config.get().getConfigurationSection("factions").getConfigurationSection(faction).getString("region.world")));
        region.setRange(config.get().getConfigurationSection("factions").getConfigurationSection(faction).getInt("region.range"));
        return region;
    }

    public void setRegion(Player p, String faction, int range) {
        Region region;
        if (regionIsSetted(faction))
            region = tempRegions.get(faction);
        else
            region = new Region();
        region.setX((int) p.getLocation().getX());
        region.setY((int) p.getLocation().getY());
        region.setZ((int) p.getLocation().getZ());
        region.setWorld(p.getWorld());
        region.setRange(range);

        if (regionIsSetted(faction))
            tempRegions.remove(faction);
        tempRegions.put(faction, region);
    }

    public boolean regionIsSetted(String faction) {
        return tempRegions.containsKey(faction);
    }

    public void confirmRegion(String faction) {
        if (!regionIsSetted(faction)) return;
        Region region = tempRegions.get(faction);
        if (!config.get().getConfigurationSection("factions").getConfigurationSection(faction).contains("region")) {
            config.get().getConfigurationSection("factions").getConfigurationSection(faction).createSection("region");
        }

        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").set("x", region.getX());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").set("y", region.getY());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").set("z", region.getZ());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").set("world", region.getWorld().getName());
        config.get().getConfigurationSection("factions").getConfigurationSection(faction)
                .getConfigurationSection("region").set("range", region.getRange());

        fillRegion(region);

        tempRegions.remove(faction);

        save();
    }

    public void fillRegion(Region region) {
        int x = region.getX();
        int y = region.getY();
        int z = region.getZ();

        int x1 = x - region.getRange();
        int x2 = x + region.getRange();
        int z1 = z - region.getRange();
        int z2 = z + region.getRange();

        World world = region.getWorld();

        Material material = Material.CYAN_WOOL;

        Location location = new Location(world, x, y - 1, z);

        world.getBlockAt(location).setType(material);

        for (int i = x1; i <= x2; i++) {
            location.set(i, y - 1, z1);
            world.getBlockAt(location).setType(material);
            location.set(i, y - 1, z2);
            world.getBlockAt(location).setType(material);
        }
        for (int i = z1; i <= z2; i++) {
            location.set(x1, y - 1, i);
            world.getBlockAt(location).setType(material);
            location.set(x2, y - 1, i);
            world.getBlockAt(location).setType(material);
        }
    }

    public void deleteRegion(String faction) {
        config.get().getConfigurationSection("factions").getConfigurationSection(faction).set("region", null);
        save();
    }

    public boolean isOwner(Player p, String faction) {
        return config.get().getConfigurationSection("factions").getConfigurationSection(faction).getString("owner").equals(p.getName());
    }

    public boolean hasFaction(Player p) {
        return config.get().getConfigurationSection("players").contains(p.getName());
    }

    public boolean existsFaction(String name) {
        return getFactionsList().contains(name);
    }

    public String getFactionName(Player p) {
        if (hasFaction(p))
            return config.get().getConfigurationSection("players").getString(p.getName());
        return "";
    }

    public boolean hasMember(String faction, Player p) {
        return config.get().getConfigurationSection("factions").getConfigurationSection(faction).getConfigurationSection("members").contains(p.getName());
    }

    public void save() {
        config.save();
    }

}
