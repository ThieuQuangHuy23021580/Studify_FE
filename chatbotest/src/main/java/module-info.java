module com.example.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.gson;
    requires okhttp3;
    requires java.desktop;

    opens com.example.chat to javafx.fxml;
    exports com.example.chat;
}