package controllers;

import backend.controllers.SessionController;
import backend.dao.SessionDAO;
import backend.models.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SessionControllerTest {

    private SessionDAO mockSessionDAO;
    private SessionController sessionController;

    @BeforeEach
    public void setUp() {
        mockSessionDAO = mock(SessionDAO.class);

        // Tạo subclass để inject mock DAO
        sessionController = new SessionController() {
            {
                this.sessionDAO = mockSessionDAO;
            }
        };
    }

    @Test
    public void testCreateNewSession_WhenSessionDoesNotExist() {
        String sessionId = "abc123";
        String userId = "user001";

        when(mockSessionDAO.sessionExists(sessionId)).thenReturn(false);
        when(mockSessionDAO.createSession(any(Session.class))).thenReturn(true);

        boolean result = sessionController.createNewSession(sessionId, userId);

        assertTrue(result);
        verify(mockSessionDAO).sessionExists(sessionId);
        verify(mockSessionDAO).createSession(any(Session.class));
    }

    @Test
    public void testCreateNewSession_WhenSessionAlreadyExists() {
        String sessionId = "abc123";
        String userId = "user001";

        when(mockSessionDAO.sessionExists(sessionId)).thenReturn(true);

        boolean result = sessionController.createNewSession(sessionId, userId);

        assertFalse(result);
        verify(mockSessionDAO).sessionExists(sessionId);
        verify(mockSessionDAO, never()).createSession(any(Session.class));
    }

    @Test
    public void testGetSessionsForUser() {
        String userId = "user001";
        List<Session> mockSessions = Arrays.asList(
                new Session("sess1", userId),
                new Session("sess2", userId)
        );

        when(mockSessionDAO.getAllSessionsByUser(userId)).thenReturn(mockSessions);

        List<Session> result = sessionController.getSessionsForUser(userId);

        assertEquals(2, result.size());
        assertEquals("sess1", result.get(0).getSessionId());
        assertEquals("sess2", result.get(1).getSessionId());
        verify(mockSessionDAO).getAllSessionsByUser(userId);
    }

    @Test
    public void testSessionExists() {
        String sessionId = "sessX";
        when(mockSessionDAO.sessionExists(sessionId)).thenReturn(true);

        boolean exists = sessionController.sessionExists(sessionId);

        assertTrue(exists);
        verify(mockSessionDAO).sessionExists(sessionId);
    }
}
