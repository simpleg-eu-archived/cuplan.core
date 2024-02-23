package eu.simpleg.cuplan.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionTest {
    @Test
    public void unwrapReturnsValue() {
        String expectedValue = "1234abcd";
        Option<String> optionalValue = Option.some(expectedValue);

        String value = optionalValue.unwrap();

        Assertions.assertEquals(expectedValue, value);
    }

    @Test
    public void unwrapNoneThrowsException() {
        Option<String> none = Option.none();

        Assertions.assertThrows(IllegalStateException.class, none::unwrap);
    }

    @Test
    public void isSomeNoneReturnsFalse() {
        Option<String> none = Option.none();

        Assertions.assertFalse(none.isSome());
    }

    @Test
    public void isSomeSomeReturnsTrue() {
        Option<String> some = Option.some("hey");

        Assertions.assertTrue(some.isSome());
    }

    @Test
    public void isNoneNoneReturnsTrue() {
        Option<String> none = Option.none();

        Assertions.assertTrue(none.isNone());
    }

    @Test
    public void isNoneSomeReturnsFalse() {
        Option<String> some = Option.some("hey");

        Assertions.assertFalse(some.isNone());
    }
}
