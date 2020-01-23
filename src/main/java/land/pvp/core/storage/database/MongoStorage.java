package land.pvp.core.storage.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Collections;
import java.util.Map;
import land.pvp.core.callback.DocumentCallback;
import org.bson.Document;

public class MongoStorage {
    private final MongoDatabase database;

    public MongoStorage(String host, String password) {
        MongoCredential credential = MongoCredential.createCredential("pvpland", "pvp_land", password.toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(host, 27017))))
                .credential(credential)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("pvp_land");
    }

    public void getOrCreateDocument(String collectionName, Object documentObject, DocumentCallback callback) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document document = new Document("_id", documentObject);

        try (MongoCursor<Document> cursor = collection.find(document).iterator()) {
            if (cursor.hasNext()) {
                callback.call(cursor.next(), true);
            } else {
                collection.insertOne(document);
                callback.call(document, false);
            }
        }
    }

    public MongoCursor<Document> getAllDocuments(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find().iterator();
    }

    public Document getDocumentByFilter(String collectionName, String filter, Object documentObject) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq(filter, documentObject)).first();
    }

    public Document getDocument(String collectionName, Object documentObject) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq("_id", documentObject)).first();
    }

    public void updateDocument(String collectionName, Object documentObject, String key, Object newValue) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.updateOne(Filters.eq(documentObject), Updates.set(key, newValue));
    }

    public void massUpdate(String collectionName, Object documentObject, Map<String, Object> updates) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            collection.updateOne(Filters.eq(documentObject), Updates.set(entry.getKey(), entry.getValue()));
        }
    }
}
