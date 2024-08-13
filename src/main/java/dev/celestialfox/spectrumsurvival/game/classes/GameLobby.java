package dev.celestialfox.spectrumsurvival.game.classes;

import dev.celestialfox.spectrumsurvival.game.managers.GameManager;
import dev.celestialfox.spectrumsurvival.game.phases.Phase;
import dev.celestialfox.spectrumsurvival.utils.Misc;
import dev.celestialfox.spectrumsurvival.utils.classes.Randomized;
import net.kyori.adventure.sound.Sound;
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
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.NamespaceID;
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
    private Task latestTask;
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
            player.playSound(Sound.sound(NamespaceID.from("minecraft:entity.ender_dragon.growl"), Sound.Source.PLAYER, 0.5f, 1.0f));
            MinecraftServer.getSchedulerManager().buildTask(() -> {
                player.playSound(Sound.sound(NamespaceID.from("minecraft:entity.wither.spawn"), Sound.Source.PLAYER, 0.5f, 1.0f));
            }).delay(TaskSchedule.millis(10)).schedule();
            MinecraftServer.getSchedulerManager().buildTask(() -> {
                player.playSound(Sound.sound(NamespaceID.from("minecraft:item.totem.use"), Sound.Source.PLAYER, 0.5f, 1.0f));
            }).delay(TaskSchedule.millis(20)).schedule();
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    public ArrayList<UUID> getPlayers() {
        return players;
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

    public void setInstance(Instance instance) {
        this.instance = instance;
        players.forEach(uuid ->
                Misc.getPlayer(uuid).setInstance(instance));
    }

    public void setInstance(Instance instance, Pos pos) {
        this.instance = instance;
        players.forEach(uuid ->
                Misc.getPlayer(uuid).setInstance(instance, pos));
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

    public void setTask(Task task) {
        latestTask = task;
    }
    public Task getTask() {
        return latestTask;
    }
}
