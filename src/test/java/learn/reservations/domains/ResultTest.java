package learn.reservations.domains;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    Result<String> result;

    @BeforeEach
    void setUp() {
        result = new Result<>();
    }


    @Test
    void addMessage() {
        String message = "First Message";
        //
        result.addMessage(message);
        //
        assertEquals(1, result.getMessages().size());
        assertEquals(message, result.getMessages().get(0));
    }


    @Test
    void isSuccessful() {
        assertTrue(result.isSuccessful());
        String message = "First Message";
        //
        result.addMessage(message);
        //
        assertFalse(result.isSuccessful());
    }
}