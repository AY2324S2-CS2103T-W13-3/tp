package seedu.address.logic.commands;
import seedu.address.model.Model;

public class NothingCommand extends Command{

    public CommandResult execute(Model model) {
        return null;
    }
}
