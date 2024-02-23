package eu.simpleg.cuplan.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResultTest {
    @Test
    public void unwrapReturnsExpectedOk() {
        String expectedValue = "abcd1234";
        Result<String, Boolean> okResult = Result.ok(expectedValue);

        String value = okResult.unwrap();

        Assertions.assertEquals(expectedValue, value);
    }

    @Test
    public void unwrapErrorThrowsException() {
        Result<String, Boolean> errResult = Result.err(false);

        Assertions.assertThrows(IllegalStateException.class, errResult::unwrap);
    }

    @Test
    public void unwrapErrReturnsExpectedError() {
        String expectedValue = "error1234";
        Result<Boolean, String> errResult = Result.err(expectedValue);

        String error = errResult.unwrapErr();

        Assertions.assertEquals(expectedValue, error);
    }

    @Test
    public void unwrapErrOkThrowsException() {
        Result<String, Boolean> okResult = Result.ok("example");

        Assertions.assertThrows(IllegalStateException.class, okResult::unwrapErr);
    }

    @Test
    public void isOkOkReturnsTrue() {
        Result<String, Boolean> okResult = Result.ok("hey");

        Assertions.assertTrue(okResult.isOk());
    }

    @Test
    public void isOkErrorReturnsFalse() {
        Result<String, Boolean> errResult = Result.err(false);
        
        Assertions.assertFalse(errResult.isOk());
    }
}
