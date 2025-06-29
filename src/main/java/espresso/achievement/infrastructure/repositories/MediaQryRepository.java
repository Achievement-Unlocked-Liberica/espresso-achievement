package espresso.achievement.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.IMediaQryRepository;
import espresso.achievement.domain.readModels.MediaStorageDetailReadModel;

@Component
public class MediaQryRepository implements IMediaQryRepository {

@Autowired
private MediaQryBlobDBProvider mediaQryBlobDBProvider;

    public MediaStorageDetailReadModel getMediaStorageDetail(String key, String userkey) {

        MediaStorageDetailReadModel entity = mediaQryBlobDBProvider.getMediaStorageDetail(key, userkey);

        return entity;
    }
}
