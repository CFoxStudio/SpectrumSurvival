package dev.celestialfox.spectrumsurvival.utils.stats;

public interface IStatsStorage {
    void saveWin(String playerId);
    void saveLoss(String playerId);
    int getWins(String playerId);
    int getLosses(String playerId);
}
