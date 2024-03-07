package seedu.address.logic.commands;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Newcmd;

import java.util.List;

/**
 * Changes the newcmd of an existing person in the address book.
 */
public class NewcmdCommand extends Command {

    public static final String COMMAND_WORD = "newcmd";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the newcmd of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing newcmd will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "r/ [NEWCMD]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "r/ Likes to swim.";
    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Newcmd: %2$s";

    private final Index index;
    private final Newcmd newcmd;

    public static final String MESSAGE_ADD_NEWCMD_SUCCESS = "Added newcmd to Person: %1$s";
    public static final String MESSAGE_DELETE_NEWCMD_SUCCESS = "Removed newcmd from Person: %1$s";
    /**
     * @param index of the person in the filtered person list to edit the newcmd
     * @param newcmd of the person to be updated to
     */
    public NewcmdCommand(Index index, Newcmd newcmd) {
        requireAllNonNull(index, newcmd);

        this.index = index;
        this.newcmd = newcmd;
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), newcmd, personToEdit.getTags());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    private String generateSuccessMessage(Person personToEdit) {
        String message = !newcmd.value.isEmpty() ? MESSAGE_ADD_NEWCMD_SUCCESS : MESSAGE_DELETE_NEWCMD_SUCCESS;
        return String.format(message, personToEdit);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NewcmdCommand)) {
            return false;
        }

        NewcmdCommand e = (NewcmdCommand) other;
        return index.equals(e.index)
                && newcmd.equals(e.newcmd);
    }
}