package espresso.achievement.cmd.infrastructure.repositories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import espresso.achievement.cmd.domain.contracts.ISkillRepository;
import espresso.achievement.cmd.domain.entities.Skill;

@Repository
public class SkillRepository implements ISkillRepository {

    @Override
    public List<Skill> getSkills() {
        return Collections.unmodifiableList(List.of(MockSkillProvider.getSkills()));
    }

    @Override
    public List<Skill>  getSkills(String[] skillKeys) {
        return Arrays.stream(MockSkillProvider.getSkills())
                        .filter(skill -> Arrays.asList(skillKeys).contains(skill.getKey()))
                        .collect(Collectors.toList());
    }

    @Override
    public Skill getSkillByKey(String skillKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSkillByKey'");
    }




    // ! Mocking Provider
    private static class MockSkillProvider {
        private static final Skill[] skills = new Skill[] {
            new Skill("Strength", "STR", "Represents physical power and brute force"),
            new Skill("Dexterity", "DEX", "Represents agility, reflexes, and hand-eye coordination"),
            new Skill("Constitution", "CON", "Represents endurance, stamina, and overall health"),
            new Skill("Intelligence", "INT", "Represents mental acuity, knowledge, and problem-solving abilities"),
            new Skill("Wisdom", "WIS", "Represents intuition, perception, and insight"),
            new Skill("Charisma", "CHA", "Represents charm, persuasiveness, and social skills"),
            new Skill("Luck", "LUC", "Represents fortune, chance, and serendipity")
        };

        public static Skill[] getSkills() {
            return skills;
        }
    }
}
