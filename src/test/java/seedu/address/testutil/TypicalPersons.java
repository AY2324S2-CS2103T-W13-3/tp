package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Entry;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 */
public class TypicalPersons {
    public static final Entry ALICE_ENTRY = new Entry("Clan", "Rain");
    public static final Entry BOB_ENTRY = new Entry("Game1", "Leader");
    public static final Entry CHARLIE_ENTRY = new Entry("LOL", "Beast");
    public static final Set<Tag> EMPTY_TAGS = new HashSet<>();
    private static final Set<Tag> tags = new HashSet<>(Arrays.asList(new Tag("friend")));
    public static final Person ALICE = new Person(ALICE_ENTRY, tags);
    public static final Person BOB = new Person(BOB_ENTRY, EMPTY_TAGS);
    public static final Person CHARLIE = new Person(CHARLIE_ENTRY, EMPTY_TAGS);
    private TypicalPersons() {}
    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        return ab;
    }
    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BOB, CHARLIE));
    }
}
