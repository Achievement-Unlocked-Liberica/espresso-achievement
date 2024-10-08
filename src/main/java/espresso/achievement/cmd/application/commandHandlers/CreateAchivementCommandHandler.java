package espresso.achievement.cmd.application.commandHandlers;

public class CreateAchivementCommandHandler {

    public CreateAchivementCommandHandler() {

    }

    public void handle(CreateAchivementCommand command) {

        if (command == null) {
            throw new IllegalArgumentException("The command is null");
        }

    }

}
