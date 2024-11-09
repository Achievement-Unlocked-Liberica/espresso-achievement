package espresso.achievement.cmd.application.commandHandlers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.cmd.domain.contracts.IAchievementCommandHandler;
import espresso.achievement.cmd.domain.commands.CreateAchivementCommand;
import espresso.achievement.cmd.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.cmd.domain.contracts.ISkillRepository;
import espresso.achievement.cmd.domain.contracts.IUserRepository;
import espresso.achievement.cmd.domain.entities.Achievement;
import espresso.achievement.cmd.domain.entities.AchievementMedia;
import espresso.achievement.cmd.domain.entities.Skill;
import espresso.achievement.cmd.domain.entities.UserProfile;
import espresso.achievement.common.response.HandlerResult;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class AchivementCommandHandler implements IAchievementCommandHandler {

    @Autowired
    private IAchievementCmdRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ISkillRepository skillRepository;

    public HandlerResult<Achievement> handle(CreateAchivementCommand command) {

        // ? Validate the inputs and throw an exception if the input is invalid
        if (command == null) {
            throw new IllegalArgumentException("The command is null");
        }

        // Get the profile of the user that is creating the achievemnet
        UserProfile userProfile = userRepository.getUserByKey(command.getUserKey());

        // Get the skills of the achievement to be created
        List<Skill> skills = skillRepository.getSkills(command.getSkills());

        List<AchievementMedia> medias = Arrays.stream(command.getMediaFiles())
                .map(mediaFile -> AchievementMedia.createPreMedia(
                        mediaFile.getOriginalName(),
                        mediaFile.getMimeType(),
                        mediaFile.getEncoding(),
                        mediaFile.getSize()
                ))
                .collect(Collectors.toList());
        
    

        Achievement entity = Achievement.create(
                command.getTitle(),
                command.getDescription(),
                command.getCompletedDate(),
                command.getIsPublic(),
                userProfile,
                skills,
                medias
        );

        System.out.println("Achievement: " + entity.toString());

        achievementRepository.save(entity);

        return HandlerResult.success(null,entity);
    }
}
