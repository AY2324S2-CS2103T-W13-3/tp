package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CATEGORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCategoryCommand;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Entry;
import seedu.address.model.person.EntryList;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        boolean hasEntry = true;
        EntryList entryList = new EntryList();

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG, PREFIX_CATEGORY, PREFIX_DESCRIPTION);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        assert argMultimap.getValue(PREFIX_NAME).isPresent() : "Name prefix is present";
        if (argMultimap.getValue(PREFIX_NAME).get().isEmpty()) {
            throw new ParseException("Name cannot be empty!");
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_CATEGORY) && !arePrefixesPresent(argMultimap, PREFIX_DESCRIPTION)) {
            hasEntry = false;
        } else if (!arePrefixesPresent(argMultimap, PREFIX_CATEGORY)
                && arePrefixesPresent(argMultimap, PREFIX_DESCRIPTION)) {
            throw new ParseException("Category cannot be empty!");
        } else if (arePrefixesPresent(argMultimap, PREFIX_CATEGORY)
                && !arePrefixesPresent(argMultimap, PREFIX_DESCRIPTION)) {
            throw new ParseException("Description cannot be empty!");
        } else {
            List<String> categories = argMultimap.getAllValues(PREFIX_CATEGORY);
            List<String> descriptions = argMultimap.getAllValues(PREFIX_DESCRIPTION);
            List<String> lowercaseCategories = new ArrayList<>();

            for (int i = 0; i < categories.size(); i++) {
                String cat = categories.get(i);
                lowercaseCategories.add(cat.toLowerCase().trim());
            }

            if (hasDuplicates(lowercaseCategories)) {
                throw new ParseException(AddCategoryCommand.MESSAGE_DUPLICATE_CATEGORY);
            }

            if (categories.size() == 0 || descriptions.size() == 0) {
                throw new ParseException(AddCategoryCommand.ENTRY_NOT_ADDED);
            }

            if (categories.size() != descriptions.size()) {
                throw new ParseException(AddCategoryCommand.DIFFERENT_NUMBER_CATEGORIES_DESCRIPTIONS);
            }

            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).equalsIgnoreCase("Name")) {
                    throw new ParseException("Name already exists!");
                }
            }

            entryList = ParserUtil.parseEntries(categories, descriptions);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME);
        assert argMultimap.getValue(PREFIX_NAME).isPresent() : "Name prefix is present";
        Entry name = ParserUtil.parse("Name", argMultimap.getValue(PREFIX_NAME).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person = new Person(name, tagList);
        if (hasEntry) {
            return new AddCommand(person, entryList);
        } else {
            return new AddCommand(person, null);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }


    /**
     * Checks whether a given list contains any duplicate elements.
     *
     * @param list The list to be checked for duplicates.
     * @return {@code true} if the list contains duplicates, {@code false} otherwise.
     * @throws NullPointerException if the specified list is null.
     */
    private boolean hasDuplicates(List<String> list) {
        Set<String> set = new HashSet<>();
        for (String element : list) {
            if (set.contains(element)) {
                return true;
            }
            set.add(element);
        }
        return false;
    }
}
