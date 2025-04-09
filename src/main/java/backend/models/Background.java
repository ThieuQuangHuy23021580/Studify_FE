package backend.models;

public class Background {
    private int id;
    private String imagePath;
    private String category;

    public Background(int id, String imagePath, String category) {
        this.id = id;
        this.imagePath = imagePath;
        this.category = category;
    }

    public Background(String imagePath, String category) {
        this.imagePath = imagePath;
        this.category = category;
    }

    public Background() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
