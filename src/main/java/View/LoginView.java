package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class LoginView {
    public void Start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/FXML/DashBoardView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
