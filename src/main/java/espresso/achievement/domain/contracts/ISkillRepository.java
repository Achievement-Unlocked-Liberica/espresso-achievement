package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.Skill;

import java.util.List;

public interface ISkillRepository {

    public List<Skill> getSkills();

    public List<Skill> getSkills(String[] skillKeys);

    public Skill getSkillByKey(String skillKey);
}
