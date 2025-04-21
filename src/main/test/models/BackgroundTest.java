package models;

import backend.models.Background;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BackgroundTest {

    @Test
    public void testConstructorWithAllFields() {
        Background bg = new Background(1, "images/bg1.jpg", "Nature");

        assertEquals(1, bg.getId());
        assertEquals("images/bg1.jpg", bg.getImagePath());
        assertEquals("Nature", bg.getCategory());
    }

    @Test
    public void testConstructorWithoutId() {
        Background bg = new Background("images/bg2.jpg", "Space");

        assertEquals(0, bg.getId());
        assertEquals("images/bg2.jpg", bg.getImagePath());
        assertEquals("Space", bg.getCategory());
    }

    @Test
    public void testSettersAndGetters() {
        Background bg = new Background();
        bg.setId(10);
        bg.setImagePath("assets/custom.jpg");
        bg.setCategory("Custom");

        assertEquals(10, bg.getId());
        assertEquals("assets/custom.jpg", bg.getImagePath());
        assertEquals("Custom", bg.getCategory());
    }
}
