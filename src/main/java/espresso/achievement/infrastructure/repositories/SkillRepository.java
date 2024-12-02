package espresso.achievement.infrastructure.repositories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.ISkillRepository;
import espresso.achievement.domain.entities.Skill;

@Repository
public class SkillRepository implements ISkillRepository {

    @Override
    public List<Skill> getSkills() {
        return Collections.unmodifiableList(List.of(MockSkillProvider.getSkills()));
    }

    @Override
    public List<Skill> getSkills(String[] skillKeys) {
        List<Skill> skillList = Arrays.stream(MockSkillProvider.getSkills())
                .filter(skill -> Arrays.asList(skillKeys).contains(skill.getAbbreviation()))
                .collect(Collectors.toList());

        skillList.forEach(skill -> skill.cleanForSerialization());

        // clkean the entity for serialization by keeping minimal property values
        return Collections.unmodifiableList(skillList);
    }

    @Override
    public Skill getSkillByKey(String skillKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSkillByKey'");
    }

    // ! Mocking Provider
    private static class MockSkillProvider {
        private static final Skill[] skills = new Skill[] {
                new Skill("Strength", "str", "Represents physical power and brute force"),
                new Skill("Dexterity", "dex", "Represents agility, reflexes, and hand-eye coordination"),
                new Skill("Constitution", "con", "Represents endurance, stamina, and overall health"),
                new Skill("Intelligence", "int", "Represents mental acuity, knowledge, and problem-solving abilities"),
                new Skill("Wisdom", "wis", "Represents intuition, perception, and insight"),
                new Skill("Charisma", "cha", "Represents charm, persuasiveness, and social skills"),
                new Skill("Luck", "luc", "Represents fortune, chance, and serendipity")
        };

        public static Skill[] getSkills() {
            return skills;
        }
    }
}
