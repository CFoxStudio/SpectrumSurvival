package dev.cfox.gamejam.game.classes;

import dev.cfox.gamejam.game.managers.GameManager;
import dev.cfox.gamejam.game.phases.Phase;
import dev.cfox.gamejam.utils.Misc;
import dev.cfox.gamejam.utils.classes.Randomized;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.UUID;

public class GameLobby {
    private final Logger logger = LoggerFactory.getLogger(GameLobby.class);
    private final ArrayList<UUID> players = new ArrayList<>();
    Team team = MinecraftServer.getTeamManager().createTeam("team_" + getName());
    private final ArrayList<UUID> eliminated = new ArrayList<>();
    private Phase phase;
    private Instance instance;
    private String name = "";

    public void setPlayers(ArrayList<UUID> playerList) {
        players.addAll(playerList);
        for (UUID uuid : players) {
            team.addMember(Misc.getPlayer(uuid).getUsername());
        }
        team.updateCollisionRule(TeamsPacket.CollisionRule.NEVER);
    }

    public void eliminate(Player player) {
        if (!eliminated.contains(player)) {
            eliminated.add(player.getUuid());
            logger.debug("Player " + player.getUsername() + " got eliminated (GameLobby: " + name + ")");
            sendMessage(Randomized.elimination(player));
            player.sendMessage(Component.text("You've got eliminated!", NamedTextColor.RED, TextDecoration.BOLD));
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    public ArrayList<UUID> getAllPlayers() {
        return players;
    }

    public ArrayList<UUID> getPlayers() {
        ArrayList<UUID> playersInGame = new ArrayList<>(players);
        playersInGame.removeIf(uuid -> getEliminated().contains(uuid));
        return playersInGame;
    }

    public ArrayList<UUID> getEliminated() {
        return eliminated;
    }

    public void setPhase(Phase newPhase) {
        phase = newPhase;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void genName() {
        UUID uuid = UUID.randomUUID();
        if (!GameManager.gameLobbies.containsKey(uuid.toString())) {
            name = uuid.toString();
        } else {
            genName();
        }
    }

    public void setInstance(Instance instance, Pos pos) {
        this.instance = instance;
        players.forEach(uuid ->
                Misc.getPlayer(uuid).setInstance(instance).thenRun(() -> Misc.getPlayer(uuid).teleport(pos)));
    }

    public Instance getInstance() {
        return instance;
    }

    public void teleport(Pos pos) {
        players.forEach(uuid -> Misc.getPlayer(uuid).teleport(pos));
    }

    public void sendMessage(Component component) {
        players.forEach(uuid -> Misc.getPlayer(uuid).sendMessage(component));
    }
}
