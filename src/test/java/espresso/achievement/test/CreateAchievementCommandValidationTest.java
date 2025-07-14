package espresso.achievement.test;

import espresso.achievement.domain.commands.CreateAchivementCommand;
import java.util.Date;
import java.util.Set;

/**
 * Simple manual test to verify the validation works correctly
 */
public class CreateAchievementCommandValidationTest {
    
    public static void main(String[] args) {
        System.out.println("Testing CreateAchievementCommand validation...");
        
        // Test 1: Valid skills
        CreateAchivementCommand command1 = new CreateAchivementCommand();
        command1.setUserKey("USER123");
        command1.setTitle("Test Achievement");
        command1.setDescription("Test Description");
        command1.setCompletedDate(new Date());
        command1.setSkills(new String[]{"str", "dex", "int"});
        
        Set<String> errors1 = command1.validate();
        System.out.println("\nTest 1 - Valid skills:");
        System.out.println("Errors: " + errors1);
        System.out.println("Should have no skill-related errors: " + 
            errors1.stream().noneMatch(e -> e.contains("skills[")));
        
        // Test 2: Invalid skills
        CreateAchivementCommand command2 = new CreateAchivementCommand();
        command2.setUserKey("USER123");
        command2.setTitle("Test Achievement");
        command2.setDescription("Test Description");
        command2.setCompletedDate(new Date());
        command2.setSkills(new String[]{"str", "invalid", "dex", "badskill"});
        
        Set<String> errors2 = command2.validate();
        System.out.println("\nTest 2 - Invalid skills:");
        System.out.println("Errors: " + errors2);
        System.out.println("Should have 2 skill errors: " + 
            errors2.stream().filter(e -> e.contains("skills[")).count());
        
        // Test 3: Mixed case valid skills
        CreateAchivementCommand command3 = new CreateAchivementCommand();
        command3.setUserKey("USER123");
        command3.setTitle("Test Achievement");
        command3.setDescription("Test Description");
        command3.setCompletedDate(new Date());
        command3.setSkills(new String[]{"STR", "Dex", "WIS"});
        
        Set<String> errors3 = command3.validate();
        System.out.println("\nTest 3 - Mixed case valid skills:");
        System.out.println("Errors: " + errors3);
        System.out.println("Should have no skill-related errors: " + 
            errors3.stream().noneMatch(e -> e.contains("skills[")));
            
        System.out.println("\nValidation testing completed!");
    }
}
