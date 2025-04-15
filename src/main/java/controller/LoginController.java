package controller;

import backend.controllers.AuthController;
import backend.models.Database;
import backend.models.User;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailAddressField;

    @FXML
    private PasswordField ConfirmPasswordField;

    @FXML
    private Label lb_text1;

    @FXML
    private CheckBox show;

    @FXML
    private Button toSignUpButton;

    @FXML
    private Label lb_text2;

    @FXML
    private Label lb_text3;

    @FXML
    private Button signInButton;

    @FXML
    private Label lb_text4;

    @FXML
    private Button signUpButton;

    @FXML
    private AnchorPane layer1;

    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane layer2;

    @FXML
    private Button toSignInButton;

    @FXML
    private Label lb_text5;

    @FXML
    private ImageView bg;

    @FXML
    private TextField showPassword;

    private AuthController authController;

    Rectangle clip;

    @FXML
    void initialize() {
        authController = new AuthController(); // Khởi tạo AuthController
        showPassword.setVisible(false);
        lb_text2.setVisible(false);
        lb_text5.setVisible(false);
        lb_text4.setVisible(false);
        toSignInButton.setVisible(false);
        signUpButton.setVisible(false);
        ConfirmPasswordField.setVisible(false);
        clip = new Rectangle(407, 800);
        bg.setClip(clip);
        signInButton.setCursor(Cursor.HAND);
        signUpButton.setCursor(Cursor.HAND);
        showPassword.setCursor(Cursor.HAND);
    }

    @FXML
    void signUpButtonPressed(ActionEvent event) {
        String email = emailAddressField.getText();
        String password = passwordField.getText();
        String confirmPassword = ConfirmPasswordField.getText();

        // Kiểm tra dữ liệu đầu vào
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            lb_text4.setText("Vui lòng điền đầy đủ thông tin!");
            lb_text4.setVisible(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            lb_text4.setText("Mật khẩu xác nhận không khớp!");
            lb_text4.setVisible(true);
            return;
        }

        if (!checkStrongPassword(password)) {
            lb_text4.setText("Mật khẩu cần có chữ hoa, chữ thường, số và ký tự đặc biệt!");
            lb_text4.setVisible(true);
            return;
        }

        // Đăng ký người dùng
        try {
            User newUser = new User(email, password);
            boolean success = authController.register(newUser);
            if (success) {
                lb_text4.setText("Đăng ký thành công! Vui lòng đăng nhập.");
                lb_text4.setVisible(true);
                toSignInButtonPressed(event); // Chuyển về màn đăng nhập
            }
        } catch (Exception e) {
            lb_text4.setText("Lỗi: " + e.getMessage());
            lb_text4.setVisible(true);
        }
    }

    @FXML
    void signInButtonPressed() throws IOException {
        String email = emailAddressField.getText();
        String password = passwordField.getText();

        // Kiểm tra dữ liệu đầu vào
        if (email.isEmpty() || password.isEmpty()) {
            lb_text3.setText("Vui lòng điền đầy đủ thông tin!");
            lb_text3.setVisible(true);
            return;
        }

        // Đăng nhập
        try {
            User user = authController.login(email, password);
            lb_text3.setText("Đăng nhập thành công: " + user.getEmail());
            lb_text3.setVisible(true);

            // Chuyển đến màn hình chính sau khi đăng nhập (nếu có)
            // Ví dụ: FXMLLoader.load(getClass().getResource("/path/to/main.fxml"));
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setTitle("Main Application");
            // Tải giao diện chính (nếu có) hoặc hiển thị thông báo
        } catch (Exception e) {
            lb_text3.setText("Lỗi: " + e.getMessage());
            lb_text3.setVisible(true);
        }
    }

    public void showCharacter() {
        if (show.isSelected()) {
            showPassword.setVisible(true);
            showPassword.setText(passwordField.getText());
            passwordField.setVisible(false);
        } else {
            passwordField.setVisible(true);
            passwordField.setText(showPassword.getText());
            showPassword.setVisible(false);
        }
    }

    public static boolean checkStrongPassword(String password) {
        String spCharacter = "!@#$%^&*()_+-={}[]|:;\"'<>,.?/";
        boolean haveUpperCase = false;
        boolean haveLowerCase = false;
        boolean haveSpecial = false;
        boolean haveDigit = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                haveUpperCase = true;
            }
            if (Character.isLowerCase(c)) {
                haveLowerCase = true;
            }
            if (Character.isDigit(c)) {
                haveDigit = true;
            }
            if (spCharacter.indexOf(c) != -1) {
                haveSpecial = true;
            }
        }
        return haveDigit && haveUpperCase && haveLowerCase && haveSpecial;
    }

    public void toSignUpButtonPressed(ActionEvent actionEvent) {
        emailAddressField.setText("");
        passwordField.setText("");
        show.setVisible(false);
        TranslateTransition moveClip = new TranslateTransition(Duration.seconds(0.8), clip);
        moveClip.setToX(793);
        moveClip.play();

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(0.8));
        slide1.setNode(layer1);
        slide1.setToX(793);
        slide1.play();

        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(0.5));
        slide2.setNode(layer2);
        slide2.setToX(-420);
        slide2.play();

        slide2.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double currentTranslateX = layer2.getTranslateX();

            if (currentTranslateX <= -240) {
                lb_text2.setVisible(true);
                lb_text5.setVisible(true);
                lb_text4.setVisible(true);
                toSignInButton.setVisible(true);
                signUpButton.setVisible(true);
                ConfirmPasswordField.setVisible(true);
                lb_text1.setVisible(false);
                lb_text3.setVisible(false);
                toSignUpButton.setVisible(false);
                signInButton.setVisible(false);
            }
        });

        slide1.setOnFinished(event -> {});
        slide2.setOnFinished(event -> {});
    }

    public void toSignInButtonPressed(ActionEvent actionEvent) {
        emailAddressField.setText(null);
        passwordField.setText(null);
        show.setVisible(true);
        TranslateTransition moveClip = new TranslateTransition(Duration.seconds(0.8), clip);
        moveClip.setToX(0);
        moveClip.play();

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(0.8));
        slide1.setNode(layer1);
        slide1.setToX(0);
        slide1.play();

        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(0.5));
        slide2.setNode(layer2);
        slide2.setToX(0);
        slide2.play();

        slide2.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double currentTranslateX = layer2.getTranslateX();

            if (currentTranslateX >= -200) {
                layer2.setTranslateX(0);
                lb_text2.setVisible(false);
                lb_text5.setVisible(false);
                lb_text4.setVisible(false);
                toSignInButton.setVisible(false);
                signUpButton.setVisible(false);
                ConfirmPasswordField.setVisible(false);
                lb_text1.setVisible(true);
                lb_text3.setVisible(true);
                toSignUpButton.setVisible(true);
                signInButton.setVisible(true);
            }
        });

        slide1.setOnFinished(event -> {});
        slide2.setOnFinished(event -> {});
    }
}