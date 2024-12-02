package espresso.achievement.domain.contracts;

import espresso.achievement.domain.readModels.MediaStorageDetailReadModel;

public interface IMediaQryRepository {

    MediaStorageDetailReadModel getMediaStorageDetail(String key, String userkey);
}
