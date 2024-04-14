package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CATEGORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonFieldsContainKeywordPredicate;
/**
 * Parses input arguments and creates a new FindCommand object.
 * This class is responsible for handling the parsing of input arguments provided by the user
 * for the find command. It supports filtering persons by categories, descriptions, and tags.
 * The input arguments are expected to contain specific prefixes that denote the type of filter
 * to apply.
 */
public class FindCommandParser implements Parser<FindCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * The method expects arguments to contain at least one valid prefix (category/description or tag).
     * Each category must have a corresponding description if specified. Tags can be listed
     * separately and are split by whitespace.
     *
     * @param args the input arguments provided by the user for the find command.
     * @return a FindCommand object ready for execution.
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
            ArgumentTokenizer.tokenize(args, PREFIX_CATEGORY, PREFIX_DESCRIPTION, PREFIX_TAG);
        boolean isTagPresent = arePrefixesPresent(argMultimap, PREFIX_TAG);
        System.out.println(isTagPresent);
        Map<String, List<String>> categoryDescriptionMap = new HashMap<>();
        if (arePrefixesPresent(argMultimap, PREFIX_CATEGORY, PREFIX_DESCRIPTION)) {
            List<String> categories = argMultimap.getAllValues(PREFIX_CATEGORY);
            List<String> descriptions = argMultimap.getAllValues(PREFIX_DESCRIPTION);
            if (categories.size() != descriptions.size()) {
                throw new ParseException("Invalid command format! \n" + FindCommand.COMMAND_FORMAT_CAT_DESC);
            }
            if (categories.contains("") || descriptions.contains("")) {
                throw new ParseException("Invalid command format! \n" + FindCommand.EMPTY_CAT_OR_DESC);
            }
            for (int i = 0; i < categories.size(); i++) {
                categoryDescriptionMap.computeIfAbsent(categories.get(i).trim(), k -> new ArrayList<>())
                    .add(descriptions.get(i).trim());
            }
        }
        Set<String> tags = new HashSet<>();
        List<String> scannedTags = argMultimap.getAllValues(PREFIX_TAG);
        if (isTagPresent) {
            tags.addAll(new HashSet<>(scannedTags));
            if (scannedTags.contains("")) {
                throw new ParseException("Invalid command format! \n" + FindCommand.EMPTY_TAG);
            }
            if (argMultimap.getAllValues(PREFIX_CATEGORY).contains("")
                || argMultimap.getAllValues(PREFIX_DESCRIPTION).contains("")) {
                throw new ParseException("Invalid command format! \n" + FindCommand.EMPTY_CAT_OR_DESC);
            }
            if ((!categoryDescriptionMap.keySet().isEmpty()
                && argMultimap.getAllValues(PREFIX_CATEGORY).size() != scannedTags.size())
                || (!categoryDescriptionMap.values().isEmpty()
                && argMultimap.getAllValues(PREFIX_DESCRIPTION).size() != scannedTags.size())) {
                throw new ParseException("Invalid command format! \n" + FindCommand.COMMAND_CAT_DESC_TAG);
            }
        }
        if (categoryDescriptionMap.isEmpty() && tags.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return new FindCommand(new PersonFieldsContainKeywordPredicate(categoryDescriptionMap, tags));
    }
    /**
     * Checks if the specified prefixes are present in the argument multimap and not empty.
     * This utility method is used to validate that the necessary prefixes for a command are provided
     * by the user and that they contain values.
     *
     * @param argumentMultimap the multimap of arguments parsed from the user's input.
     * @param prefixes the prefixes to check for presence and non-empty values.
     * @return true if all specified prefixes are present and contain non-empty values, otherwise false.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}

