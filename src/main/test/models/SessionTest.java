package models;

import backend.models.Session;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    @Test
    public void testConstructorAndGetters() {
        Session session = new Session("abc123", "user001");

        assertEquals("abc123", session.getSessionId());
        assertEquals("user001", session.getUserId());
    }

    @Test
    public void testSetters() {
        Session session = new Session("oldId", "oldUser");
        session.setSessionId("newSessionId");
        session.setUserId("newUserId");

        assertEquals("newSessionId", session.getSessionId());
        assertEquals("newUserId", session.getUserId());
    }
}
