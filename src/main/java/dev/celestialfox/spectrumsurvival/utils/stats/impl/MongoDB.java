package dev.celestialfox.spectrumsurvival.utils.stats.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import dev.celestialfox.spectrumsurvival.utils.stats.IStatsStorage;
import org.bson.Document;

public class MongoDB implements IStatsStorage {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoDB(String connectionString, String databaseName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = this.mongoClient.getDatabase(databaseName);
    }

    @Override
    public void saveWin(String playerId) {
        MongoCollection<Document> collection = database.getCollection("player_stats");
        Document query = new Document("player_id", playerId);
        Document update = new Document("$inc", new Document("wins", 1));
        collection.updateOne(query, update, new UpdateOptions().upsert(true));
    }

    @Override
    public void saveLoss(String playerId) {
        MongoCollection<Document> collection = database.getCollection("player_stats");
        Document query = new Document("player_id", playerId);
        Document update = new Document("$inc", new Document("losses", 1));
        collection.updateOne(query, update, new UpdateOptions().upsert(true));
    }

    @Override
    public int getWins(String playerId) {
        MongoCollection<Document> collection = database.getCollection("player_stats");
        Document query = new Document("player_id", playerId);
        Document result = collection.find(query).first();
        return result != null ? result.getInteger("wins", 0) : 0;
    }

    @Override
    public int getLosses(String playerId) {
        MongoCollection<Document> collection = database.getCollection("player_stats");
        Document query = new Document("player_id", playerId);
        Document result = collection.find(query).first();
        return result != null ? result.getInteger("losses", 0) : 0;
    }
}
