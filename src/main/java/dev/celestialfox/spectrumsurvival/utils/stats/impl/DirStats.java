package dev.celestialfox.spectrumsurvival.utils.stats.impl;

import dev.celestialfox.spectrumsurvival.utils.stats.IStatsStorage;

import java.io.*;
import java.nio.file.*;

public class DirStats implements IStatsStorage {
    private final Path dirPath;

    public DirStats(String dirName) {
        this.dirPath = Paths.get(dirName);
        if (!Files.exists(this.dirPath)) {
            try {
                Files.createDirectories(this.dirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Path getPlayerFilePath(String playerId) {
        return dirPath.resolve(playerId + ".txt");
    }

    private int[] loadPlayerData(String playerId) {
        Path filePath = getPlayerFilePath(playerId);
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                int wins = Integer.parseInt(reader.readLine());
                int losses = Integer.parseInt(reader.readLine());
                return new int[]{wins, losses};
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return new int[]{0, 0};
    }

    private void savePlayerData(String playerId, int wins, int losses) {
        Path filePath = getPlayerFilePath(playerId);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(String.valueOf(wins));
            writer.newLine();
            writer.write(String.valueOf(losses));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveWin(String playerId) {
        int[] data = loadPlayerData(playerId);
        savePlayerData(playerId, data[0] + 1, data[1]);
    }

    @Override
    public void saveLoss(String playerId) {
        int[] data = loadPlayerData(playerId);
        savePlayerData(playerId, data[0], data[1] + 1);
    }

    @Override
    public int getWins(String playerId) {
        return loadPlayerData(playerId)[0];
    }

    @Override
    public int getLosses(String playerId) {
        return loadPlayerData(playerId)[1];
    }
}
