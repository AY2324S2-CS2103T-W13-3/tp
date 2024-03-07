package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's newcmd in the address book.
 * Guarantees: immutable; is always valid
 */
public class Newcmd {
    public final String value;

    public Newcmd(String newcmd) {
        requireNonNull(newcmd);
        value = newcmd;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Newcmd // instanceof handles nulls
                && value.equals(((Newcmd) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}