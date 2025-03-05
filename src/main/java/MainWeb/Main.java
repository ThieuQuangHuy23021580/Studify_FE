package MainWeb;

import javafx.application.Application;
import javafx.stage.Stage;
import View.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            new LoginView().Start(stage);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

}
