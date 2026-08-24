package com.kalavit.javulna;

import com.kalavit.javulna.exception.InvalidOrderException;
import org.junit.Test;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InvalidOrderExceptionTest {

    @Test
    public void emptyConstructor() {
        InvalidOrderException ex = new InvalidOrderException();
        assertNotNull(ex);
    }

    @Test
    public void messageConstructor() {
        InvalidOrderException ex = new InvalidOrderException("bad order");
        assertEquals("bad order", ex.getMessage());
    }

    @Test
    public void causeConstructor() {
        RuntimeException cause = new RuntimeException("root cause");
        InvalidOrderException ex = new InvalidOrderException(cause);
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void messageAndCauseConstructor() {
        RuntimeException cause = new RuntimeException("root cause");
        InvalidOrderException ex = new InvalidOrderException("bad order", cause);
        assertEquals("bad order", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void fullConstructor() {
        RuntimeException cause = new RuntimeException("root cause");
        InvalidOrderException ex = new InvalidOrderException("bad order", cause, false, false);
        assertEquals("bad order", ex.getMessage());
    }

    @Test
    public void errorListConstructor() {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("order", "quantity must be positive"));
        errors.add(new ObjectError("order", "price missing"));

        InvalidOrderException ex = new InvalidOrderException(errors);

        assertTrue(ex.getMessage().contains("Failed to validate order"));
        assertTrue(ex.getMessage().contains("quantity must be positive"));
    }
}