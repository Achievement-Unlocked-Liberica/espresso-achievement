package espresso.achievement.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.entities.KeyGenerator;
import espresso.achievement.domain.readModels.MediaStorageDetailReadModel;
import espresso.achievement.service.configuration.AppConfiguration;

@Component
public class MediaQryBlobDBProvider {

    @Autowired
    private AppConfiguration appConfiguration;

    public MediaStorageDetailReadModel getMediaStorageDetail(String key, String userkey) {

        MediaStorageDetailReadModel entity = new MediaStorageDetailReadModel(
                appConfiguration.getCloudConfig().getStorageUrl(),
                "media-files",
                "/pre-media/" + key + "/uploaded",
                KeyGenerator.generateKeyString(24));
                
        return entity;

    }
}