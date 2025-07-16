package br.com.caiomoizes.snowCraft.factions;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FactionsManager {

    public FactionsManager() {
        //
    }

    public void createFaction(String name, Player owner) {
        //
    }

    public void deleteFaction(String name) {
        //
    }

    public void inviteMember(Player target, String name) {
        //
    }

    public void removeMember(String target) {
        //
    }

    public List<Player> getMembers(String name) {
        List<Player> list = new ArrayList<>();
        return list;
    }

    public void setRegion() {
        //
    }

    public boolean isOwner(Player p) {
        return true;
    }

    public boolean hasFaction(Player p) {
        return true;
    }

    public String getFactionName(Player p) {
        return "";
    }

    public boolean hasMember(String name, String p) {
        return true;
    }

}
