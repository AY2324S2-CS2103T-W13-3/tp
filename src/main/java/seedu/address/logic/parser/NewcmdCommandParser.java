package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.NewcmdCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Newcmd;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWCMD;

public class NewcmdCommandParser implements Parser {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code NewcmdCommand}
     * and returns a {@code NewcmdCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public NewcmdCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NEWCMD);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    NewcmdCommand.MESSAGE_USAGE), ive);
        }

        Newcmd newcmd = new Newcmd(argMultimap.getValue(PREFIX_NEWCMD).orElse(""));

        return new NewcmdCommand(index, newcmd);
    }
}