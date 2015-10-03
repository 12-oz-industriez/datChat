package datchat.dao;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;

public class MongoClientHolder {

    public static final MongoClient MONGO_CLIENT = MongoClients.create();

}
