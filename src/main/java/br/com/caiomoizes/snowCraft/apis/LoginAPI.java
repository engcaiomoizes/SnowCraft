package br.com.caiomoizes.snowCraft.apis;

import br.com.caiomoizes.snowCraft.SnowCraft;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LoginAPI {

    public static SnowCraft snowcraft = SnowCraft.getInstance();

    private static List<Player> jogadoresLogados = new ArrayList<>();

    public static void disableLogin() {
        snowcraft.getConfig().set("require-login", false);
    }

    public static void enableLogin() {
        snowcraft.getConfig().set("require-login", true);
    }

    public static boolean requireLogin() {
        return snowcraft.getConfig().getBoolean("require-login");
    }

    public static boolean estaLogado(Player p) {
        return jogadoresLogados.contains(p);
    }

    public static void logar(Player p) {
        jogadoresLogados.add(p);
    }

    public static void deslogar(Player p) {
        jogadoresLogados.remove(p);
    }

    public static void registrar(Player p, String senha) {
        snowcraft.getUsers().get().set(p.getName().toLowerCase(), senha);
        snowcraft.getUsers().save();
    }

    public static void desregistrar(Player p) {
        snowcraft.getUsers().get().set(p.getName().toLowerCase(), null);
        snowcraft.getUsers().save();
    }

    public static void trocarSenha(Player p, String novaSenha) {
        snowcraft.getUsers().get().set(p.getName().toLowerCase(), novaSenha);
        snowcraft.getUsers().save();
    }

    public static String getSenha(Player p) {
        return snowcraft.getUsers().get().getString(p.getName().toLowerCase());
    }

    public static boolean estaRegistrado(Player p) {
        return snowcraft.getUsers().get().contains(p.getName().toLowerCase());
    }

    public static List<Player> getJogadoresLogados() {
        return jogadoresLogados;
    }

    public static void setJogadoresLogados(List<Player> jogadoresLogados) {
        LoginAPI.jogadoresLogados = jogadoresLogados;
    }

}
