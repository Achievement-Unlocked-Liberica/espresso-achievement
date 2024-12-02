package espresso.achievement.application.queryHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.contracts.IAchievementMediaQueryHandler;
import espresso.achievement.domain.contracts.IMediaQryRepository;
import espresso.achievement.domain.entities.KeyGenerator;
import espresso.achievement.domain.entities.PreMedia;
import espresso.achievement.domain.queries.GetAchievementMediaStorageQuery;
import espresso.achievement.domain.readModels.MediaStorageDetailReadModel;
import espresso.achievement.service.configuration.AppConfiguration;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class AchievementMediaQueryHandler implements IAchievementMediaQueryHandler {

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private IMediaQryRepository mediaRepository;

    public HandlerResult<MediaStorageDetailReadModel> handle(GetAchievementMediaStorageQuery query) {
        // ? Validate the inputs and throw an exception if the input is invalid
        if (query == null) {
            throw new IllegalArgumentException("The query is null");
        }

        MediaStorageDetailReadModel entity = mediaRepository.getMediaStorageDetail(
                query.getKey(),
                query.getUserKey());

        return entity != null
                ? HandlerResult.success(null, entity)
                : HandlerResult.empty();
    }
}
