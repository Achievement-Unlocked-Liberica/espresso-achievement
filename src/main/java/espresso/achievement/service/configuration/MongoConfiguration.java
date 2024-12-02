package espresso.achievement.service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;

@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration  {

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    protected String getDatabaseName() {
        return "LibericaDB";
    }

    @Override
    protected MongoClient createMongoClient(MongoClientSettings settings) {
        // TODO Auto-generated method stub
        return super.createMongoClient(settings);
    }
}
