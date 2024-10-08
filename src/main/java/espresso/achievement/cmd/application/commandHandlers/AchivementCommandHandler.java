package espresso.achievement.cmd.application.commandHandlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.cmd.domain.contracts.IAchievementCommandHandler;
import espresso.achievement.cmd.domain.contracts.IAchievementRepository;
import espresso.achievement.cmd.domain.contracts.ISkillRepository;
import espresso.achievement.cmd.domain.contracts.IUserRepository;
import espresso.achievement.cmd.domain.entities.Achievement;
import espresso.achievement.cmd.domain.entities.Skill;
import espresso.achievement.cmd.domain.entities.UserProfile;

@Service
public class AchivementCommandHandler implements IAchievementCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ISkillRepository skillRepository;

    public AchivementCommandHandler() {

    }

    public void handle(CreateAchivementCommand command) {

        //? Validate the inputs and throw an exception if the input is invalid
        if (command == null) {
            throw new IllegalArgumentException("The command is null");
        }

        //? Get the full entities and value objects from the repository

        // Get the profile of the user that is creating the achievemnet
        UserProfile userProfile = userRepository.getUserByKey(command.getUserKey());

        // Get the skills of the achievement to be created
        List<Skill> skills = skillRepository.getSkills(command.getSkills());

        // Create the achievement
        Achievement achievement = new Achievement(
                command.getTitle(),
                command.getDescription(),
                command.getCompletedDate(),
                command.getIsPublic(),
                userProfile);

        // Set the skills of the achievement
        achievement.setSkills(skills);

        System.out.println("Achievement: " + achievement.toString());

        //? Save the achievement
        achievementRepository.save(achievement);
    }
}
