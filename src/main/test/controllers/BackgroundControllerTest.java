package controllers;

import backend.controllers.BackgroundController;
import backend.dao.BackgroundDAO;
import backend.dao.UserDAO;
import backend.models.Background;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BackgroundControllerTest {

    private BackgroundDAO mockBackgroundDAO;
    private UserDAO mockUserDAO;
    private BackgroundController controller;

    @BeforeEach
    public void setup() {
        mockBackgroundDAO = mock(BackgroundDAO.class);
        mockUserDAO = mock(UserDAO.class);
        controller = new BackgroundController(mockBackgroundDAO, mockUserDAO);
    }

    @Test
    public void testGetAllBackgrounds() {
        List<Background> mockList = Arrays.asList(new Background(), new Background());
        when(mockBackgroundDAO.getAllBackgrounds()).thenReturn(mockList);

        List<Background> result = controller.getAllBackgrounds();
        assertEquals(2, result.size());
        verify(mockBackgroundDAO).getAllBackgrounds();
    }

    @Test
    public void testGetBackgroundsByCategory() {
        String category = "Nature";
        List<Background> mockList = Arrays.asList(new Background("nature.jpg", "Nature"));
        when(mockBackgroundDAO.getBackgroundsByCategory(category)).thenReturn(mockList);

        List<Background> result = controller.getBackgroundsByCategory(category);
        assertEquals(1, result.size());
        assertEquals("Nature", result.get(0).getCategory());
        verify(mockBackgroundDAO).getBackgroundsByCategory(category);
    }

    @Test
    public void testSetUserBackground() {
        when(mockUserDAO.setUserBackground(1, 10)).thenReturn(true);

        boolean result = controller.setUserBackground(1, 10);
        assertTrue(result);
        verify(mockUserDAO).setUserBackground(1, 10);
    }

    @Test
    public void testGetUserBackground() {
        when(mockUserDAO.getUserBackgroundId(1)).thenReturn(99);
        Background mockBackground = new Background(99, "bg.jpg", "Abstract");
        when(mockBackgroundDAO.getBackgroundById(99)).thenReturn(mockBackground);

        Background result = controller.getUserBackground(1);

        assertEquals(99, result.getId());
        assertEquals("bg.jpg", result.getImagePath());
        verify(mockUserDAO).getUserBackgroundId(1);
        verify(mockBackgroundDAO).getBackgroundById(99);
    }
}
